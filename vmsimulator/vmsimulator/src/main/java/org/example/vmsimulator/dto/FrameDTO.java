// FILE: D:\PROJECTSCS\vmsimulator\vmsimulator\src\main\java\org\example\vmsimulator\dto\FrameDTO.java
package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrameDTO {
    private int id; // The PPN
    private int occupiedByVpn;
    private int[] data; // e.g., a few bytes represented as Hex
}