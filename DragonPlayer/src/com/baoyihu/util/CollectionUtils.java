package com.baoyihu.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baoyihu.common.util.DebugLog;

public class CollectionUtils
{
    private static final String TAG = "CollectionUtils";
    
    public interface ITransform<T, U>
    {
        U transform(T t);
    }
    
    public interface NameGetor<T>
    {
        String getName(T item);
    }
    
    public interface IPredicate<T>
    {
        public boolean apply(final T t);
    }
    
    public static <T> Collection<T> filterInPlace(Collection<T> collection, IPredicate<T> predicate)
    {
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext())
        {
            if (!predicate.apply(iterator.next()))
            {
                iterator.remove();
            }
        }
        return collection;
    }
    
    public static List<Integer> intArrayToIntegerList(int[] idList)
    {
        List<Integer> integers = new ArrayList<Integer>(8);
        for (int id : idList)
        {
            integers.add(Integer.valueOf(id));
        }
        return integers;
    }
    
    public static List<Long> longArrayToIntegerList(long[] idList)
    {
        List<Long> numbers = new ArrayList<Long>(8);
        for (long id : idList)
        {
            numbers.add(Long.valueOf(id));
        }
        return numbers;
    }
    
    public static List<String> stringArrayToIntegerList(String[] idList)
    {
        List<String> numbers = new ArrayList<String>(8);
        for (String id : idList)
        {
            numbers.add(String.valueOf(id));
        }
        return numbers;
    }
    
    public static <T> T findFirst(Collection<T> collection, IPredicate<T> predicate)
    {
        if (collection == null)
        {
            return null;
        }
        for (T element : collection)
        {
            if (predicate.apply(element))
            {
                return element;
            }
        }
        
        return null;
    }
    
    public static <T> List<T> newArrayList(T element)
    {
        List<T> list = new ArrayList<T>(8);
        list.add(element);
        return list;
    }
    
    public static <T> List<T> newArrayList(T... elements)
    {
        List<T> list = new ArrayList<T>(8);
        for (T element : elements)
        {
            list.add(element);
        }
        return list;
    }
    
    public static <T, U> Collection<U> transform(Collection<T> collection, ITransform<T, U> transform)
    {
        Collection<U> retval = new ArrayList<U>(8);
        
        for (T element : collection)
        {
            retval.add(transform.transform(element));
        }
        
        return retval;
    }
    
    public static <T, U> List<U> transform(List<T> collection, ITransform<T, U> transform)
    {
        List<U> retval = new ArrayList<U>(8);
        
        for (T element : collection)
        {
            retval.add(transform.transform(element));
        }
        
        return retval;
    }
    
    public static <T> List<T> select(Collection<T> collection, IPredicate<T> predicate)
    {
        final List<T> retval = new ArrayList<T>(8);
        
        for (final T element : collection)
        {
            if (predicate.apply(element))
            {
                retval.add(element);
            }
        }
        
        return retval;
    }
    
    public static <T> boolean isOneOf(final T testObject, T... candicates)
    {
        for (final T object : candicates)
        {
            if (testObject.equals(object))
            {
                return true;
            }
        }
        return false;
    }
    
    public static <T> Collection<T> firstN(Collection<T> items, int n)
    {
        Collection<T> retval = new ArrayList<T>(8);
        int i = 0;
        
        for (final T item : items)
        {
            if (i >= n)
            {
                break;
            }
            
            retval.add(item);
            ++i;
        }
        
        return retval;
    }
    
    public static <T> Collection<T> rest(Collection<T> items)
    {
        Collection<T> retval = new ArrayList<T>(8);
        int i = 0;
        
        for (final T item : items)
        {
            if (i == 0)
            {
                ++i;
                continue;
            }
            retval.add(item);
            ++i;
        }
        
        return retval;
    }
    
    public static <T> List<T> betweenNM(List<T> items, int n, int m)
    {
        /* N included, M excluded */
        List<T> retval = new ArrayList<T>(8);
        
        if (n >= items.size())
        {
            return new ArrayList<T>(8);
        }
        
        int boundary = Math.min(items.size(), m);
        
        for (int i = n; i < boundary; i++)
        {
            retval.add(items.get(i));
        }
        
        return retval;
    }
    
    public static <T> List<List<T>> transpose(List<List<T>> lol)
    {
        try
        {
            List<List<T>> result = new ArrayList<List<T>>(8);
            for (int i = 0; i < 3; i++)
            {
                List<T> row = new ArrayList<T>(8);
                for (int k = 0; k < lol.size(); k++)
                {
                    if (lol.get(k).isEmpty())
                    {
                        continue;
                    }
                    row.add(lol.get(k).get(i));
                }
                result.add(row);
            }
            return result;
        }
        catch (IndexOutOfBoundsException e)
        {
            DebugLog.trace(TAG, e);
            return lol;
        }
    }
    
    public static <T> List<T> mergeLists(List<List<T>> lists)
    {
        List<T> retval = new ArrayList<T>(8);
        
        for (List<T> l : lists)
        {
            retval.addAll(l);
        }
        
        return retval;
    }
    
    public static <T> T getOrNull(List<T> list, int index)
    {
        try
        {
            return list.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            DebugLog.trace(TAG, e);
            return null;
        }
    }
    
    public static <T> String listToString(List<T> list)
    {
        String ret = null;
        if (list != null && !list.isEmpty())
        {
            StringBuilder builder = new StringBuilder();
            for (T temp : list)
            {
                builder.append(',');
                builder.append(temp.toString());
            }
            ret = builder.substring(1);
        }
        return ret;
    }
    
    public static <T> String listToString(List<T> list, NameGetor<T> getor)
    {
        String ret = null;
        if (list != null && !list.isEmpty())
        {
            StringBuilder builder = new StringBuilder();
            for (T temp : list)
            {
                String name = getor.getName(temp);
                if (name != null)
                {
                    builder.append(',');
                    builder.append(name);
                }
            }
            if (builder.length() > 0)
            {
                ret = builder.substring(1);
            }
        }
        return ret;
    }
    
    public static <T> List<String> listToListString(List<T> list, NameGetor<T> getor)
    {
        List<String> ret = new ArrayList<String>();
        if (list != null && !list.isEmpty())
        {
            for (T temp : list)
            {
                String name = getor.getName(temp);
                if (name != null)
                {
                    ret.add(name);
                }
            }
        }
        return ret;
    }
    
    public static <T, V> void removeListFromMap(Map<T, V> map, List<T> list)
    {
        for (T key : list)
        {
            map.remove(key);
        }
    }
}
