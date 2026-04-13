package com.solardb.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "device",
        uniqueConstraints = @UniqueConstraint(name = "device_type_serial_number_uk", columnNames = {"device_type", "serial_number"})
)
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    protected Device() {
    }

    public Device(String deviceType, String serialNumber) {
        this.deviceType = deviceType;
        this.serialNumber = serialNumber;
    }

    public Long getId() {
        return id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}

