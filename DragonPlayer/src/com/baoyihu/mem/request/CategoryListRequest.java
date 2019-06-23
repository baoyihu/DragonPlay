package com.baoyihu.mem.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;

@Root(name = "CategoryListReq", strict = false)
public class CategoryListRequest
{
    @Element(name = "categoryid", required = false)
    public String categoryId;
    
    @Element(name = "type", required = false)
    public String categoryType;
    
    @Element(name = "count", required = false)
    public Integer categoryCount;
    
    @Element(name = "offset", required = false)
    public Integer categoryOffset;
    
    public String getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }
    
    public String getType()
    {
        return categoryType;
    }
    
    public void setType(String type)
    {
        this.categoryType = type;
    }
    
    public Integer getCount()
    {
        return categoryCount;
    }
    
    public void setCount(Integer count)
    {
        this.categoryCount = count;
    }
    
    public Integer getOffset()
    {
        return categoryOffset;
    }
    
    public void setOffset(Integer offset)
    {
        this.categoryOffset = offset;
    }
    
    public CategoryListRequest()
    {
        
    }
    
    public CategoryListRequest(Parcel source)
    {
        
        categoryId = source.readString();
        categoryType = source.readString();
        categoryCount = (Integer)source.readValue(Integer.class.getClassLoader());
        categoryOffset = (Integer)source.readValue(Integer.class.getClassLoader());
    }
    
}