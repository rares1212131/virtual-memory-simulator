package org.example.vmsimulator.domain;

import org.example.vmsimulator.exceptions.WriteProtectionFaultException;
import org.example.vmsimulator.utils.BitUtils;

public class MMU {
    private final TLB tlb;
    private final PageTableDirectory pageDirectory;
    private final OS os;
    private final int offsetBits;

    public MMU(TLB tlb, PageTableDirectory pd, OS os, int offsetBits) {
        this.tlb = tlb;
        this.pageDirectory = pd;
        this.os = os;
        this.offsetBits = offsetBits;
    }

    public PageTableEntry translateAddress(int virtualAddress, boolean isWrite) {
        //takes the virtual address and returns the physical one
        while (true) {
            int vpn = BitUtils.getVpn(virtualAddress, offsetBits);
            TLBEntry tlbEntry = tlb.lookup(vpn); //searches for the fast path

            if (tlbEntry != null) { //checking for stale in tlb, if the page was swapped
                if (tlbEntry.getPageTableEntry().isValidBit()) {
                    updateBitsOnAccess(tlbEntry.getPageTableEntry(), isWrite);
                    return tlbEntry.getPageTableEntry();
                } else {
                    tlb.invalidate(vpn);
                }
            }


            //then searches the page in page table
            PageTableEntry pte = pageDirectory.findPageTableEntry(vpn);

            if (pte == null || !pte.isValidBit()) {
                os.handlePageFault(vpn);
                continue;
            }

            tlb.insert(vpn, pte);
            updateBitsOnAccess(pte, isWrite);
            return pte;
        }
    }


    //updates after every successful command
    private void updateBitsOnAccess(PageTableEntry pte, boolean isWrite) {
        pte.setReferenceBit(true); // recently used for ClockAlgorithm
        pte.setLastAccessTime(System.currentTimeMillis()); // set the last access time
        if (isWrite) {
            if (!pte.isWriteBit()) {
                throw new WriteProtectionFaultException("Write Protection Fault: Write to read-only page.");
            }
            pte.setDirtyBit(true);
        }
    }
}