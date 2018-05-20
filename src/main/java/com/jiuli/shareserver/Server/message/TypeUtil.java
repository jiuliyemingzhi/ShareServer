package com.jiuli.shareserver.Server.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class TypeUtil {
    //resp result类型
    public static final byte GROUP = 1;
    public static final byte JOIN = 2;
    public static final byte GROUPS = 3;

    //resp Type
    private static Type TYPE_GROUP;
    private static Type TYPE_JOIN;
    private static Type TYPE_GROUPS;


    //req content类型
    public static final byte USER_INFO = 1;

    //req Type
    private static Type TYPE_USER_INFO;
    private static Type TYPE_REQ;


    private static Gson gson;


    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String toJson(Object o) {
        return getGson().toJson(o);
    }

    public static Type getReqType(byte type) {
        switch (type) {
            case USER_INFO:
                if (TYPE_USER_INFO == null) {
                    TYPE_USER_INFO = new TypeToken<ReqModel<UserInfo>>() {
                    }.getType();
                }
                return TYPE_USER_INFO;
        }
        return TYPE_REQ == null ? TYPE_REQ = ReqModel.class : TYPE_REQ;
    }

}
