package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiskPageDTO {
    private int vpn;
    private String status; // "Unallocated", "On Disk", "In Memory (Frame X)"
    private boolean writeProtected;
}