package com.miniioc.util.mapper;

import com.miniioc.util.json.JsonMapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class TypeMapperRegistry {
    private final Map<Class<?>, ParameterMapper> mappers = new ConcurrentHashMap<>();

    public TypeMapperRegistry() {
        registerDefaults();
    }

    private void registerDefaults() {
        register(int.class, Integer::parseInt);
        register(Integer.class, Integer::parseInt);
        register(long.class, Long::parseLong);
        register(Long.class, Long::parseLong);
        register(double.class, Double::parseDouble);
        register(Double.class, Double::parseDouble);
        register(float.class, Float::parseFloat);
        register(Float.class, Float::parseFloat);
        register(boolean.class, Boolean::parseBoolean);
        register(Boolean.class, Boolean::parseBoolean);
        register(byte.class, Byte::parseByte);
        register(Byte.class, Byte::parseByte);
        register(short.class, Short::parseShort);
        register(Short.class, Short::parseShort);
        register(char.class, v -> v.charAt(0));
        register(Character.class, v -> v.charAt(0));
        register(String.class, v -> v);
        register(UUID.class, UUID::fromString);
        register(BigInteger.class, BigInteger::new);
        register(BigDecimal.class, BigDecimal::new);
        register(LocalDate.class, LocalDate::parse);
        register(LocalDateTime.class, LocalDateTime::parse);
        register(Instant.class, Instant::parse);
        register(ZonedDateTime.class, ZonedDateTime::parse);
        register(Date.class, v -> Date.from(Instant.parse(v)));
    }

    public void register(Class<?> type, ParameterMapper mapper) {
        mappers.put(type, mapper);
    }

    public Object map(Class<?> type, String value) throws Exception {
        if (value == null) return null;
        if (mappers.containsKey(type)) return mappers.get(type).map(value);

        if (type.isEnum()) {
            return parseEnum(type, value);
        }

        return JsonMapper.fromJson(value, type);
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T parseEnum(Class<?> type, String value) {
        return Enum.valueOf((Class<T>) type, value);
    }
}
