package com.baoyihu.mem.response;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.baoyihu.mem.node.Category;

@Root(name = "CategoryListResp", strict = false)
public class CategoryListResponse
{
    
    @Element(required = false)
    private int counttotal;
    
    @ElementList(type = Category.class, required = false)
    private List<Category> categorylist;
    
    public List<Category> getCategorylist()
    {
        return categorylist;
    }
    
    public void setCategorylist(List<Category> categorylist)
    {
        this.categorylist = categorylist;
    }
    
    public int getCategoryCounttotal()
    {
        return counttotal;
    }
    
    public void setCategoryCounttotal(int counttotal)
    {
        this.counttotal = counttotal;
    }
    
    public CategoryListResponse()
    {
        
    }
    
}