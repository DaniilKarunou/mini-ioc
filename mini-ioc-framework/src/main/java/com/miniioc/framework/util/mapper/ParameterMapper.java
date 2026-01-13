package com.miniioc.framework.util.mapper;

@FunctionalInterface
public interface ParameterMapper {
    Object map(String value);
}
