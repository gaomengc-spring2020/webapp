package com.mengchen.webapp.utils;

import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;

public class StatsDCheckPoint {


    @Autowired
    private static StatsDClient statsDClient;


    public static void StatsDCheckPoint(String Metrix_Name, long Start_Time){

        String TimerName = "Matrix_Name"+".Timer";

        statsDClient.recordExecutionTimeToNow(TimerName, Start_Time);
        statsDClient.incrementCounter(Metrix_Name);
    }


}
