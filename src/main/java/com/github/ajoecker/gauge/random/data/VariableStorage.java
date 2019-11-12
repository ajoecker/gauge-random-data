package com.github.ajoecker.gauge.random.data;

@FunctionalInterface
public interface VariableStorage {
    void put(String key, Object value);
}
