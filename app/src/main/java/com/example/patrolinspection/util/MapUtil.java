package com.example.patrolinspection.util;

import com.example.patrolinspection.R;

import java.util.HashMap;
import java.util.Map;

public class MapUtil
{
    private static Map<String, Integer> stateMap = new HashMap<String, Integer>();

    private static Map<String,String> eventTypeMap = new HashMap<>();
    private static Map<String,String> lineTypeMap = new HashMap<>();
    private static Map<String,String> planTypeMap = new HashMap<>();
    private static Map<String,String> faceTypeMap = new HashMap<>();

    static {
        stateMap.put("进行中", R.drawable.state_running);
        stateMap.put("未开始",R.drawable.state_unstart);
        stateMap.put("已结束",R.drawable.state_ended);
        stateMap.put("漏检",R.drawable.state_miss);
        stateMap.put("跳检",R.drawable.state_jump);
        stateMap.put("未巡检",R.drawable.state_unstart);
        stateMap.put("已巡检",R.drawable.state_ended);
        stateMap.put("巡检中",R.drawable.state_running);

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
}
