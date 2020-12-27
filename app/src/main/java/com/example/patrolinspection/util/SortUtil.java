package com.example.patrolinspection.util;

import com.example.patrolinspection.db.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

//排序工具 用于正确显示事件种类的顺序
public class SortUtil
{
    public static final String[] EVENT_SORT_STRINGS = {"material","fireProtection",
            "technical","publicSecurity","production","elseEvent","alarm"};
    public static final List<String> EVENT_SORT_LIST = new ArrayList<>();

    static {
        for(int i = 0; i < EVENT_SORT_STRINGS.length; i++){
            EVENT_SORT_LIST.add(EVENT_SORT_STRINGS[i]);
        }
    }

    public static int getPos(String s){
        return EVENT_SORT_LIST.indexOf(s);
    }

    public static void sortStringList(List<String> stringList){
        Collections.sort(stringList, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                //若为正o1在后
                return getPos(MapUtil.getEventType(o1))-getPos(MapUtil.getEventType(o2));
            }
        });
    }

    public static void sortEventList(List<Event> eventList){
        Collections.sort(eventList, new Comparator<Event>()
        {
            @Override
            public int compare(Event o1, Event o2)
            {
                //若为正o1在后
                return getPos(o1.getType())-getPos(o2.getType());
            }
        });
    }
}
