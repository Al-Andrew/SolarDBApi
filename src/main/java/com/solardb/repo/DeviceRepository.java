package com.solardb.repo;

import com.solardb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceTypeAndSerialNumber(String deviceType, String serialNumber);
}

