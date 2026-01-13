package com.miniioc.framework.util;

import com.miniioc.framework.annotation.web.HttpMethod;

public class MappingValueResolver {

    private MappingValueResolver(){}

    public static String firstOrDefault(String[] arr1, String[] arr2, String defaultValue) {
        if (arr1.length > 0) return arr1[0];
        if (arr2.length > 0) return arr2[0];
        return defaultValue;
    }

    public static HttpMethod firstOrDefault(HttpMethod[] arr, HttpMethod defaultValue) {
        return arr.length > 0 ? arr[0] : defaultValue;
    }
}
