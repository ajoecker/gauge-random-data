package com.github.ajoecker.gauge.random.data;

import static com.thoughtworks.gauge.datastore.DataStoreFactory.*;

public interface VariableStorage {
    void put(String key, Object value);
    Object get(String key);

    static VariableStorage create() {
        String clearStateLevel = System.getenv("gauge_clear_state_level");
        if ("suite".equals(clearStateLevel)) {
            return new VariableStorage() {
                @Override
                public void put(String key, Object value) {
                    getSuiteDataStore().put(key, value);
                }

                @Override
                public Object get(String key) {
                    return getSuiteDataStore().get(key);
                }
            };
        } else if ("spec".equals(clearStateLevel)) {
            return new VariableStorage() {
                @Override
                public void put(String key, Object value) {
                    getSpecDataStore().put(key, value);
                }

                @Override
                public Object get(String key) {
                    return getSpecDataStore().get(key);
                }
            };
        }
        return new VariableStorage() {
            @Override
            public void put(String key, Object value) {
                getScenarioDataStore().put(key, value);
            }

            @Override
            public Object get(String key) {
                return getScenarioDataStore().get(key);
            }
        };
    }
}
