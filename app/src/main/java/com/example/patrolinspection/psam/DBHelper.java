package com.example.patrolinspection.psam;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.patrolinspection.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBHelper {
    private static final String DB_ADDR = "/data/data/com.example.patrolinspection/files/baoan.db";
    public static void addDataBase(Context context) throws IOException{
        
        //打开静态数据库文件的输入流
        InputStream is = context.getResources().openRawResource(R.raw.baoan);
//      InputStream is = new FileInputStream("/mnt/");
        //通过Context类来打开目标数据库文件的输出流，这样可以避免将路径写死
        FileOutputStream os = context.openFileOutput("baoan.db", Context.MODE_PRIVATE);
        byte[] buffer = new byte[1024];
          int count = 0;
          // 将静态数据库文件拷贝到目的地
          while ((count = is.read(buffer)) > 0) {
            os.write(buffer, 0, count);
          }
          is.close();
          os.close();
        
    }
    
    // 查询性别
    public static String quarySex(Context context, String id){
        String sex = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("sex", new String[]{"sex"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            sex = cursor.getString(cursor.getColumnIndex("sex"));
        }
        db.close();
        return sex;
    }
    
    //查询民族
    public static String quaryNation(Context context, String id){
        String nation = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("nation", new String[]{"nation"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            nation = cursor.getString(cursor.getColumnIndex("nation"));
        }
        db.close();
        return nation;
    }

    //查询行政区
    public static String quaryArea(Context context, String id){
        String area = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("area", new String[]{"area"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            area = cursor.getString(cursor.getColumnIndex("area"));
        }
        db.close();
        return area;
    }
    
    //查询保安等级
    public static String quaryBaoAnGrade(Context context, String id){
        String baoangrade = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("baoangrade", new String[]{"baoangrade"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            baoangrade = cursor.getString(cursor.getColumnIndex("baoangrade"));
        }
        db.close();
        return baoangrade;
    }
    //查询血型
    public static String quaryBloodType(Context context, String id){
        String bloodtype = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("bloodtype", new String[]{"bloodtype"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            bloodtype = cursor.getString(cursor.getColumnIndex("bloodtype"));
        }
        db.close();
        return bloodtype;
    }
    
    //查询文化程度
    public static String quaryCultrueStatus(Context context, String id){
        String cultruestatus = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("cultruestatus", new String[]{"cultruestatus"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            cultruestatus = cursor.getString(cursor.getColumnIndex("cultruestatus"));
        }
        db.close();
        return cultruestatus;
    }
    
    //查询健康状况
    public static String quaryHealth(Context context, String id){
        String health = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("health", new String[]{"health"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            health = cursor.getString(cursor.getColumnIndex("health"));
        }
        db.close();
        return health;
    }
    //查询婚姻状况
    public static String quaryMarrigaStatus(Context context, String id){
        String marrigastatus = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("marrigastatus", new String[]{"marrigastatus"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            marrigastatus = cursor.getString(cursor.getColumnIndex("marrigastatus"));
        }
        db.close();
        return marrigastatus;
    }
    
    //查询兵役状况
    public static String quaryMilityStatus(Context context, String id){
        String militystatus = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("militystatus", new String[]{"militystatus"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            militystatus = cursor.getString(cursor.getColumnIndex("militystatus"));
        }
        db.close();
        return militystatus;
    }
    
    //查询公安局
    public static String quaryPliceOffice(Context context, String id){
        String pliceoffice = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("pliceoffice", new String[]{"pliceoffice"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            pliceoffice = cursor.getString(cursor.getColumnIndex("pliceoffice"));
        }
        db.close();
        return pliceoffice;
    }
    
    //查询政治面貌
    public static String quaryPolity(Context context, String id){
        String polity = null;
        SQLiteDatabase db = context.openOrCreateDatabase(DB_ADDR, Context.MODE_PRIVATE, null);
        Cursor cursor = db.query("polity", new String[]{"polity"}, "_id=?", new String[]{id}, null, null, null);
        while(cursor.moveToNext()){
            polity = cursor.getString(cursor.getColumnIndex("polity"));
        }
        db.close();
        return polity;
    }

}
