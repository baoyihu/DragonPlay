package com.baoyihu.mem.node;

import android.os.Parcel;
import android.os.Parcelable;

public enum CategoryType implements Parcelable
{
    VOD, AUDIO_VOD, VIDEO_VOD, TELEPLAY_VOD, CREDIT_VOD, CHANNEL, AUDIO_CHANNEL, VIDEO_CHANNEL, WEB_CHANNEL, MIX, VAS, TVOD;
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(ordinal());
    }
    
    public static final Parcelable.Creator<CategoryType> CREATOR = new Creator<CategoryType>()
    {
        @Override
        public CategoryType[] newArray(int size)
        {
            return new CategoryType[size];
        }
        
        @Override
        public CategoryType createFromParcel(Parcel source)
        {
            return CategoryType.values()[source.readInt()];
        }
    };
    
    @Override
    public int describeContents()
    {
        return 0;
    }
    
}