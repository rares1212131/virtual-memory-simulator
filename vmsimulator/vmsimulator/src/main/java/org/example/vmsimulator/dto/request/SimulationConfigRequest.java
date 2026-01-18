// FILE: D:\PROJECTSCS\vmsimulator\vmsimulator\src\main\java\org\example\vmsimulator\dto\request\SimulationConfigRequest.java
package org.example.vmsimulator.dto.request;

import lombok.Data;

@Data
public class SimulationConfigRequest {
    private int virtualAddressWidth;
    private int pageSize;
    private int tlbSize;
    private int tlbAssociativity;
    private int physicalMemorySize;
    private String algorithm; // "FIFO", "LRU", "Clock", "Random"
}