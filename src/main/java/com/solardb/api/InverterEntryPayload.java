package com.solardb.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Payload is not stable: example.json contains duplicate fields in different naming styles.
 * We ingest everything into a key/value map and then coalesce known keys.
 */
@Schema(
        description = "Flat JSON object from the inverter export; keys are normalized during ingest.",
        additionalProperties = Schema.AdditionalPropertiesValue.TRUE
)
public class InverterEntryPayload {
    private final Map<String, Object> fields = new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        fields.put(key, value);
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}

