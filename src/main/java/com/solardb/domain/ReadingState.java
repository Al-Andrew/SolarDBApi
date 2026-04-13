package com.solardb.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reading_state")
public class ReadingState {
    @Id
    @Column(name = "reading_id")
    private Long readingId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "reading_id")
    private Reading reading;

    @Column(name = "work_state")
    private String workState;

    @Column(name = "charger_work_state")
    private String chargerWorkState;

    @Column(name = "mppt_state")
    private String mpptState;

    @Column(name = "charging_state")
    private String chargingState;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "warning_message")
    private String warningMessage;

    @Column(name = "inverter_error_message")
    private String inverterErrorMessage;

    @Column(name = "inverter_warning_message")
    private String inverterWarningMessage;

    protected ReadingState() {
    }

    public static ReadingState create() {
        return new ReadingState();
    }

    void setReading(Reading reading) {
        this.reading = reading;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public void setChargerWorkState(String chargerWorkState) {
        this.chargerWorkState = chargerWorkState;
    }

    public void setMpptState(String mpptState) {
        this.mpptState = mpptState;
    }

    public void setChargingState(String chargingState) {
        this.chargingState = chargingState;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public void setInverterErrorMessage(String inverterErrorMessage) {
        this.inverterErrorMessage = inverterErrorMessage;
    }

    public void setInverterWarningMessage(String inverterWarningMessage) {
        this.inverterWarningMessage = inverterWarningMessage;
    }
}

