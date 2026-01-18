package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationStateDTO {
    private List<TlbSetDTO> tlb;
    private List<FrameDTO> physicalMemory;
    private List<PageTableDTO> pageTables;
    private List<DiskPageDTO> disk;
    private List<String> simulationMessages;
    private int tlbHits;
    private int tlbMisses;
    private int pageFaults;
    private int pageTableHits;
}