package com.baoyihu.dragonplayer;

import com.baoyihu.common.util.DebugLog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ConfirmDialog
{
    static AlertDialog globalDialog = null;
    
    public static void show(Context context, String text, OnClickListener okListener)
    {
        AlertDialog alertDialog = new MyDialog(context);
        alertDialog.setTitle(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", okListener);
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        alertDialog.show();
        
    }
    
    public static void popUp(Context context, String text, final OnClickListener okListener)
    {
        final MyDialog alertDialog = new MyDialog(context);
        alertDialog.setTitle(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", okListener);
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        alertDialog.show();
        alertDialog.removeIn(okListener, 5000);
        
    }
    
    public static void removeLastDialog()
    {
        if (globalDialog != null)
        {
            globalDialog.dismiss();
        }
    }
    
    static class MyDialog extends AlertDialog
    {
        Runnable runnable = null;
        
        boolean alive = true;
        
        public void removeIn(final OnClickListener okListener, int millSeconds)
        {
            View view = getWindow().getDecorView();
            if (view != null)
            {
                runnable = new Runnable()
                {
                    
                    @Override
                    public void run()
                    {
                        if (alive)
                        {
                            okListener.onClick(MyDialog.this, DialogInterface.BUTTON_POSITIVE);
                        }
                    }
                };
                view.postDelayed(runnable, millSeconds);
            }
        }
        
        protected MyDialog(Context context)
        {
            super(context);
            alive = true;
            removeLastDialog();
            globalDialog = this;
        }
        
        @Override
        protected void onStop()
        {
            DebugLog.info("MyDialog", "onStop()");
            View view = getWindow().getDecorView();
            if (runnable != null)
            {
                view.removeCallbacks(runnable);
                runnable = null;
            }
            alive = false;
            super.onStop();
            
            if (globalDialog != null && globalDialog == this)
            {
                globalDialog = null;
            }
        }
    }
    
    public static void choose(Context context, String text, OnClickListener okListener)
    {
        AlertDialog alertDialog = new MyDialog(context);
        alertDialog.setTitle(text);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", okListener);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (OnClickListener)null);
        
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        alertDialog.show();
    }
}
