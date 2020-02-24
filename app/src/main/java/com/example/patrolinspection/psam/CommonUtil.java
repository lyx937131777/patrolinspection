package com.example.patrolinspection.psam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static byte[] inputStream2byte(InputStream inStream) throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    }
    
    public static Bitmap makeBitmap(byte[] jpegData, int maxNumOfPixels) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,
                    options);
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(
                    options, -1, maxNumOfPixels);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,options);
        } catch (OutOfMemoryError ex) {
            return null;
        }
    }
    
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels < 0) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
    public static String formateImageToBase64(Bitmap bitmap){
        if (bitmap == null) {
            return "";
        }
        ByteArrayOutputStream out = null;
        try {  
           out = new ByteArrayOutputStream();  
           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
 
           out.flush();  
           out.close();  
 
           byte[] imgBytes = out.toByteArray();  
           return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
       } catch (Exception e) {  
           e.printStackTrace();
           return "";  
       } finally {  
           try {  
               out.flush();  
               out.close();  
           } catch (IOException e) {
               e.printStackTrace();  
           }  
       }  
    }
    public static String bytesToHexString(byte[] src) { 
        return bytesToHexString(src, false);
    }
    
    public static String bytesToHexString(byte[] src, boolean isPrefix) {  
        StringBuilder stringBuilder = new StringBuilder();  
        if (isPrefix == true) {  
            stringBuilder.append("0x");  
        }  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        char[] buffer = new char[2];  
        for (int i = 0; i < src.length; i++) {  
            buffer[0] = Character.toUpperCase(Character.forDigit(  
                    (src[i] >>> 4) & 0x0F, 16));  
            buffer[1] = Character.toUpperCase(Character.forDigit(src[i] & 0x0F,  
                    16));  
            stringBuilder.append(buffer);  
        }  
        return stringBuilder.toString();  
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) return null;

        byte[] ret;
        int sz = s.length();

        try {
            ret = new byte[sz/2];
            for (int i=0 ; i <sz ; i+=2) {
                ret[i/2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                                    | hexCharToInt(s.charAt(i+1)));
            }
            return ret;
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        return null;
    }
    
    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);

        throw new RuntimeException ("invalid hex char '" + c + "'");
    }

    public static byte[] intToBytesBe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; i++) {
            bytes[bytes.length - i - 1] = (byte)((intValue >> i*8) & 0xff);
        }

        return bytes;
    }
    
    public static byte[] intToBytesLe(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)((intValue >> i*8) & 0xff);
        }

        return bytes;
    }
    
    public static boolean IsNumber(String str) {
        String regex = "^[0-9]*$";
        return match(regex, str);
    }
    
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void delayms(int ms) {
        if (ms > 0) {
            try {
                Thread.sleep((long)ms);
            } catch (InterruptedException var2) {
            }
        }

    }
    
//    public static String readDSN(Context context){
//        try {
//            Class tm = Class.forName("android.telephony.TelephonyManager");
//            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//            Method getDefault = tm.getMethod("getDefault", null);
//            //read dsn
//            Method getDsn = tm.getMethod("getDsn", Context.class);
//            //read csn
//            Method getDsn2 = tm.getMethod("getDsn2", Context.class);
//
//            String dsn = (String)getDsn.invoke(getDefault.invoke(telephonyManager, null),context);
//            String csn = (String)getDsn2.invoke(getDefault.invoke(telephonyManager, null),context);
////            Log.e("@@@@@", "dsn="+dsn);
////            Log.e("@@@@@", "csn="+csn);
//            return dsn;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
}
