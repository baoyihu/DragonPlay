package com.baoyihu.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemInfo
{
    final long[] sysCpu = new long[7];
    
    public static final int PROC_SPACE_TERM = ' ';
    
    public static final int PROC_OUT_LONG = 0x2000;
    
    public static final int PROC_COMBINE = 0x100;
    
    static float totalCpuTime = 0;
    
    static float processCpuTime = 0;
    
    public static float getProcessCpuRate()
    {
        float cpuRate = 0;
        float processCpuTime1 = getAppCpuTime();
        float totalCpuTime1 = getTotalCpuTime();
        if (totalCpuTime + processCpuTime > 0)
        {
            cpuRate = 100 * (processCpuTime1 - processCpuTime) / (totalCpuTime1 - totalCpuTime);
        }
        processCpuTime = processCpuTime1;
        totalCpuTime = totalCpuTime1;
        if (cpuRate > 100)
        {
            cpuRate = 100;
        }
        else if (cpuRate < 0)
        {
            cpuRate = 0;
        }
        return cpuRate;
    }
    
    public static long getTotalCpuTime()
    { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        long totalCpu = Long.parseLong(cpuInfos[2]) + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
            + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5]) + Long.parseLong(cpuInfos[7])
            + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }
    
    public static long getAppCpuTime()
    { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try
        {
            int pid = android.os.Process.myPid();
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13]) + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
            + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }
}
