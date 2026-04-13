package com.solardb.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reading_energy_accumulated")
public class ReadingEnergyAccumulated {
    @Id
    @Column(name = "reading_id")
    private Long readingId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "reading_id")
    private Reading reading;

    @Column(name = "accumulated_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedPowerW;

    @Column(name = "accumulated_time")
    private String accumulatedTime;

    @Column(name = "accumulated_charger_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedChargerPowerW;

    @Column(name = "accumulated_discharger_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedDischargerPowerW;

    @Column(name = "accumulated_buy_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedBuyPowerW;

    @Column(name = "accumulated_sell_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedSellPowerW;

    @Column(name = "accumulated_load_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedLoadPowerW;

    @Column(name = "accumulated_self_use_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedSelfUsePowerW;

    @Column(name = "accumulated_pv_sell_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedPvSellPowerW;

    @Column(name = "accumulated_grid_charger_power_w", precision = 14, scale = 3)
    private BigDecimal accumulatedGridChargerPowerW;

    protected ReadingEnergyAccumulated() {
    }

    public static ReadingEnergyAccumulated create() {
        return new ReadingEnergyAccumulated();
    }

    void setReading(Reading reading) {
        this.reading = reading;
    }

    public void setAccumulatedPowerW(BigDecimal accumulatedPowerW) {
        this.accumulatedPowerW = accumulatedPowerW;
    }

    public void setAccumulatedTime(String accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public void setAccumulatedChargerPowerW(BigDecimal accumulatedChargerPowerW) {
        this.accumulatedChargerPowerW = accumulatedChargerPowerW;
    }

    public void setAccumulatedDischargerPowerW(BigDecimal accumulatedDischargerPowerW) {
        this.accumulatedDischargerPowerW = accumulatedDischargerPowerW;
    }

    public void setAccumulatedBuyPowerW(BigDecimal accumulatedBuyPowerW) {
        this.accumulatedBuyPowerW = accumulatedBuyPowerW;
    }

    public void setAccumulatedSellPowerW(BigDecimal accumulatedSellPowerW) {
        this.accumulatedSellPowerW = accumulatedSellPowerW;
    }

    public void setAccumulatedLoadPowerW(BigDecimal accumulatedLoadPowerW) {
        this.accumulatedLoadPowerW = accumulatedLoadPowerW;
    }

    public void setAccumulatedSelfUsePowerW(BigDecimal accumulatedSelfUsePowerW) {
        this.accumulatedSelfUsePowerW = accumulatedSelfUsePowerW;
    }

    public void setAccumulatedPvSellPowerW(BigDecimal accumulatedPvSellPowerW) {
        this.accumulatedPvSellPowerW = accumulatedPvSellPowerW;
    }

    public void setAccumulatedGridChargerPowerW(BigDecimal accumulatedGridChargerPowerW) {
        this.accumulatedGridChargerPowerW = accumulatedGridChargerPowerW;
    }
}

