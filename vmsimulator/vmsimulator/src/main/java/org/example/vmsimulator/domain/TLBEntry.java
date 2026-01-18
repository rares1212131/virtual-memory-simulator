package org.example.vmsimulator.domain;

import lombok.Getter;

@Getter
public class TLBEntry {

    //vpn field to store the full Virtual Page Number.
    //This is purely for visualization purposes and doesn't affect the core logic.
    private final int vpn;
    private final int tag;
    private final int physicalPageNumber;
    private final PageTableEntry pageTableEntry;

    /**
     * Constructor for a new TLB Entry.
     * @param vpn The full Virtual Page Number.
     * @param tag The tag calculated from the VPN.
     * @param pageTableEntry The complete PageTableEntry from the main page table.
     */
    public TLBEntry(int vpn, int tag, PageTableEntry pageTableEntry) {
        this.vpn = vpn;
        this.tag = tag;
        this.pageTableEntry = pageTableEntry;
        this.physicalPageNumber = pageTableEntry.getPhysicalPageNumber();
    }
}