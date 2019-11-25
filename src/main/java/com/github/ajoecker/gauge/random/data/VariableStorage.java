package com.github.ajoecker.gauge.random.data;

import com.thoughtworks.gauge.datastore.DataStore;

import java.util.Optional;

import static com.thoughtworks.gauge.datastore.DataStoreFactory.*;

public interface VariableStorage {
    void put(String key, Object value);

    Optional<Object> get(String key);

    static VariableStorage create() {
        String clearStateLevel = System.getenv("gauge_clear_state_level");

        if ("suite".equals(clearStateLevel)) {
            return asStorage(getSuiteDataStore());
        } else if ("spec".equals(clearStateLevel)) {
            return asStorage(getSpecDataStore());
        }
        return asStorage(getScenarioDataStore());
    }

    private static VariableStorage asStorage(DataStore dataStore) {
        return new VariableStorage() {
            @Override
            public void put(String key, Object value) {
                dataStore.put(key, value);
            }

            @Override
            public Optional<Object> get(String key) {
                return Optional.ofNullable(dataStore.get(key));
            }
        };
    }

}
