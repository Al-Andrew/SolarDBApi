package com.solardb.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reading_electrical")
public class ReadingElectrical {
    @Id
    @Column(name = "reading_id")
    private Long readingId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "reading_id")
    private Reading reading;

    @Column(name = "pv_voltage_v", precision = 10, scale = 3)
    private BigDecimal pvVoltageV;

    @Column(name = "battery_voltage_v", precision = 10, scale = 3)
    private BigDecimal batteryVoltageV;

    @Column(name = "battery_current_a", precision = 10, scale = 3)
    private BigDecimal batteryCurrentA;

    @Column(name = "battery_power_w", precision = 12, scale = 3)
    private BigDecimal batteryPowerW;

    @Column(name = "bus_voltage_v", precision = 10, scale = 3)
    private BigDecimal busVoltageV;

    @Column(name = "control_current_a", precision = 10, scale = 3)
    private BigDecimal controlCurrentA;

    @Column(name = "charger_current_a", precision = 10, scale = 3)
    private BigDecimal chargerCurrentA;

    @Column(name = "charger_power_w", precision = 12, scale = 3)
    private BigDecimal chargerPowerW;

    @Column(name = "inverter_voltage_v", precision = 10, scale = 3)
    private BigDecimal inverterVoltageV;

    @Column(name = "inverter_current_a", precision = 10, scale = 3)
    private BigDecimal inverterCurrentA;

    @Column(name = "inverter_frequency_hz", precision = 10, scale = 3)
    private BigDecimal inverterFrequencyHz;

    @Column(name = "grid_voltage_v", precision = 10, scale = 3)
    private BigDecimal gridVoltageV;

    @Column(name = "grid_current_a", precision = 10, scale = 3)
    private BigDecimal gridCurrentA;

    @Column(name = "grid_frequency_hz", precision = 10, scale = 3)
    private BigDecimal gridFrequencyHz;

    @Column(name = "load_current_a", precision = 10, scale = 3)
    private BigDecimal loadCurrentA;

    @Column(name = "load_percent", precision = 10, scale = 3)
    private BigDecimal loadPercent;

    @Column(name = "p_inverter_w", precision = 12, scale = 3)
    private BigDecimal pInverterW;

    @Column(name = "p_grid_w", precision = 12, scale = 3)
    private BigDecimal pGridW;

    @Column(name = "p_load_w", precision = 12, scale = 3)
    private BigDecimal pLoadW;

    @Column(name = "s_inverter_va", precision = 12, scale = 3)
    private BigDecimal sInverterVa;

    @Column(name = "s_grid_va", precision = 12, scale = 3)
    private BigDecimal sGridVa;

    @Column(name = "s_load_va", precision = 12, scale = 3)
    private BigDecimal sLoadVa;

    @Column(name = "q_inverter_var", precision = 12, scale = 3)
    private BigDecimal qInverterVar;

    @Column(name = "q_grid_var", precision = 12, scale = 3)
    private BigDecimal qGridVar;

    @Column(name = "q_load_var", precision = 12, scale = 3)
    private BigDecimal qLoadVar;

    protected ReadingElectrical() {
    }

    public static ReadingElectrical create() {
        return new ReadingElectrical();
    }

    void setReading(Reading reading) {
        this.reading = reading;
    }

    public void setPvVoltageV(BigDecimal pvVoltageV) {
        this.pvVoltageV = pvVoltageV;
    }

    public void setBatteryVoltageV(BigDecimal batteryVoltageV) {
        this.batteryVoltageV = batteryVoltageV;
    }

    public void setBatteryCurrentA(BigDecimal batteryCurrentA) {
        this.batteryCurrentA = batteryCurrentA;
    }

    public void setBatteryPowerW(BigDecimal batteryPowerW) {
        this.batteryPowerW = batteryPowerW;
    }

    public void setBusVoltageV(BigDecimal busVoltageV) {
        this.busVoltageV = busVoltageV;
    }

    public void setControlCurrentA(BigDecimal controlCurrentA) {
        this.controlCurrentA = controlCurrentA;
    }

    public void setChargerCurrentA(BigDecimal chargerCurrentA) {
        this.chargerCurrentA = chargerCurrentA;
    }

    public void setChargerPowerW(BigDecimal chargerPowerW) {
        this.chargerPowerW = chargerPowerW;
    }

    public void setInverterVoltageV(BigDecimal inverterVoltageV) {
        this.inverterVoltageV = inverterVoltageV;
    }

    public void setInverterCurrentA(BigDecimal inverterCurrentA) {
        this.inverterCurrentA = inverterCurrentA;
    }

    public void setInverterFrequencyHz(BigDecimal inverterFrequencyHz) {
        this.inverterFrequencyHz = inverterFrequencyHz;
    }

    public void setGridVoltageV(BigDecimal gridVoltageV) {
        this.gridVoltageV = gridVoltageV;
    }

    public void setGridCurrentA(BigDecimal gridCurrentA) {
        this.gridCurrentA = gridCurrentA;
    }

    public void setGridFrequencyHz(BigDecimal gridFrequencyHz) {
        this.gridFrequencyHz = gridFrequencyHz;
    }

    public void setLoadCurrentA(BigDecimal loadCurrentA) {
        this.loadCurrentA = loadCurrentA;
    }

    public void setLoadPercent(BigDecimal loadPercent) {
        this.loadPercent = loadPercent;
    }

    public void setpInverterW(BigDecimal pInverterW) {
        this.pInverterW = pInverterW;
    }

    public void setpGridW(BigDecimal pGridW) {
        this.pGridW = pGridW;
    }

    public void setpLoadW(BigDecimal pLoadW) {
        this.pLoadW = pLoadW;
    }

    public void setsInverterVa(BigDecimal sInverterVa) {
        this.sInverterVa = sInverterVa;
    }

    public void setsGridVa(BigDecimal sGridVa) {
        this.sGridVa = sGridVa;
    }

    public void setsLoadVa(BigDecimal sLoadVa) {
        this.sLoadVa = sLoadVa;
    }

    public void setqInverterVar(BigDecimal qInverterVar) {
        this.qInverterVar = qInverterVar;
    }

    public void setqGridVar(BigDecimal qGridVar) {
        this.qGridVar = qGridVar;
    }

    public void setqLoadVar(BigDecimal qLoadVar) {
        this.qLoadVar = qLoadVar;
    }
}

