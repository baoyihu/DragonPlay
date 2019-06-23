package com.baoyihu.dragonplayer;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class FileManagerAdapter extends SimpleAdapter
{
    public FileManagerAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
        int[] to, boolean isOnline)
    {
        super(context, data, resource, from, to);
    }
    
    /**
     * 
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.SimpleAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = super.getView(position, convertView, parent);
        if (null != v)
        {
            ImageView imgView = (ImageView)v.findViewById(R.id.iconLeft);
            imgView.setBackgroundResource(R.drawable.online);
            // if (isOnline)
            // {
            // imgView.setBackgroundResource(R.drawable.online);
            // }
            // else
            // {
            // imgView.setBackgroundResource(R.drawable.local);
            // }
        }
        
        return v;
    }
    
}
