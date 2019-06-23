package com.baoyihu.dragonplayer.view;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FloatingWindow extends PopupWindow
{
    private static final String TAG = "FloatingWindow";
    
    private TextView contentView = null;
    
    private View parentView = null;
    
    private boolean needWork = false;
    
    private UpdateTextInterface textGetter = null;
    
    public interface UpdateTextInterface
    {
        String getText();
        
        int getColorText();
    }
    
    private static final int MSG_FLOATING_WINDOWN_UPDATE = 87101;
    
    public FloatingWindow(View parent, Rect rect, UpdateTextInterface textGetter)
    {
        super(new TextView(parent.getContext()), rect.width(), rect.height());
        this.contentView = (TextView)super.getContentView();
        this.parentView = parent;
        this.textGetter = textGetter;
        setPopupWindowFacade();
    }
    
    private void setPopupWindowFacade()
    {
        
        contentView.setTextSize(12);
        contentView.setTextColor(Color.BLUE);
        Drawable drawble = getBackground();
        if (drawble != null)
        {
            drawble.setAlpha(0);
        }
        
        contentView.setGravity(Gravity.CENTER);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(android.R.style.Animation_Dialog);
        this.update();
        this.setTouchable(true);
        this.setFocusable(false);
    }
    
    public void start()
    {
        if (!needWork)
        {
            needWork = true;
            if (workingHandler != null)
            {
                workingHandler.sendEmptyMessageDelayed(MSG_FLOATING_WINDOWN_UPDATE, 1000);
            }
        }
    }
    
    public void stop()
    {
        if (needWork)
        {
            needWork = false;
            if (workingHandler != null)
            {
                workingHandler.removeMessages(MSG_FLOATING_WINDOWN_UPDATE);
                workingHandler = null;
            }
        }
    }
    
    @Override
    public void dismiss()
    {
        stop();
        workingHandler = null;
        super.dismiss();
    }
    
    private int toRight = 100;
    
    private int toTop = 150;
    
    public void setPosition(int toRight, int toTop)
    {
        this.toRight = toRight;
        
        this.toTop = toTop;
    }
    
    private Handler workingHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_FLOATING_WINDOWN_UPDATE:
                    if (needWork)
                    {
                        String text = textGetter.getText();
                        contentView.setText(text);
                        contentView.setTextColor(textGetter.getColorText());
                        contentView.setSingleLine();
                        showAtLocation(parentView, Gravity.TOP | Gravity.RIGHT, toRight, toTop);
                        if (workingHandler != null)
                        {
                            workingHandler.sendEmptyMessageDelayed(MSG_FLOATING_WINDOWN_UPDATE, 2000);
                        }
                    }
                    break;
                
            }
        }
    };
    
}
