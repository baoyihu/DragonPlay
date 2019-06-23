package com.baoyihu.util;

import com.baoyihu.common.util.DebugLog;

public class NetworkExecuter
{
    boolean finished = false;
    
    Runnable runnable = null;
    
    private static final String TAG = "NetworkExecuter";
    
    public NetworkExecuter(Runnable runnable)
    {
        this.runnable = runnable;
    }
    
    public boolean doWork()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (runnable != null)
                {
                    runnable.run();
                }
                finished = true;
            }
            
        }).start();
        while (!finished)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                DebugLog.trace(TAG, e);
            }
        }
        return true;
    }
}
