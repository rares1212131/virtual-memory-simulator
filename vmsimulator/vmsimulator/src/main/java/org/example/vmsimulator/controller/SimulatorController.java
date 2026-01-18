package org.example.vmsimulator.controller;

import org.example.vmsimulator.dto.SimulationStateDTO;
import org.example.vmsimulator.dto.request.MemoryAccessRequest;
import org.example.vmsimulator.dto.request.SimulationConfigRequest;
import org.example.vmsimulator.service.SimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulatorController {

    @Autowired
    private SimulatorService simulatorService;

    @PostMapping("/initialize")
    public ResponseEntity<SimulationStateDTO> initialize(@RequestBody SimulationConfigRequest config) {
        return ResponseEntity.ok(simulatorService.initialize(config));
    }

    @PostMapping("/allocate")
    public ResponseEntity<SimulationStateDTO> allocate(@RequestParam int vpn, @RequestParam(defaultValue = "false") boolean writeProtected) {
        return ResponseEntity.ok(simulatorService.allocatePage(vpn, writeProtected));
    }

    @PostMapping("/access")
    public ResponseEntity<SimulationStateDTO> accessMemory(@RequestBody MemoryAccessRequest request) {
        return ResponseEntity.ok(simulatorService.performMemoryAccess(request));
    }
}