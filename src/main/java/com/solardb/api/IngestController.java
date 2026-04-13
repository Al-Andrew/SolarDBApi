package com.solardb.api;

import com.solardb.service.InverterIngestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class IngestController {
    private final InverterIngestService ingestService;

    public IngestController(InverterIngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/readings")
    public ResponseEntity<Map<String, Object>> ingest(@Valid @RequestBody InverterEntryPayload payload) {
        long readingId = ingestService.ingest(payload);
        return ResponseEntity
                .created(URI.create("/api/v1/readings/" + readingId))
                .body(Map.of("readingId", readingId));
    }
}

