package me.wcc.util;

import java.util.Map;

import com.google.gson.Gson;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午5:26
 */
public class JsonUtils {
    private JsonUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Util class");
    }
    public static final Gson GSON = new Gson();

    public static <T> T toObject(String json, Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) {
        return GSON.fromJson(json, Map.class);
    }
}
