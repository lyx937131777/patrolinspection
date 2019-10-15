package com.example.patrolinspection.psam;

import android.content.Context;
import android.nfc.tech.IsoDep;
import android.util.Log;

import com.odm.OdmUtil;
import com.odm.tools.Tools;
import com.odm.tty.TtyDevice;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class PsamUtil
{
    private static final String TAG = "PsamUtil";
    private boolean DEBUG = true;

    private TtyDevice mTtyDevice;
    //private MiscDevice mMiscDevice;
    private Context context;

    private BaoAnBasicInfo baoanBasicInfo;//保安基本信息
    private ExpendInfo expendinfo;//拓展信息
    private TrackRecord trackinfo;//从业记录信息

    private static final int PIN_POWER = 0;
    private static final int PIN_DOWNLOAD = 6;

    public PsamUtil(Context c){
        //mMiscDevice = new MiscDevice("/dev/psam",'M');
        mTtyDevice = new TtyDevice("/dev/ttySC1");
        context = c;
    }

    //A2000L
    public int open(){
        if(DEBUG) Log.d(TAG, "open ");
        //Psam上电
        int ret = -1;
        Tools.system("echo 0 > /sys/class/misc/psam/goto_download");
        OdmUtil.delayms(100);
        Tools.system("echo 1 > /sys/class/misc/psam/power_en");
        mTtyDevice.ttyOpen();
        ret = mTtyDevice.ttyInit(9600, 8, 1, 0, false, false);
        /*
        mMiscDevice.setPinLow(PIN_DOWNLOAD);
        OdmUtil.delayms(100);
        ret = mMiscDevice.setPinHigh(PIN_POWER);

        if (ret == 0) {
            mTtyDevice.ttyOpen();
            ret = mTtyDevice.ttyInit(9600, 8, 1, 0, false, false);
        }
        */
        return ret;
    }

    public void close(){
        if(DEBUG) Log.d(TAG, "close ");
        //关闭串口
        mTtyDevice.ttyClose();
        //Psam下电
        //mMiscDevice.setPinHigh(PIN_DOWNLOAD);
        //mMiscDevice.setPinLow(PIN_POWER);
        Tools.system("echo 1 > /sys/class/misc/psam/goto_download");
        Tools.system("echo 0 > /sys/class/misc/psam/power_en");
    }

    /**
     * 保安卡安全认证
     * @param isodep
     * @return 0:非isodep卡，-1：无PSAM卡，-2：无效卡，-3：未读到保安卡id
     */
    public int sercurityAuth(IsoDep isodep){
        int st = 0;
        byte[] psam_suiji = null;
        byte[] iccard_suiji = null;
        byte[] psam_response = null;
        //3 读取保安卡唯一序列号
        try {
            //IsoDep isodep = IsoDep.get(tagFromIntent);
            //isodep.connect();
            byte[] cardid = readICID(isodep);
            if(cardid == null){
                st=-3;
                return st;
            }
            //Log.e(TAG,"cardid = " + CommonUtil.bytesToHexString(cardid));
            resetPsam();
            //4 进入PSAM应用
            boolean topsam = toPSAM();
            //Log.e(TAG, "toPSAM="+topsam);
            //5 对PSAM取随机数
            psam_suiji = getPSAMsuiji();
            //Log.e(TAG, "suiji="+CommonUtil.bytesToHexString(psam_suiji));
            if(psam_suiji != null){
                //6 保安卡内部认证
                iccard_suiji = internalICcardAuth(isodep,psam_suiji);
                //Log.e(TAG, "iccard_suiji="+CommonUtil.bytesToHexString(iccard_suiji));
                if(iccard_suiji != null){
                    //7 PSAM卡内部认证
                    byte[] retdata = internalPSAM(iccard_suiji,cardid);
                    if(retdata != null){
                        //返回状态为9000，则将前面的数据发给保安卡做外部认证
                        if(externalIC(isodep,retdata)){
                            st = 1;
                        }
                    }else{
                        //如果返回状态不是9000 尝试取 PSAM卡响应
                        psam_response = getResponse();
                        if(psam_response != null){
                            //9 保安卡外部认证
                            if(externalIC(isodep,psam_response)){
                                st = 1;
                            }
                        } else {
                            st = -2;//无效卡
                        }
                    }
                }
            }else{
                st = -1;//无卡
            }
            Log.d(TAG, "st="+st);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    //读取保安卡唯一序列号
    private byte[] readICID(IsoDep isoDeps){
        if(DEBUG) Log.d(TAG, "readICID 读取保安卡唯一序列号");
        byte[] icid = null;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("00B0850008");
            if(DEBUG) Log.d(TAG, "cmd =" + CommonUtil.bytesToHexString(cmd));
            byte[] resp = isoDeps.transceive(cmd);
            if(DEBUG) Log.d(TAG, "ic resp =" + CommonUtil.bytesToHexString(resp));
            if(resp != null && resp.length > 8){
                icid = new byte[resp.length - 2];
                System.arraycopy(resp, 0, icid, 0, resp.length - 2);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return icid;
    }

    //4 进入PSAM应用
    //psam上电
    //给psam发送 01 00A4000002 1000
    //返回值开头为90，61，6C为true
    private boolean toPSAM(){
        if(DEBUG) Log.d(TAG, "toPSAM 进入PSAM应用");
        boolean flag = false;
        try{
            byte[] resp = sendDataToPsam("00A40000021000");
            if(resp != null && resp.length > 3){
                if(resp[resp.length-2] == (byte)0x90){
                    flag = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    //5 对PSAM取随机数
    //给psam发送010084000004
    //拿psam随机数（ex：73 fd f5 fb 90 00）
    //无卡id或无随机数 st=-1 无卡
    private byte[] getPSAMsuiji(){
        if(DEBUG) Log.d(TAG, "getPSAMsuiji 对PSAM取随机数");
        byte[] psamsuiji = null;
        try {
            byte[] resp = sendDataToPsam("0084000004");
            //resp=00007e4908fb9000
            if(resp != null){
                if(resp.length >= 6){
                    psamsuiji = new byte[4];
                    System.arraycopy(resp, 2, psamsuiji, 0, 4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return psamsuiji;
    }

    //6 保安卡内部认证
    //用psam随机数发送给icard拿icard随机数
    //8088020008 psam随机数 00000000
    //获取返回值 >= 12 取4位 ic卡随机数
    //icsuiji = new byte[4];
    //System.arraycopy(resp, 8, icsuiji, 0, 4);
    private byte[] internalICcardAuth(IsoDep isoDeps,byte[] psamsuiji){
        if(DEBUG) Log.d(TAG, "internalICcardAuth 保安卡内部认证");
        byte[] icsuiji = null;
        try {
            byte[] cmd = CommonUtil.hexStringToBytes("8088020008"+CommonUtil.bytesToHexString(psamsuiji)+"00000000");
            byte[] resp = isoDeps.transceive(cmd);
            if(resp != null){
                if(resp.length  >= 12){
                    icsuiji = new byte[4];
                    System.arraycopy(resp, 8, icsuiji, 0, 4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icsuiji;
    }

    //7 PSAM卡内部认证
    //用ic卡随机数发给psam
    //01 0088000010 ic卡随机数 00000000 ic卡id
    //返回值开头为90，61，6C为true，false st=-2 无效卡
    private byte[] internalPSAM(byte[] icsuiji, byte[] cardid){
        if(DEBUG) Log.d(TAG, "internalPSAM PSAM卡内部认证");
        byte[] retdata = null;
        try{
            byte[] resp = sendDataToPsam("0088000010"+CommonUtil.bytesToHexString(icsuiji)+"00000000"+CommonUtil.bytesToHexString(cardid));
            //=0000703df199f4f40d979000

            if(resp != null && resp.length >= 12){
                if(resp[resp.length-2] == (byte)0x90){
                    retdata = new byte[8];
                    System.arraycopy(resp, 2, retdata, 0, 8);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return retdata;
    }

    //8 取 PSAM卡响应
    //给psam发送 01 00C0000008
    //返回值>8
    //data = new byte[resp.length - 2];
    //System.arraycopy(resp, 0, data, 0, resp.length - 2);
    private byte[] getResponse(){
        if(DEBUG) Log.d(TAG, "getResponse 取 PSAM卡响应");
        byte[] data = null;
        try {
            byte[] resp = sendDataToPsam("00C0000008");
            //=00006f00
            if(resp != null && resp.length > 8){
                data = new byte[resp.length - 2];
                System.arraycopy(resp, 0, data, 0, resp.length - 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //9 保安卡外部认证
    //将psam卡响应发给ic卡8082030008 响应数据
    //返回值开头为90，61，6C为true st=1
    private boolean externalIC(IsoDep isoDeps,byte[] responseData){
        if(DEBUG) Log.d(TAG, "externalIC 保安卡外部认证");
        boolean flag = false;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("8082030008"+CommonUtil.bytesToHexString(responseData));
            if(DEBUG) Log.d(TAG, "cmd =" + CommonUtil.bytesToHexString(cmd));
            byte[] resp = isoDeps.transceive(cmd);
            if(DEBUG) Log.d(TAG, "resp =" + CommonUtil.bytesToHexString(resp));
            if(resp != null && resp.length > 0){
                if(resp[0] == (byte)0x90 || resp[0] == (byte)0x61 || resp[0] == (byte)0x6C){
                    flag = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public byte[] sendDataToIC(IsoDep isoDeps,byte[] data){
        byte[] resp = null;
        try{
            if(DEBUG) Log.d(TAG, "data =" + CommonUtil.bytesToHexString(data));
            resp = isoDeps.transceive(data);
            if(DEBUG) Log.d(TAG, "resp =" + CommonUtil.bytesToHexString(resp));

        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }

    //psam复位
    public boolean resetPsam(){
        if(DEBUG) Log.d(TAG, "resetPsam ");
        boolean flag = false;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("02000201610361");
            if(DEBUG) Log.d(TAG, "cmd =" + CommonUtil.bytesToHexString(cmd));
            mTtyDevice.ttyWrite(cmd);
            OdmUtil.delayms(500);
            byte[] temp = new byte[50];
            int ret = mTtyDevice.ttyRead(temp);
            byte[] resp = new byte[ret];
            System.arraycopy(temp, 0, resp, 0, ret);
            //byte[] resp = mContactCard.transceive(cmd);
            if(DEBUG) Log.d(TAG, "resp =" + CommonUtil.bytesToHexString(resp));
            if(resp != null && resp.length > 0){
                if(resp[0] == (byte)0x90 || resp[0] == (byte)0x61 || resp[0] == (byte)0x6C){
                    flag = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public byte[] sendDataToPsam(String data){
        try {
            if(DEBUG) Log.d(TAG, "send data =" + data);
            byte[] cmd = getApduCmdPacket(CommonUtil.hexStringToBytes(data));
            if(DEBUG) Log.d(TAG, "cmd =" + CommonUtil.bytesToHexString(cmd));
            mTtyDevice.ttyWrite(cmd);
            OdmUtil.delayms(500);
            byte[] temp = new byte[1024];
            int ret = mTtyDevice.ttyRead(temp);
            byte[] retdata = new byte[ret];
            System.arraycopy(temp, 0, retdata, 0, ret);
            if(DEBUG) Log.d(TAG, "retdata =" + CommonUtil.bytesToHexString(retdata));
            byte[] resp = getResponseData(retdata);
            if(DEBUG) Log.d(TAG, "psam resp =" + CommonUtil.bytesToHexString(resp));
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //读取基本信息
    public byte[] getBasicInfo(IsoDep isoDeps){
        if(DEBUG) Log.d(TAG, "getBasicInfo 读取基本信息");
        byte[] data = null;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("00A4000002" + "1001");
            byte[] resp = isoDeps.transceive(cmd);
            if(resp != null){
                byte[] cmd2 = CommonUtil.hexStringToBytes("00B0" + "9500CC");
                data = isoDeps.transceive(cmd2);
                if(data.length < 100){
                    data = null;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //读取保安员监管信息
    public byte[] getJGInfo(IsoDep isoDeps){
        if(DEBUG) Log.d(TAG, "getJGInfo 读取保安员监管信息");
        byte[] data = null;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("00A4000002" + "1002");
            byte[] resp = isoDeps.transceive(cmd);
            if(resp != null){
                byte[] cmd2 = CommonUtil.hexStringToBytes("00B0" + "980057");
                data = isoDeps.transceive(cmd2);
                if(data.length < 80){
                    data = null;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //读取保安员从业信息
    public byte[] getTrackInfo(IsoDep isoDeps){
        if(DEBUG) Log.d(TAG, "getTrackInfo 读取保安员从业信息");
        byte[] data = null;
        try{
            byte[] cmd = CommonUtil.hexStringToBytes("00A4000002" + "1003");
            byte[] resp = isoDeps.transceive(cmd);
            if(resp != null){
                byte[] cmd2 = CommonUtil.hexStringToBytes("00B0" + "9900E9");
                data = isoDeps.transceive(cmd2);
                if(data.length < 100){
                    data = null;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //读取照片信息
    public byte[] getPhotoInfo(IsoDep isoDeps){
        if(DEBUG) Log.d(TAG, "getPhotoInfo 读取照片信息");
        byte[] photoData = new byte[2049];
        byte[] jp2Data = new byte[2048];
        try {
            byte[] cmd1 = CommonUtil.hexStringToBytes("00A4000002" + "1001");
            byte[] resp0 = isoDeps.transceive(cmd1);
            if(resp0 != null){
                byte[] cmd2 = CommonUtil.hexStringToBytes("00A4000002" + "EF16");
                byte[] resp1 = isoDeps.transceive(cmd2);
                if(resp1 != null){
                    //每次读取128个字节
                    int readLen = 0;
                    String point = "";
                    String len = Integer.toHexString(128);
                    byte[] resp = null;
                    for(int i = 0 ; i < 17; i++){
                        byte[] cmd = null;
                        if(i == 16){
                            cmd = CommonUtil.hexStringToBytes("00B0" + "080001");
                            byte[] resp2 = isoDeps.transceive(cmd);
                        }else{

                            point = Integer.toHexString(readLen);
                            if(point.length() == 1){
                                cmd = CommonUtil.hexStringToBytes("00B0" + "000" + point + len);
                            }else if(point.length() == 2){
                                cmd = CommonUtil.hexStringToBytes("00B0" + "00" + point + len);
                            }else{
                                cmd = CommonUtil.hexStringToBytes("00B0" + "0" + point + len);
                            }
                            resp = isoDeps.transceive(cmd);
                            if(resp != null & resp.length >= 128){
                                System.arraycopy(resp, 0, photoData, readLen, 128);
                            }
                            readLen += 128;
                        }
                    }
                    System.arraycopy(photoData, 1, jp2Data, 0, 2048);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jp2Data;
    }

    public void resolveInfo(byte[] basicInfo,byte[] photoInfo, byte[] expendInfo,byte[] trackInfo, List<byte[]> historyInfo){
        //resolveBasicInfo(basicInfo);

        this.baoanBasicInfo = new BaoAnBasicInfo();
        this.expendinfo = new ExpendInfo();
        this.trackinfo = new TrackRecord();
        resolveBasicInfo(basicInfo,true);
        resolveExpendInfo(expendInfo);
        resolveTrackRecord(trackInfo,historyInfo);
    }

    public BaoAnBasicInfo getBaoAnBasicInfo(){
        return this.baoanBasicInfo;
    }

    public ExpendInfo getExpendInfo(){
        return this.expendinfo;
    }

    public TrackRecord getTrackRecord(){
        return this.trackinfo;
    }

    /*拓展信息 */
    private void resolveExpendInfo(byte[] expendInfo){
        ExpendInfo expend = new ExpendInfo();
        //1-6现住地址
        String xzdz = new String(expendInfo, 0, 6);
        xzdz = DBHelper.quaryArea(this.context, xzdz)+"";
        //7-76现住址详细地址
        byte[] xzxzByte = new byte[70];
        System.arraycopy(expendInfo, 6, xzxzByte, 0, 70);
        //System.out.println(Tools.Bytes2HexString(xzxzByte, 70));
        String xzxz = "";
        try {
            xzxz = new String(xzxzByte, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //77-78文化程度
        String cultureStatus = new String(expendInfo, 76, 2);
        cultureStatus = DBHelper.quaryCultrueStatus(this.context, cultureStatus);
        //79兵役状况
        String milityStatus = new String(expendInfo, 78, 1);
        milityStatus = DBHelper.quaryMilityStatus(this.context, milityStatus);
        //80-81婚姻状况
        String marrigeStatus = new String(expendInfo, 79, 2);
        marrigeStatus = DBHelper.quaryMarrigaStatus(this.context, marrigeStatus);
        //82身高  身高直接由此字节转换，如AF 为175
        int heightInt = expendInfo[81]&0xFF;
        String height = heightInt +"";
//		height = "" + Integer.valueOf(height, 16);
        //83健康状况
        String healthStatus = new String(expendInfo, 82, 1);
        healthStatus = DBHelper.quaryHealth(this.context, healthStatus);
        //84-85政治面貌
        String polityStatus = new String(expendInfo, 83, 2);
        polityStatus = DBHelper.quaryPolity(this.context, polityStatus);
        //86-87保安员职业等级
        String baoanGrade = new String(expendInfo, 85, 2);
        baoanGrade = DBHelper.quaryBaoAnGrade(this.context, baoanGrade);

        expendinfo.setHeight(height);
        expendinfo.setHealthStatus(healthStatus);
        expendinfo.setPolitiStatus(polityStatus);
        expendinfo.setCultureStatus(cultureStatus);
        expendinfo.setMilitaryStatus(milityStatus);
        expendinfo.setMarraigesStatus(marrigeStatus);
        expendinfo.setXzdz(xzdz + xzxz);

        baoanBasicInfo.setBaoAnGrade(baoanGrade);


        String info = "现住地址:"+ xzdz
                + "\n现住址详细地址:"+xzxz
                + "\n文化程度:"+cultureStatus
                + "\n兵役状况:"+ milityStatus
                + "\n婚姻状况:"+marrigeStatus
                + "\n身高:"+height
                + "\n健康状况:"+healthStatus
                + "\n政治面貌:"+polityStatus
                + "\n保安员职业等级:"+baoanGrade ;
        if(DEBUG) Log.d(TAG, info);
    }

    /*从业信息*/
    private void resolveTrackRecord(byte[] trackInfo, List<byte[]> historyInfo){
        //TrackRecord  trackInfo = new TrackRecord();
        //1当前从业单位类型
        String unitType = new String(trackInfo, 0, 1);
        //2-14当前从业单位编码
        String dqunitcode = new String(trackInfo, 1, 13);
        //15-84单位名称
        byte[] dqunitBytes = new byte[70];
        System.arraycopy(trackInfo, 14, dqunitBytes, 0, 70);
//		String dqunitName = new String(this.trackInfo, 15, 69);
        String dqunitName = "";
        try {
            dqunitName = new String(dqunitBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //85-92入职时间
        String hireDate = new String(trackInfo, 84, 8);
        hireDate = hireDate.substring(0,4) + "-" + hireDate.substring(4,6) + "-" +hireDate.substring(6,8) ;
        //93-162当前服务对象、服务区域名称
        byte[] dqServiceBytes = new byte[70];
        System.arraycopy(trackInfo, 92, dqServiceBytes, 0, 70);
//		String dqServiceObject = new String(this.trackInfo, 93, 69);
        String dqServiceObject = "";
        try {
            dqServiceObject = new String(dqServiceBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //163-232上已服务对象、服务区域名称
        byte[] lastServiceObjectBytes = new byte[70];
        System.arraycopy(trackInfo, 162, lastServiceObjectBytes, 0, 70);
//		String lastServiceObject = new String(this.trackInfo, 163, 69);
        String lastServiceObject = "";
        try {
            lastServiceObject = new String(lastServiceObjectBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //233历史从业记录数
//		String recordCount = new String(this.trackInfo, 232, 1);
        int recordCount = trackInfo[232] ;

        trackinfo.setUnitName(dqunitName);
        trackinfo.setHireDate(hireDate);
        trackinfo.setServiceObject(dqServiceObject);
        trackinfo.setLastServiceObject(lastServiceObject);
        trackinfo.setHistoryRecordCount(recordCount + "");

//		/**历史记录**/
//		List<String> historyRecord = new ArrayList<String>();
//		for(byte[] bytes : historyInfo){
//			//1历史从业单位类型
//			String hisUnitType = new String(bytes, 0, 1);
//			//2-14历史从业单位编码
//			String hisUnitCode = new String(bytes, 1, 13);
//			//15-84单位名称
//			byte[] hisUnitNameBytes = new byte[70];
//			System.arraycopy(bytes, 14, hisUnitNameBytes, 0, 70);
//			String hisUnitName = "";
//			try {
//				hisUnitName = new String(hisUnitNameBytes, "GB2312");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//85-92入职时间
//			String hisHireDate = new String(bytes, 84, 8);
//			hisHireDate = hisHireDate.substring(0,4) + "-" + hisHireDate.substring(4,6) + "-" +hisHireDate.substring(6,8) ;
//
//			//93-100离职时间
//			String hisFireDate = new String(bytes, 92, 8);
//			hisFireDate = hisFireDate.substring(0,4) + "-" + hisFireDate.substring(4,6) + "-" +hisFireDate.substring(6,8) ;
//			historyRecord.add(hisHireDate + "~" + hisFireDate + "，\n" + hisUnitName) ;
//			System.out.println(hisUnitNameBytes + hisUnitCode + hisUnitName + hisHireDate + hisFireDate);
//		}
//		trackinfo.setHistoryRecord(historyRecord);


        String info = "当前从业单位类型:"+unitType
                + "\n当前从业单位编码:"+dqunitcode
                + "\n单位名称:"+dqunitName
                + "\n入职时间:"+hireDate
                + "\n当前服务对象、服务区域名称:"+dqServiceObject
                + "\n上已服务对象、服务区域名称:"+lastServiceObject
                + "\n历史从业记录数:"+ recordCount;
        if(DEBUG) Log.d(TAG, info);
//		if(DEBUG) Log.d(TAG, "historyRecord = "+historyRecord);
    }

    //解析基本信息
    private void resolveBasicInfo(byte[] basicInfo,boolean bo){
        /****基本信息****/
        //1-14字节保安员证号
        String baoanID = new String(basicInfo, 0, 14);
        /*暂时不转换成汉字
        if("31".equals(baoanID.substring(0, 2))){
            baoanID = "沪" + baoanID.substring(2, 14);
        }
        */
        //15-32公民身份证
        String id = new String(basicInfo, 14, 18);
        //33-62姓名
//      byte[] nameBytes = Tools.HexString2Bytes("B3CCBBEFC8D9");
        byte[] nameBytes = new byte[30];
        System.arraycopy(basicInfo, 32, nameBytes, 0, 30);
        String name = "";
        try {
            name = new String(nameBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //63-92曾用名
//      String usedname = new String(baiscInfo, 62, 30);
        byte[] usednameBytes = new byte[30];
        System.arraycopy(basicInfo, 62, usednameBytes, 0, 30);
        String usedname = "";
        try {
            usedname = new String(usednameBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //93性别
        String sex = new String(basicInfo, 92, 1);

        //查询数据库
        sex = DBHelper.quarySex(context, sex);

        //94-95民族
        String nation = new String(basicInfo, 93, 2);
        nation = DBHelper.quaryNation(context, nation);
        //96-103出生日期
        String barthday = new String(basicInfo, 95, 8);
        barthday = barthday.substring(0, 4) + "-" + barthday.substring(4, 6) + "-" + barthday.substring(6, 8);
        //104-109户籍地省市
        String hjss = new String(basicInfo, 103, 6);
        hjss = DBHelper.quaryArea(context, hjss);
        //110-179户籍详细地址
        byte[] hjxzBytes = new byte[70];
        System.arraycopy(basicInfo, 109, hjxzBytes, 0, 70);
//      String hjxz = new String(basicInfo, 109, 70);
        String hjxz = "";
        try {
            hjxz = new String(hjxzBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //180血型
        String bloodtype = new String(basicInfo, 179, 1);
        bloodtype = DBHelper.quaryBloodType(context, bloodtype);
        //181-184发证公安机关
        String fzjg = new String(basicInfo, 180, 4);
        fzjg = DBHelper.quaryPliceOffice(context, fzjg);
        //185-192发证日期
        String fzrq = new String(basicInfo, 184, 8);
        fzrq = fzrq.substring(0, 4) + "-" + fzrq.substring(4, 6) + "-" + fzrq.substring(6, 8);
        //193-193首次发证公安机关
        String scfzjg = new String(basicInfo, 192, 4);
        scfzjg = DBHelper.quaryPliceOffice(context, scfzjg);
        //197-204首次发证日期
        String scfzrq = new String(basicInfo, 196, 8);
        scfzrq = scfzrq.substring(0, 4) + "-" + scfzrq.substring(4, 6) + "-" + scfzrq.substring(6, 8);

        baoanBasicInfo.setBaoAnName(name);
        baoanBasicInfo.setSex(sex);
        baoanBasicInfo.setNation(nation);
        baoanBasicInfo.setBarth(barthday);
        baoanBasicInfo.setBaoAnID(baoanID);
        baoanBasicInfo.setId(id);
        baoanBasicInfo.setFzjg(fzjg);
        baoanBasicInfo.setFzrq(fzrq);
        baoanBasicInfo.setScfzjg(scfzjg);
        baoanBasicInfo.setScfarq(scfzrq);

        if(expendinfo!= null){
            expendinfo.setUsedName(usedname);
            expendinfo.setBloodType(bloodtype);
            expendinfo.setHjdz(hjxz );
        }
        String info = "保安员证号:"+baoanID
                + "\n身份证:" + id
                + "\n姓名:" + name
                + "\n曾用名:" + usedname
                + "\n性别:" + sex
                + "\n民族:" + nation
                + "\n出生日期:" + barthday
                + "\n户籍地省市:" +hjss
                + "\n户籍详细地址:" + hjxz
                + "\n血型:" + bloodtype
                + "\n发证公安机关:" + fzjg
                + "\n发证日期:" + fzrq
                + "\n首次发证公安机关:" +scfzjg
                + "\n首次发证日期:" +scfzrq;
        if(DEBUG) Log.d(TAG, info);
    }

    //保安员历史从业信息文件
//	public List<byte[]> getHistoryTrackInfo(int recordCount){
//		List<byte[]> list = new ArrayList<byte[]>();
//		byte[] cmd1 = Tools.HexString2Bytes(COS_SELECT_FILE + "1003");
//		byte[] resp1 = manager.icCardAPDU(cmd1);
//		byte[] cmd2 = null;
//		for(int i = 1; i <= recordCount; i++){
//			byte[] resp = null;
//			String index = Integer.toHexString(i);
//			if(index.length() < 2){
//				cmd2 = Tools.HexString2Bytes("00B20"+ i +"D464");
//			}else{
//				cmd2 = Tools.HexString2Bytes("00B2"+ i +"D464");
//			}
//			resp = manager.icCardAPDU(cmd2);
//			if(resp != null && resp.length > 100){
//				list.add(resp);
//			}else{
//				break;
//			}
//		}
//
////		getListEdu();
//		return list;
//	}


    //解析基本信息
    public BaoAnBasicInfo resolveBasicInfo(byte[] basicInfo){
        /****基本信息****/
        //1-14字节保安员证号
        String baoanID = new String(basicInfo, 0, 14);
        /*暂时不转换成汉字
        if("31".equals(baoanID.substring(0, 2))){
            baoanID = "沪" + baoanID.substring(2, 14);
        }
        */
        //15-32公民身份证
        String id = new String(basicInfo, 14, 18);
        //33-62姓名
//      byte[] nameBytes = Tools.HexString2Bytes("B3CCBBEFC8D9");
        byte[] nameBytes = new byte[30];
        System.arraycopy(basicInfo, 32, nameBytes, 0, 30);
        String name = "";
        try {
            name = new String(nameBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //63-92曾用名
//      String usedname = new String(baiscInfo, 62, 30);
        byte[] usednameBytes = new byte[30];
        System.arraycopy(basicInfo, 32, usednameBytes, 0, 30);
        String usedname = "";
        try {
            usedname = new String(usednameBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //93性别
        String sex = new String(basicInfo, 92, 1);

        //查询数据库
        sex = DBHelper.quarySex(context, sex);

        //94-95民族
        String nation = new String(basicInfo, 93, 2);
        nation = DBHelper.quaryNation(context, nation);
        //96-103出生日期
        String barthday = new String(basicInfo, 95, 8);
        barthday = barthday.substring(0, 4) + "-" + barthday.substring(4, 6) + "-" + barthday.substring(6, 8);
        //104-109户籍地省市
        String hjss = new String(basicInfo, 103, 6);
        hjss = DBHelper.quaryArea(context, hjss);
        //110-179户籍详细地址
        byte[] hjxzBytes = new byte[70];
        System.arraycopy(basicInfo, 109, hjxzBytes, 0, 70);
//      String hjxz = new String(basicInfo, 109, 70);
        String hjxz = "";
        try {
            hjxz = new String(hjxzBytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //180血型
        String bloodtype = new String(basicInfo, 179, 1);
        bloodtype = DBHelper.quaryBloodType(context, bloodtype);
        //181-184发证公安机关
        String fzjg = new String(basicInfo, 180, 4);
        fzjg = DBHelper.quaryPliceOffice(context, fzjg);
        //185-192发证日期
        String fzrq = new String(basicInfo, 184, 8);
        fzrq = fzrq.substring(0, 4) + "-" + fzrq.substring(4, 6) + "-" + fzrq.substring(6, 8);
        //193-193首次发证公安机关
        String scfzjg = new String(basicInfo, 192, 4);
        scfzjg = DBHelper.quaryPliceOffice(context, scfzjg);
        //197-204首次发证日期
        String scfzrq = new String(basicInfo, 196, 8);
        scfzrq = scfzrq.substring(0, 4) + "-" + scfzrq.substring(4, 6) + "-" + scfzrq.substring(6, 8);

        BaoAnBasicInfo baoanBasicInfo = new BaoAnBasicInfo();

        baoanBasicInfo.setBaoAnName(name);
        baoanBasicInfo.setSex(sex);
        baoanBasicInfo.setNation(nation);
        baoanBasicInfo.setBarth(barthday);
        baoanBasicInfo.setBaoAnID(baoanID);
        baoanBasicInfo.setId(id);
        baoanBasicInfo.setFzjg(fzjg);
        baoanBasicInfo.setFzrq(fzrq);
        baoanBasicInfo.setScfzjg(scfzjg);
        baoanBasicInfo.setScfarq(scfzrq);

        if(expendinfo!= null){
            expendinfo.setUsedName(usedname);
            expendinfo.setBloodType(bloodtype);
            expendinfo.setHjdz(hjxz );
        }
        String info = "保安员证号:"+baoanID
                + "\n身份证:" + id
                + "\n姓名:" + name
                + "\n曾用名:" + usedname
                + "\n性别:" + sex
                + "\n民族:" + nation
                + "\n出生日期:" + barthday
                + "\n户籍地省市:" +hjss
                + "\n户籍详细地址:" + hjxz
                + "\n血型:" + bloodtype
                + "\n发证公安机关:" + fzjg
                + "\n发证日期:" + fzrq
                + "\n首次发证公安机关:" +scfzjg
                + "\n首次发证日期:" +scfzrq;
        if(DEBUG) Log.d(TAG, info);
        return baoanBasicInfo;
    }

    /**
     * 获取APDU命令包
     * [报头(1)][长度(2)][包序号(1)][类型(1)0x62][data(...)][报尾(1)0x03][校验位(1)]
     * 报头：0x02
     * 长度：包序号+类型+data的长度
     * 包序号：自+1，对应响应包的包序号，这里用0x00
     * 类型：0x62 APDU命令
     * data：传输数据
     * 报尾：0x03
     * 校验位：长度+包序号+类型+data+报尾
     * @param data
     * @return 命令数据包
     *
     */
    public byte[] getApduCmdPacket(byte[]data){
        int length = 1+1+data.length;
        int packetlength = 1+2+1+1+data.length+1+1;
        byte[] packet = new byte[packetlength];
        packet[0] = (byte)0x02;//报头1

        byte[] be = CommonUtil.intToBytesBe(length);
        System.arraycopy(be, 2, packet, 1, 2);//长度2

        packet[3] = (byte)0x00;//包序号1
        packet[4] = (byte)0x62;//类型1
        System.arraycopy(data, 0, packet, 5, data.length);//长度2
        packet[packetlength-2] = (byte)0x03;//报尾
        fillChecksum(packet, packet.length);
        return packet;
    }

    /**
     * 获取APDU响应包
     * [0x06][报头(1)][长度(2)][包序号(1)][类型(1)0x62][data(...)][报尾(1)0x03][校验位(1)]
     * @param packet
     * @return
     */
    public byte[] getResponseData(byte[] packet){
        int datalength = packet.length - 8;
        if (datalength > 0){
            byte[] data = new byte[datalength];
            System.arraycopy(packet, 6, data, 0, datalength);
            return data;
        } else {
            return null;
        }
    }

    /**
     * 计算校验位
     * @param packet
     * @param length
     */
    public void fillChecksum(byte[] packet, int length) {
        int sum = 0;
        int datalength = length - 1 - 1;
        byte[] data = new byte[datalength];

        System.arraycopy(packet, 1, data, 0, datalength);

        for (int i = 0; i < data.length; i++) {
            sum ^= data[i];
        }
        byte[] le = CommonUtil.intToBytesLe(sum);
        System.arraycopy(le, 0, packet, length - 1, 1);
    }


}
