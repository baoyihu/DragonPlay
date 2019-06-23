package com.baoyihu.dragonplayer.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class SelectDialog implements DialogInterface.OnClickListener
{
    AlertDialog.Builder builder;
    
    Handler handler;
    
    int messageId;
    
    List<String> list = new ArrayList<String>();
    
    int seletedIndex;
    
    public SelectDialog(Context context)
    {
        builder = new AlertDialog.Builder(context);
    }
    
    public SelectDialog setTitle(CharSequence title)
    {
        builder.setTitle(title);
        return this;
    }
    
    public SelectDialog setList(List<String> strList)
    {
        list = new ArrayList<String>(strList);
        return this;
    }
    
    public SelectDialog setIntList(List<Integer> intList)
    {
        list = new ArrayList<String>();
        for (Integer temp : intList)
        {
            list.add(String.valueOf(temp));
        }
        
        return this;
    }
    
    public SelectDialog setIntArray(int[] array)
    {
        list = new ArrayList<String>();
        for (int iLoop = 0; iLoop < array.length; iLoop++)
        {
            list.add(String.valueOf(array[iLoop]));
        }
        
        return this;
    }
    
    public SelectDialog addSelectItem(String item)
    {
        list.add(item);
        return this;
    }
    
    public SelectDialog setSelectValue(String value)
    {
        seletedIndex = list.indexOf(value);
        return this;
    }
    
    public SelectDialog setSelectIndex(int index)
    {
        seletedIndex = index;
        return this;
    }
    
    public SelectDialog setHandler(Handler handler, int messageId)
    {
        this.handler = handler;
        this.messageId = messageId;
        return this;
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        Message message = handler.obtainMessage(messageId, which, 0, list.get(which));
        handler.sendMessage(message);
        dialog.dismiss();
    }
    
    public SelectDialog setNegativeButton(String text, OnClickListener listener)
    {
        builder.setNegativeButton(text, listener);
        return this;
    }
    
    public SelectDialog show()
    {
        String[] items = new String[list.size()];
        builder.setSingleChoiceItems(list.toArray(items), seletedIndex, this);
        AlertDialog dialog = builder.create();
        
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        dialog.show();
        return this;
    }
}
