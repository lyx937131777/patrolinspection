package com.example.patrolinspection.util;

import com.example.patrolinspection.R;

import java.util.HashMap;
import java.util.Map;

public class MapUtil
{
    private static Map<String, Integer> map = new HashMap<String, Integer>();

    static {
        map.put("进行中", R.drawable.state_running);
        map.put("未开始",R.drawable.state_unstart);
        map.put("已结束",R.drawable.state_ended);
        map.put("漏检",R.drawable.state_miss);
        map.put("跳检",R.drawable.state_jump);
    }

    public static int get(String s){
        return map.get(s);
    }
}
