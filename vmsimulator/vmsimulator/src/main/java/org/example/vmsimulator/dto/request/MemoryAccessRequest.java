package org.example.vmsimulator.dto.request;

import lombok.Data;

@Data
public class MemoryAccessRequest {
    private int virtualAddress;

    // Changed from 'isWrite' to 'write' to ensure JSON maps correctly
    private boolean write;

    private int value;
}