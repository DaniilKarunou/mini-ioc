package com.miniioc.util.mapper;

@FunctionalInterface
public interface ParameterMapper {
    Object map(String value);
}
