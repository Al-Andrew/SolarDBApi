package com.solardb.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reading_relay", indexes = {
        @Index(name = "reading_relay_reading_idx", columnList = "reading_id")
})
public class ReadingRelay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_id", nullable = false)
    private Reading reading;

    @Enumerated(EnumType.STRING)
    @Column(name = "relay_name", nullable = false)
    private RelayName relayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "relay_state", nullable = false)
    private RelayState relayState;

    protected ReadingRelay() {
    }

    public ReadingRelay(RelayName relayName, RelayState relayState) {
        this.relayName = relayName;
        this.relayState = relayState;
    }

    void setReading(Reading reading) {
        this.reading = reading;
    }
}

