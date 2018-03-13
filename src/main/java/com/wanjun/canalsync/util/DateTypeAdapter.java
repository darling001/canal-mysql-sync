package com.wanjun.canalsync.util;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-13
 *
 * Gson
 */
public class DateTypeAdapter implements JsonSerializer<Date> , JsonDeserializer<Date>   {


    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public JsonElement serialize(Date src, Type arg1, JsonSerializationContext arg2) {
        String dateFormatAsString = format.format(new Date(src.getTime()));
        return new JsonPrimitive(dateFormatAsString);
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        try {
            Date date = format.parse(json.getAsString());
            return new Date(date.getTime());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

}
