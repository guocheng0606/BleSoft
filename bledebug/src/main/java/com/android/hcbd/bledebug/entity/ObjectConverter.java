package com.android.hcbd.bledebug.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ObjectConverter implements PropertyConverter<List<Instructions>, String> {

    @Override
    public List<Instructions> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        Type type = new TypeToken<ArrayList<Instructions>>() {
        }.getType();
        ArrayList<Instructions> itemList= new Gson().fromJson(databaseValue, type);
        return itemList;
    }

    @Override
    public String convertToDatabaseValue(List<Instructions> arrays) {
        if (arrays == null) {
            return null;
        } else {
            String dbString = new Gson().toJson(arrays);
            return dbString;
        }
    }
}
