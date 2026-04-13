package com.solardb.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reading_temperature")
public class ReadingTemperature {
    @Id
    @Column(name = "reading_id")
    private Long readingId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "reading_id")
    private Reading reading;

    @Column(name = "radiator_temperature_c", precision = 10, scale = 3)
    private BigDecimal radiatorTemperatureC;

    @Column(name = "external_temperature_c", precision = 10, scale = 3)
    private BigDecimal externalTemperatureC;

    @Column(name = "ac_radiator_temperature_c", precision = 10, scale = 3)
    private BigDecimal acRadiatorTemperatureC;

    @Column(name = "dc_radiator_temperature_c", precision = 10, scale = 3)
    private BigDecimal dcRadiatorTemperatureC;

    @Column(name = "transformer_temperature_c", precision = 10, scale = 3)
    private BigDecimal transformerTemperatureC;

    protected ReadingTemperature() {
    }

    public static ReadingTemperature create() {
        return new ReadingTemperature();
    }

    void setReading(Reading reading) {
        this.reading = reading;
    }

    public void setRadiatorTemperatureC(BigDecimal radiatorTemperatureC) {
        this.radiatorTemperatureC = radiatorTemperatureC;
    }

    public void setExternalTemperatureC(BigDecimal externalTemperatureC) {
        this.externalTemperatureC = externalTemperatureC;
    }

    public void setAcRadiatorTemperatureC(BigDecimal acRadiatorTemperatureC) {
        this.acRadiatorTemperatureC = acRadiatorTemperatureC;
    }

    public void setDcRadiatorTemperatureC(BigDecimal dcRadiatorTemperatureC) {
        this.dcRadiatorTemperatureC = dcRadiatorTemperatureC;
    }

    public void setTransformerTemperatureC(BigDecimal transformerTemperatureC) {
        this.transformerTemperatureC = transformerTemperatureC;
    }
}

