package com.solardb.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading", indexes = {
        @Index(name = "reading_device_time_idx", columnList = "device_id, recorded_at DESC")
})
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    @Column(name = "inverter_id")
    private Integer inverterId;

    @Column(name = "charger_id")
    private Integer chargerId;

    @Column(name = "machine_type")
    private String machineType;

    @Column(name = "hardware_version")
    private String hardwareVersion;

    @Column(name = "software_version")
    private String softwareVersion;

    @Column(name = "inverter_machine_type")
    private String inverterMachineType;

    @Column(name = "inverter_serial_number")
    private String inverterSerialNumber;

    @Column(name = "inverter_hardware_version")
    private String inverterHardwareVersion;

    @Column(name = "inverter_software_version")
    private String inverterSoftwareVersion;

    @OneToOne(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReadingElectrical electrical;

    @OneToOne(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReadingState state;

    @OneToOne(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReadingTemperature temperature;

    @OneToOne(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReadingEnergyAccumulated energyAccumulated;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingRelay> relays = new ArrayList<>();

    protected Reading() {
    }

    public Reading(Device device, Instant recordedAt) {
        this.device = device;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public Integer getInverterId() {
        return inverterId;
    }

    public void setInverterId(Integer inverterId) {
        this.inverterId = inverterId;
    }

    public Integer getChargerId() {
        return chargerId;
    }

    public void setChargerId(Integer chargerId) {
        this.chargerId = chargerId;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getInverterMachineType() {
        return inverterMachineType;
    }

    public void setInverterMachineType(String inverterMachineType) {
        this.inverterMachineType = inverterMachineType;
    }

    public String getInverterSerialNumber() {
        return inverterSerialNumber;
    }

    public void setInverterSerialNumber(String inverterSerialNumber) {
        this.inverterSerialNumber = inverterSerialNumber;
    }

    public String getInverterHardwareVersion() {
        return inverterHardwareVersion;
    }

    public void setInverterHardwareVersion(String inverterHardwareVersion) {
        this.inverterHardwareVersion = inverterHardwareVersion;
    }

    public String getInverterSoftwareVersion() {
        return inverterSoftwareVersion;
    }

    public void setInverterSoftwareVersion(String inverterSoftwareVersion) {
        this.inverterSoftwareVersion = inverterSoftwareVersion;
    }

    public ReadingElectrical getElectrical() {
        return electrical;
    }

    public void setElectrical(ReadingElectrical electrical) {
        this.electrical = electrical;
        if (electrical != null) {
            electrical.setReading(this);
        }
    }

    public ReadingState getState() {
        return state;
    }

    public void setState(ReadingState state) {
        this.state = state;
        if (state != null) {
            state.setReading(this);
        }
    }

    public ReadingTemperature getTemperature() {
        return temperature;
    }

    public void setTemperature(ReadingTemperature temperature) {
        this.temperature = temperature;
        if (temperature != null) {
            temperature.setReading(this);
        }
    }

    public ReadingEnergyAccumulated getEnergyAccumulated() {
        return energyAccumulated;
    }

    public void setEnergyAccumulated(ReadingEnergyAccumulated energyAccumulated) {
        this.energyAccumulated = energyAccumulated;
        if (energyAccumulated != null) {
            energyAccumulated.setReading(this);
        }
    }

    public List<ReadingRelay> getRelays() {
        return relays;
    }

    public void addRelay(ReadingRelay relay) {
        relay.setReading(this);
        this.relays.add(relay);
    }
}

