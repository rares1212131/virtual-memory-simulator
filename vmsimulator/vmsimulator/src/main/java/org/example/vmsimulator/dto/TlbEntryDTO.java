package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vmsimulator.domain.TLBEntry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TlbEntryDTO {
    private int vpn;
    private int tag;
    private int physicalPageNumber;
    private PageTableEntryDTO pageTableEntry; // To show the bits (V, D, R, W)

    // CHANGE: Updated constructor to take the full domain object
    // and create a nested PageTableEntryDTO for complete visualization.
    public TlbEntryDTO(TLBEntry tlbEntry) {
        this.vpn = tlbEntry.getVpn();
        this.tag = tlbEntry.getTag();
        this.physicalPageNumber = tlbEntry.getPhysicalPageNumber();
        this.pageTableEntry = new PageTableEntryDTO(tlbEntry.getPageTableEntry());
    }
}