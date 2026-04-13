package com.solardb.service;

import com.solardb.api.InverterEntryPayload;
import com.solardb.domain.*;
import com.solardb.repo.DeviceRepository;
import com.solardb.repo.ReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class InverterIngestService {
    private static final String DEVICE_TYPE_INVERTER = "INVERTER";

    private final DeviceRepository deviceRepository;
    private final ReadingRepository readingRepository;

    public InverterIngestService(DeviceRepository deviceRepository, ReadingRepository readingRepository) {
        this.deviceRepository = deviceRepository;
        this.readingRepository = readingRepository;
    }

    @Transactional
    public Long ingest(InverterEntryPayload payload) {
        Map<String, Object> f = payload.getFields();

        String serial = coalesceString(f, "InverterSerialNumber", "_inverterSerialNumber", "SerialNumber", "_serialNumber");
        if (serial == null || serial.isBlank()) {
            throw new IllegalArgumentException("Missing serial number (expected InverterSerialNumber or SerialNumber).");
        }

        Device device = deviceRepository
                .findByDeviceTypeAndSerialNumber(DEVICE_TYPE_INVERTER, serial)
                .orElseGet(() -> deviceRepository.save(new Device(DEVICE_TYPE_INVERTER, serial)));

        Instant recordedAt = parseRecordedAt(f).orElseGet(Instant::now);

        Reading r = new Reading(device, recordedAt);
        r.setInverterId(coalesceInt(f, "InverterId", "_inverterId"));
        r.setChargerId(coalesceInt(f, "ChargerId", "_chargerId"));
        r.setMachineType(coalesceString(f, "MachineType", "_machineType"));
        r.setHardwareVersion(coalesceString(f, "HardwareVersion", "_hardwareVersion"));
        r.setSoftwareVersion(coalesceString(f, "SoftwareVersion", "_softwareVersion"));
        r.setInverterMachineType(coalesceString(f, "InverterMachineType", "_inverterMachineType"));
        r.setInverterSerialNumber(coalesceString(f, "InverterSerialNumber", "_inverterSerialNumber"));
        r.setInverterHardwareVersion(coalesceString(f, "InverterHardwareVersion", "_inverterHardwareVersion"));
        r.setInverterSoftwareVersion(coalesceString(f, "InverterSoftwareVersion", "_inverterSoftwareVersion"));

        r.setElectrical(buildElectrical(f));
        r.setState(buildState(f));
        r.setTemperature(buildTemperature(f));
        r.setEnergyAccumulated(buildEnergyAccumulated(f));
        addRelays(r, f);

        return readingRepository.save(r).getId();
    }

    private static Optional<Instant> parseRecordedAt(Map<String, Object> f) {
        String rt = coalesceString(f, "RecordTime", "_recordTime", "recordedAt");
        if (rt == null || rt.isBlank()) return Optional.empty();

        // example uses "0001-01-01T00:00:00" sometimes; treat that as "unknown"
        if (rt.startsWith("0001-01-01")) return Optional.empty();

        try {
            return Optional.of(OffsetDateTime.parse(rt).toInstant());
        } catch (DateTimeParseException ignored) {
        }

        try {
            return Optional.of(Instant.parse(rt));
        } catch (DateTimeParseException ignored) {
        }

        return Optional.empty();
    }

    private static ReadingElectrical buildElectrical(Map<String, Object> f) {
        ReadingElectrical e = ReadingElectrical.create();
        e.setPvVoltageV(coalesceDecimal(f, "PvVoltage", "_pvVoltage"));

        e.setBatteryVoltageV(coalesceDecimal(f, "BatteryVoltage", "_batteryVoltage"));
        e.setBatteryCurrentA(coalesceDecimal(f, "BattCurrent", "_battCurrent"));
        e.setBatteryPowerW(coalesceDecimal(f, "BattPower", "_battPower"));

        e.setBusVoltageV(coalesceDecimal(f, "BusVoltage", "_busVoltage"));
        e.setControlCurrentA(coalesceDecimal(f, "ControlCurrent", "_controlCurrent"));

        e.setChargerCurrentA(coalesceDecimal(f, "ChargerCurrent", "_chargerCurrent"));
        e.setChargerPowerW(coalesceDecimal(f, "ChargerPower", "_chargerPower"));

        e.setInverterVoltageV(coalesceDecimal(f, "InverterVoltage", "_inverterVoltage"));
        e.setInverterCurrentA(coalesceDecimal(f, "InverterCurrent", "_inverterCurrent"));
        e.setInverterFrequencyHz(coalesceDecimal(f, "InverterFrequency", "_inverterFrequency"));

        e.setGridVoltageV(coalesceDecimal(f, "GridVoltage", "_gridVoltage"));
        e.setGridCurrentA(coalesceDecimal(f, "GridCurrent", "_gridCurrent"));
        e.setGridFrequencyHz(coalesceDecimal(f, "GridFrequency", "_gridFrequency"));

        e.setLoadCurrentA(coalesceDecimal(f, "LoadCurrent", "_loadCurrent"));
        e.setLoadPercent(coalesceDecimal(f, "LoadPercent", "_loadPercent"));

        e.setpInverterW(coalesceDecimal(f, "PInverter", "_pInverter"));
        e.setpGridW(coalesceDecimal(f, "PGrid", "_pGrid"));
        e.setpLoadW(coalesceDecimal(f, "PLoad", "_pLoad"));

        e.setsInverterVa(coalesceDecimal(f, "SInverter", "_sInverter"));
        e.setsGridVa(coalesceDecimal(f, "SGrid", "_sGrid"));
        e.setsLoadVa(coalesceDecimal(f, "Sload", "_sload", "SLoad", "_sLoad"));

        e.setqInverterVar(coalesceDecimal(f, "Qinverter", "_qinverter", "QInverter", "_qInverter"));
        e.setqGridVar(coalesceDecimal(f, "Qgrid", "_qgrid", "QGrid", "_qGrid"));
        e.setqLoadVar(coalesceDecimal(f, "Qload", "_qload", "QLoad", "_qLoad"));
        return e;
    }

    private static ReadingState buildState(Map<String, Object> f) {
        ReadingState s = ReadingState.create();
        s.setWorkState(coalesceString(f, "WorkState", "_workState"));
        s.setChargerWorkState(coalesceString(f, "ChargerWorkstate", "_chargerWorkstate"));
        s.setMpptState(coalesceString(f, "MpptState", "_mpptState"));
        s.setChargingState(coalesceString(f, "ChargingState", "_chargingState"));
        s.setErrorMessage(coalesceString(f, "ErrorMessage", "_errorMessage"));
        s.setWarningMessage(coalesceString(f, "WarningMessage", "_warningMessage"));
        s.setInverterErrorMessage(coalesceString(f, "InverterErrorMessage", "_inverterErrorMessage"));
        s.setInverterWarningMessage(coalesceString(f, "InverterWarningMessage", "_inverterWarningMessage"));
        return s;
    }

    private static ReadingTemperature buildTemperature(Map<String, Object> f) {
        ReadingTemperature t = ReadingTemperature.create();
        t.setRadiatorTemperatureC(coalesceDecimal(f, "RadiatorTemperature", "_radiatorTemperature"));
        t.setExternalTemperatureC(coalesceDecimal(f, "ExternalTemperature", "_externalTemperature"));
        t.setAcRadiatorTemperatureC(coalesceDecimal(f, "AcRadiatorTemperature", "_acRadiatorTemperature"));
        t.setDcRadiatorTemperatureC(coalesceDecimal(f, "DcRadiatorTemperature", "_dcRadiatorTemperature"));
        t.setTransformerTemperatureC(coalesceDecimal(f, "TransformerTemperature", "_transformerTemperature"));
        return t;
    }

    private static ReadingEnergyAccumulated buildEnergyAccumulated(Map<String, Object> f) {
        ReadingEnergyAccumulated a = ReadingEnergyAccumulated.create();
        a.setAccumulatedPowerW(coalesceDecimal(f, "AccumulatedPower", "_accumulatedPower"));
        a.setAccumulatedTime(coalesceString(f, "AccumulatedTime", "_accumulatedTime"));
        a.setAccumulatedChargerPowerW(coalesceDecimal(f, "AccumulatedChargerPower", "_accumulatedChargerPower"));
        a.setAccumulatedDischargerPowerW(coalesceDecimal(f, "AccumulatedDischargerPower", "_accumulatedDischargerPower"));
        a.setAccumulatedBuyPowerW(coalesceDecimal(f, "AccumulatedBuyPower", "_accumulatedBuyPower"));
        a.setAccumulatedSellPowerW(coalesceDecimal(f, "AccumulatedSellPower", "_accumulatedSellPower"));
        a.setAccumulatedLoadPowerW(coalesceDecimal(f, "AccumulatedLoadPower", "_accumulatedLoadPower"));
        a.setAccumulatedSelfUsePowerW(coalesceDecimal(f, "AccumulatedSelf_usePower", "_accumulatedSelfUsePower"));
        a.setAccumulatedPvSellPowerW(coalesceDecimal(f, "AccumulatedPV_sellPower", "_accumulatedPvSellPower"));
        a.setAccumulatedGridChargerPowerW(coalesceDecimal(f, "AccumulatedGrid_chargerPower", "_accumulatedGridChargerPower"));
        return a;
    }

    private static void addRelays(Reading r, Map<String, Object> f) {
        putRelay(r, f, RelayName.BATTERY, "BatteryRelay", "_batteryRelay");
        putRelay(r, f, RelayName.PV, "PvRelay", "_pvRelay");
        putRelay(r, f, RelayName.GRID, "GridRelayState", "_gridRelayState");
        putRelay(r, f, RelayName.INVERTER, "InverterRelayState", "_inverterRelayState");
        putRelay(r, f, RelayName.LOAD, "LoadRelayState", "_loadRelayState");
        putRelay(r, f, RelayName.N_LINE, "N_LineRelayState", "_nLineRelayState");
        putRelay(r, f, RelayName.DC, "DCRelayState", "_dcRelayState");
        putRelay(r, f, RelayName.EARTH, "EarthRelayState", "_earthRelayState");
    }

    private static void putRelay(Reading r, Map<String, Object> f, RelayName name, String... keys) {
        String raw = coalesceString(f, keys);
        if (raw == null || raw.isBlank()) return;

        RelayState state = switch (raw.trim().toUpperCase(Locale.ROOT)) {
            case "CONNECT" -> RelayState.CONNECT;
            case "DISCONNECT" -> RelayState.DISCONNECT;
            default -> null;
        };
        if (state == null) return;

        r.addRelay(new ReadingRelay(name, state));
    }

    private static String coalesceString(Map<String, Object> f, String... keys) {
        for (String k : keys) {
            Object v = f.get(k);
            if (v == null) continue;
            String s = String.valueOf(v);
            if (!s.isBlank()) return s;
        }
        return null;
    }

    private static Integer coalesceInt(Map<String, Object> f, String... keys) {
        for (String k : keys) {
            Object v = f.get(k);
            if (v == null) continue;
            if (v instanceof Number n) return n.intValue();
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) continue;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private static BigDecimal coalesceDecimal(Map<String, Object> f, String... keys) {
        for (String k : keys) {
            Object v = f.get(k);
            if (v == null) continue;
            if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
            String s = String.valueOf(v).trim();
            if (s.isEmpty()) continue;
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}

