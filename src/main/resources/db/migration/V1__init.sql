-- Normalized schema for inverter snapshots/readings.
-- Focus: store a reading row + grouped detail tables, avoid duplicate/raw naming quirks.

CREATE TABLE device (
    id              BIGSERIAL PRIMARY KEY,
    device_type     TEXT NOT NULL,                 -- e.g. INVERTER
    serial_number   TEXT NOT NULL,
    UNIQUE (device_type, serial_number)
);

CREATE TABLE reading (
    id                  BIGSERIAL PRIMARY KEY,
    device_id            BIGINT NOT NULL REFERENCES device(id),
    recorded_at          TIMESTAMPTZ NOT NULL,

    inverter_id          INTEGER,
    charger_id           INTEGER,

    machine_type         TEXT,
    hardware_version     TEXT,
    software_version     TEXT,

    inverter_machine_type        TEXT,
    inverter_serial_number       TEXT,
    inverter_hardware_version    TEXT,
    inverter_software_version    TEXT
);

CREATE INDEX reading_device_time_idx ON reading (device_id, recorded_at DESC);

CREATE TABLE reading_electrical (
    reading_id               BIGINT PRIMARY KEY REFERENCES reading(id) ON DELETE CASCADE,

    pv_voltage_v             NUMERIC(10,3),

    battery_voltage_v        NUMERIC(10,3),
    battery_current_a        NUMERIC(10,3),
    battery_power_w          NUMERIC(12,3),

    bus_voltage_v            NUMERIC(10,3),
    control_current_a        NUMERIC(10,3),

    charger_current_a        NUMERIC(10,3),
    charger_power_w          NUMERIC(12,3),

    inverter_voltage_v       NUMERIC(10,3),
    inverter_current_a       NUMERIC(10,3),
    inverter_frequency_hz    NUMERIC(10,3),

    grid_voltage_v           NUMERIC(10,3),
    grid_current_a           NUMERIC(10,3),
    grid_frequency_hz        NUMERIC(10,3),

    load_current_a           NUMERIC(10,3),
    load_percent             NUMERIC(10,3),

    p_inverter_w             NUMERIC(12,3),
    p_grid_w                 NUMERIC(12,3),
    p_load_w                 NUMERIC(12,3),

    s_inverter_va            NUMERIC(12,3),
    s_grid_va                NUMERIC(12,3),
    s_load_va                NUMERIC(12,3),

    q_inverter_var           NUMERIC(12,3),
    q_grid_var               NUMERIC(12,3),
    q_load_var               NUMERIC(12,3)
);

CREATE TABLE reading_state (
    reading_id               BIGINT PRIMARY KEY REFERENCES reading(id) ON DELETE CASCADE,

    work_state               TEXT,
    charger_work_state       TEXT,
    mppt_state               TEXT,
    charging_state           TEXT,

    error_message            TEXT,
    warning_message          TEXT,
    inverter_error_message   TEXT,
    inverter_warning_message TEXT
);

CREATE TABLE reading_temperature (
    reading_id               BIGINT PRIMARY KEY REFERENCES reading(id) ON DELETE CASCADE,

    radiator_temperature_c        NUMERIC(10,3),
    external_temperature_c        NUMERIC(10,3),
    ac_radiator_temperature_c     NUMERIC(10,3),
    dc_radiator_temperature_c     NUMERIC(10,3),
    transformer_temperature_c     NUMERIC(10,3)
);

CREATE TABLE reading_relay (
    id              BIGSERIAL PRIMARY KEY,
    reading_id      BIGINT NOT NULL REFERENCES reading(id) ON DELETE CASCADE,
    relay_name      TEXT NOT NULL,       -- e.g. BATTERY, PV, GRID, INVERTER, LOAD, N_LINE, DC, EARTH
    relay_state     TEXT NOT NULL        -- e.g. CONNECT, DISCONNECT
);

CREATE INDEX reading_relay_reading_idx ON reading_relay (reading_id);

CREATE TABLE reading_energy_accumulated (
    reading_id                      BIGINT PRIMARY KEY REFERENCES reading(id) ON DELETE CASCADE,

    accumulated_power_w             NUMERIC(14,3),
    accumulated_time                TEXT,

    accumulated_charger_power_w     NUMERIC(14,3),
    accumulated_discharger_power_w  NUMERIC(14,3),
    accumulated_buy_power_w         NUMERIC(14,3),
    accumulated_sell_power_w        NUMERIC(14,3),
    accumulated_load_power_w        NUMERIC(14,3),
    accumulated_self_use_power_w    NUMERIC(14,3),
    accumulated_pv_sell_power_w     NUMERIC(14,3),
    accumulated_grid_charger_power_w NUMERIC(14,3)
);

