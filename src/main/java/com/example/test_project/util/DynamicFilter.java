package com.example.test_project.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicFilter {

    public Map<String, Object> toFilterMap(Object obj) {
        Map<String, Object> map = new HashMap<>();

        if (obj == null) return map;

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                boolean isPrimitive = field.getType().isPrimitive();
                if (value != null && !(isPrimitive && ((Number)value).longValue() == 0)) {
                    map.put(field.getName(), value);
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error reading fields from filter object", e);
        }
        return map;
    }
}
