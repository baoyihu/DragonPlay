package com.baoyihu.mem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.common.util.TextUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * object to json
 * json to object
 */
public final class JsonParser
{
    private static final String TAG = JsonParser.class.getSimpleName();
    
    private static ObjectMapper mapper = new ObjectMapper();
    
    private JsonParser()
    {
        
    }
    
    /**
     * Entity object into json byte
     *
     * @param object javaObjects
     * @param <T>    Object Type
     * @return jsonCorresponding byte string
     */
    public static <T> byte[] toJsonByte(final T object)
    {
        String jsonString = toJsonString(object);
        if (null == jsonString)
        {
            return null;
        }
        return jsonString.getBytes();
    }
    
    /**
     * Entity object into json string
     *
     * @param object javaObjects
     * @param <T>    Object Type
     * @return String objectCorresponding json string
     */
    public static <T> String toJsonString(final T object)
    {
        String result = null;
        
        try
        {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            result = mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            DebugLog.error(TAG, "to json  string exception");
        }
        return result;
    }
    
    /**
     * Json string converts java entity object
     *
     * @param klass Object Name
     * @param json  jsonCharacter string
     * @return javaEntity object
     */
    public static <T> T toObject(final Class<T> klass, final String json)
    {
        try
        {
            return toObjectCore(json, klass);
        }
        catch (JsonMappingException e)
        {
            DebugLog.error(TAG, "to object exception");
            DebugLog.trace(TAG, e);
            return null;
        }
    }
    
    /**
     * Json string converts java entity object
     *
     * @param klass Object Name
     * @param json  jsonCharacter string
     * @return javaEntity object
     * @throws JsonMappingException Data translation exception
     */
    public static <T> T toObjectCore(final String json, final Class<T> klass)
        throws JsonMappingException
    {
        try
        {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json, klass);
        }
        catch (JsonMappingException jsonMappingException)
        {
            
            //for the first time JsonMappingException,will redress the json format,and then to
            // object again.
            try
            {
                return mapper.readValue(redressJson(json), klass);
            }
            catch (IOException e)
            {
                DebugLog.error(TAG, " all parseException to " + "JsonMappingException");
                throw new JsonMappingException("all parseException to JsonMappingException", e);
            }
        }
        catch (IOException e)
        {
            DebugLog.error(TAG, " all parseException to JsonMappingException");
            
            throw new JsonMappingException("all parseException to JsonMappingException", e);
        }
    }
    
    /**
     * The list <elementClasses> convert to List <String>
     * List converted <string>, can pass with intent
     *
     * @param objectList Object list
     * @param <T>        T
     * @return List<String>
     */
    public static <T> ArrayList<String> toJsonList(final ArrayList<T> objectList)
    {
        ArrayList<String> jsonList = new ArrayList<String>();
        for (int i = 0; i < objectList.size(); i++)
        {
            jsonList.add(toJsonString(objectList.get(i)));
        }
        return jsonList;
    }
    
    /**
     * The list <string> switch bit list <object>
     *
     * @param jsonList     List<String>
     * @param elementClass Into an object data type
     * @param <T>          T
     * @return list<elementClasses>
     */
    
    public static <T> List<T> toObjectList(final List<String> jsonList, Class<T> elementClass)
    {
        List<T> objectList = new ArrayList<T>();
        for (int i = 0; i < jsonList.size(); i++)
        {
            objectList.add(toObject(elementClass, jsonList.get(i)));
        }
        return objectList;
    }
    
    /**
     * redress the json format
     * if some format of json is incorrect and can be corrected,this method will redress it.
     * this method should be invoked when throw the JsonMappingException .
     *
     * @param json
     */
    private static String redressJson(String json)
    {
        if (TextUtils.isEmpty(json))
        {
            return json;
        }
        /**
         * match the json ^","^:
         */
        String regular = "[^\"}\\]],\"[^:]";
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(json);
        StringBuffer sb = new StringBuffer();
        int startPosition = 0;
        int endPosition = 0;
        while (matcher.find())
        {
            endPosition = matcher.start() + 1;
            sb.append(json.substring(startPosition, endPosition) + "\"");
            startPosition = endPosition;
        }
        if (endPosition < json.length())
        {
            sb.append(json.substring(endPosition));
        }
        return sb.toString();
    }
    
    /**
     * The string switch bit list <object>
     *
     * @param json
     * @param valueTypeRef Let Json identify your Object in a List
     */
    public static List jsonToList(String json, TypeReference valueTypeRef)
    {
        List objectList = new ArrayList<Object>();
        try
        {
            objectList = mapper.readValue(json, valueTypeRef);
        }
        catch (IOException e)
        {
            DebugLog.error(TAG, "");
        }
        return objectList;
    }
    
    /**
     * The list <object> switch bit string
     *
     * @param list
     */
    public static String listToJson(List list)
    {
        String json = null;
        try
        {
            json = mapper.writeValueAsString(list);
        }
        catch (JsonProcessingException e)
        {
            DebugLog.error(TAG, "");
        }
        return json;
    }
}