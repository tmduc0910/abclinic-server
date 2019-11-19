package com.abclinic.server.base;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
