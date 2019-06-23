package com.baoyihu.mem;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.fasterxml.jackson.annotation.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name = "filter")
public class NamedParameter implements Serializable, Parcelable
{
    private static final long serialVersionUID = 4511752843606013805L;
    
    @JsonProperty("key")
    @Element(required = false)
    private String key;
    
    @JsonProperty("value")
    @Element(required = false)
    private String value;
    
    public NamedParameter(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public NamedParameter()
    {
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    @Override
    public int describeContents()
    {
        return 0;
    }
    
    public NamedParameter(Parcel source)
    {
        key = source.readString();
        value = source.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(key);
        dest.writeString(value);
    }
    
    //实例化静态内部对象CREATOR实现接口Parcelable.Creator
    public static final Parcelable.Creator<NamedParameter> CREATOR = new Creator<NamedParameter>()
    {
        
        @Override
        public NamedParameter[] newArray(int size)
        {
            return new NamedParameter[size];
        }
        
        @Override
        public NamedParameter createFromParcel(Parcel source)
        {
            return new NamedParameter(source);
        }
    };
}
