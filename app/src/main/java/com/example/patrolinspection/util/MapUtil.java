package com.example.patrolinspection.util;

import com.example.patrolinspection.R;

import java.util.HashMap;
import java.util.Map;

//映射工具 将中文、单词、图标对应
public class MapUtil
{
    private static Map<String, Integer> stateMap = new HashMap<String, Integer>();

    private static Map<String,String> eventTypeMap = new HashMap<>();
    private static Map<String,String> lineTypeMap = new HashMap<>();
    private static Map<String,String> planTypeMap = new HashMap<>();
    private static Map<String,String> faceTypeMap = new HashMap<>();
    private static Map<String,String> dutyMap = new HashMap<>();
    private static Map<String,String> photoTypeMap = new HashMap<>();
    private static Map<String,String> handleTypeMap = new HashMap<>();
    private static Map<String,String> schoolEventTypeMap = new HashMap<>();
    private static Map<String,String> schoolEventRecordTypeMap = new HashMap<>();

    static {
        //patrol schedule
        stateMap.put("进行中", R.drawable.state_running);
        stateMap.put("未开始",R.drawable.state_unstart);
        stateMap.put("已结束",R.drawable.state_ended);
//        stateMap.put("漏检",R.drawable.state_miss);
//        stateMap.put("跳检",R.drawable.state_jump);
        //patrolingActivity  point state
        stateMap.put("未巡检",R.drawable.state_unstart);
        stateMap.put("已巡检",R.drawable.state_ended);
        stateMap.put("巡检中",R.drawable.state_running);
        //upload
        stateMap.put("未上传",R.drawable.state_miss);
        stateMap.put("已上传",R.drawable.state_ended);

        eventTypeMap.put("alarm","一键报警");
        eventTypeMap.put("material","物防设施");
        eventTypeMap.put("technical","技防设施");
        eventTypeMap.put("publicSecurity","治安管理");
        eventTypeMap.put("fireProtection","消防设施");
        eventTypeMap.put("production","生产安全");
        eventTypeMap.put("elseEvent","其他事件");
        eventTypeMap.put("一键报警","alarm");
        eventTypeMap.put("物防设施","material");
        eventTypeMap.put("技防设施","technical");
        eventTypeMap.put("治安管理","publicSecurity");
        eventTypeMap.put("消防设施","fireProtection");
        eventTypeMap.put("生产安全","production");
        eventTypeMap.put("其他事件","elseEvent");

        lineTypeMap.put("publicSecurity","治安巡检");
        lineTypeMap.put("technical","设备巡检");

        planTypeMap.put("Monday","周一");
        planTypeMap.put("Tuesday","周二");
        planTypeMap.put("Wednesday","周三");
        planTypeMap.put("Thursday","周四");
        planTypeMap.put("Friday","周五");
        planTypeMap.put("Saturday","周六");
        planTypeMap.put("Sunday","周日");
        planTypeMap.put("specialDate","特殊日期");
        planTypeMap.put("freeSchedule","自由排班");
        planTypeMap.put("2","Monday");
        planTypeMap.put("3","Tuesday");
        planTypeMap.put("4","Wednesday");
        planTypeMap.put("5","Thursday");
        planTypeMap.put("6","Friday");
        planTypeMap.put("7","Saturday");
        planTypeMap.put("1","Sunday");

        faceTypeMap.put("signIn","签到");
        faceTypeMap.put("signOut","签退");

        dutyMap.put("1","保安人员");
        dutyMap.put("2","保安负责");
        dutyMap.put("3","治安责任人员");
        dutyMap.put("4","治安服务人员");
        dutyMap.put("5","治安监督人员");
        dutyMap.put("6","授权管理人员");
        dutyMap.put("7","业务绑定人员");
        dutyMap.put("保安人员","1");
        dutyMap.put("保安负责","2");
        dutyMap.put("治安责任人员","3");
        dutyMap.put("治安服务人员","4");
        dutyMap.put("治安监督人员","5");
        dutyMap.put("授权管理人员","6");
        dutyMap.put("业务绑定人员","7");

        photoTypeMap.put("must","拍照（必须）");
        photoTypeMap.put("optional","拍照（可选）");
        photoTypeMap.put("forbid","拍照（禁止）");

        handleTypeMap.put("find","触发");
        handleTypeMap.put("disposal","处置");
        handleTypeMap.put("report","上报");
        handleTypeMap.put("end","结束");
        handleTypeMap.put("触发","find");
        handleTypeMap.put("处置","disposal");
        handleTypeMap.put("上报","report");
        handleTypeMap.put("结束","end");

        schoolEventTypeMap.put("wander","异常徘徊");
        schoolEventTypeMap.put("wanders","多次异常徘徊");
        schoolEventTypeMap.put("retention","异常滞留");
        schoolEventTypeMap.put("retentions","多次异常滞留");
        schoolEventTypeMap.put("retentionMultipoint","多点滞留");
        schoolEventTypeMap.put("blacklist","重点人员黑名单");

        schoolEventRecordTypeMap.put("unoperate","未操作");
        schoolEventRecordTypeMap.put("receive","收到");
        schoolEventRecordTypeMap.put("concern","关注");
        schoolEventRecordTypeMap.put("unfound","未发现");
        schoolEventRecordTypeMap.put("danger","侵害");
        schoolEventRecordTypeMap.put("undanger","未发生");
    }

    public static int getState(String s){
        return stateMap.get(s);
    }

    public static String getEventType(String s){
        return eventTypeMap.get(s);
    }

    public static String getLineType(String s){
        return lineTypeMap.get(s);
    }

    public static String getPlanType(String s){
        return planTypeMap.get(s);
    }

    public static String getFaceType(String s){
        return faceTypeMap.get(s);
    }

    public static String getDuty(String s){
        return dutyMap.get(s);
    }

    public static String getPhotoType(String s){
        return  photoTypeMap.get(s);
    }

    public static String getHandleType(String s){
        return handleTypeMap.get(s);
    }

    public static String getSchoolEventType(String s){
        return schoolEventTypeMap.get(s);
    }

    public static String getSchoolEventRecordType(String s){
        return schoolEventRecordTypeMap.get(s);
    }
}
