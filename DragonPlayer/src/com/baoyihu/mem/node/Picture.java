package com.baoyihu.mem.node;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name = "picture", strict = false)
public class Picture implements Serializable, Parcelable
{
    /**
     * 
     */
    private static final long serialVersionUID = -1691163436319351110L;
    
    public enum PictureSize
    {
        ORIGINAL("0"), XL("XL"), L("L"), M("M"), S("S"), XS("XS");
        public static final PictureSize DEFAULT = M;
        
        private final String separator;
        
        PictureSize(String separator)
        {
            this.separator = separator;
        }
        
        public String getSeparator()
        {
            return separator;
        }
    }
    
    @Element(required = false)
    private String poster;
    
    @Element(required = false)
    private String still;
    
    @Element(required = false)
    private String ad;
    
    @Element(required = false)
    private String background;
    
    @Element(required = false)
    private String icon;
    
    @Element(required = false)
    private String deflate;
    
    @Element(required = false)
    private String title;
    
    @Element(required = false)
    private String draft;
    
    public String getStillRaw()
    {
        return still;
    }
    
    public String getAdRaw()
    {
        return ad;
    }
    
    public void setPoster(String poster)
    {
        this.poster = poster;
    }
    
    public void setStill(String still)
    {
        this.still = still;
    }
    
    public void setAd(String ad)
    {
        this.ad = ad;
    }
    
    public void setBackground(String background)
    {
        this.background = background;
    }
    
    public void setIcon(String icon)
    {
        this.icon = icon;
    }
    
    public void setDeflate(String deflate)
    {
        this.deflate = deflate;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public void setDraft(String draft)
    {
        this.draft = draft;
    }
    
    @Override
    public int describeContents()
    {
        
        return 0;
    }
    
    public Picture()
    {
        
    }
    
    public Picture(Parcel source)
    {
        poster = source.readString();
        still = source.readString();
        ad = source.readString();
        background = source.readString();
        icon = source.readString();
        deflate = source.readString();
        title = source.readString();
        draft = source.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(poster);
        dest.writeString(still);
        dest.writeString(ad);
        dest.writeString(background);
        dest.writeString(icon);
        dest.writeString(deflate);
        dest.writeString(title);
        dest.writeString(draft);
    }
    
    public static final Parcelable.Creator<Picture> CREATOR = new Creator<Picture>()
    {
        
        @Override
        public Picture[] newArray(int size)
        {
            return new Picture[size];
        }
        
        @Override
        public Picture createFromParcel(Parcel source)
        {
            return new Picture(source);
        }
    };
}
