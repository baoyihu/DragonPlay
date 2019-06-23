package com.baoyihu.mem.node;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name = "category", strict = false)
public class Category implements Parcelable, Serializable
{
    private static final long serialVersionUID = -4170608087852918192L;
    
    @Element(name = "id", required = false)
    private String categoryId;
    
    @Element(name = "name", required = false)
    private String categoryName;
    
    @Element(name = "type", required = false)
    private CategoryType categoryType;
    
    @Element(name = "introduce", required = false)
    private String categoryIntroduce;
    
    @Element(name = "picture", required = false)
    private Picture categoryPicture;
    
    @Element(name = "haschildren", required = false)
    private int categoryHasChildren;
    
    @Element(name = "ratingId", required = false)
    private int categoryRatingId;
    
    @Element(name = "isSubscribed", required = false)
    private boolean categorySubscribed;
    
    @Element(name = "foreignSn", required = false)
    private String categoryForeignSn;
    
    @Element(name = "parentCategoryId", required = false)
    private String parentCategoryId;
    
    public static final Category ALL = new Category("-1");
    
    public Category(String id)
    {
        this.categoryId = id;
    }
    
    public Category()
    {
    }
    
    public String getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(String id)
    {
        this.categoryId = id;
    }
    
    public String getName()
    {
        return categoryName;
    }
    
    public void setName(String name)
    {
        this.categoryName = name;
    }
    
    public CategoryType getType()
    {
        return categoryType;
    }
    
    public void setType(CategoryType type)
    {
        this.categoryType = type;
    }
    
    public String getIntroduce()
    {
        return categoryIntroduce;
    }
    
    public void setIntroduce(String introduce)
    {
        this.categoryIntroduce = introduce;
    }
    
    public Picture getPicture()
    {
        return categoryPicture;
    }
    
    public void setPicture(Picture picture)
    {
        this.categoryPicture = picture;
    }
    
    public int getHasChildren()
    {
        return categoryHasChildren;
    }
    
    public void setHasChildren(int hasChildren)
    {
        this.categoryHasChildren = hasChildren;
    }
    
    public int getRatingId()
    {
        return categoryRatingId;
    }
    
    public void setRatingId(int ratingId)
    {
        this.categoryRatingId = ratingId;
    }
    
    public boolean isSubscribed()
    {
        return categorySubscribed;
    }
    
    public void setSubscribed(boolean subscribed)
    {
        this.categorySubscribed = subscribed;
    }
    
    public String getForeignSn()
    {
        return categoryForeignSn;
    }
    
    public void setForeignSn(String foreignSn)
    {
        this.categoryForeignSn = foreignSn;
    }
    
    public String getParentCategoryId()
    {
        return parentCategoryId;
    }
    
    public void setParentCategoryId(String parentCategoryId)
    {
        this.parentCategoryId = parentCategoryId;
    }
    
    @Override
    public String toString()
    {
        return categoryName;
    }
    
    @Override
    public int hashCode()
    {
        if (categoryId != null)
        {
            return categoryId.hashCode();
        }
        else
        {
            return 0;
        }
    }
    
    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        
        if (object == null)
        {
            return false;
        }
        
        if (!(object instanceof Category))
        {
            return false;
        }
        
        return categoryId.equals(((Category)object).getCategoryId());
    }
    
    public Category(Parcel source)
    {
        categoryId = source.readString();
        categoryName = source.readString();
        categoryType = source.readParcelable(CategoryType.class.getClassLoader());
        categoryIntroduce = source.readString();
        categoryPicture = source.readParcelable(Picture.class.getClassLoader());
        categoryHasChildren = source.readInt();
        categoryRatingId = source.readInt();
        categorySubscribed = (Boolean)source.readValue(Boolean.class.getClassLoader());
        categoryForeignSn = source.readString();
        parentCategoryId = source.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(categoryId);
        dest.writeString(categoryName);
        dest.writeParcelable(categoryType, flags);
        dest.writeString(categoryIntroduce);
        dest.writeParcelable(categoryPicture, flags);
        dest.writeInt(categoryHasChildren);
        dest.writeInt(categoryRatingId);
        dest.writeValue(categorySubscribed);
        dest.writeString(categoryForeignSn);
        dest.writeString(parentCategoryId);
    }
    
    public static final Parcelable.Creator<Category> CREATOR = new Creator<Category>()
    {
        @Override
        public Category[] newArray(int size)
        {
            return new Category[size];
        }
        
        @Override
        public Category createFromParcel(Parcel source)
        {
            return new Category(source);
        }
    };
    
    @Override
    public int describeContents()
    {
        return 0;
    }
    
}