package com.github.ajoecker.gauge.random.data;

import com.thoughtworks.gauge.datastore.DataStore;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.tinylog.Logger;

import java.util.Optional;

import static com.thoughtworks.gauge.datastore.DataStoreFactory.*;

/**
 * A {@link VariableStorage} is depending on the gauge configuration. It checks the <code>gauge_clear_state_level</code>
 * property and initialises the storage based on the configurations value.
 * <p>
 * This means, for a <code>suite</code> clear level, the storage is initialised as {@link DataStoreFactory#getSuiteDataStore()}.
 * For a <code>spec</code> clear level, the storage is initialised as {@link DataStoreFactory#getSpecDataStore()}.
 * For a <code>scenario</code> clear level (and in case non is defined), {@link DataStoreFactory#getScenarioDataStore()} is used.
 */
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

    void print();

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

            @Override
            public void print() {
                Logger.info("variable storage: ");
                dataStore.entrySet().stream().forEach(entry -> Logger.info("{} : {}", entry.getKey(), entry.getValue()));
            }
        };
    }

}
