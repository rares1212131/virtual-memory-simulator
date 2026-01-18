package org.example.vmsimulator.domain;

import lombok.Getter;
import org.example.vmsimulator.utils.BitUtils;

import java.util.ArrayList;
import java.util.List;

//the relationship between the memory management unit and pages ( to find em )
//will be done thourgh the page table Directory - > page table -> page table entries
public class PageTableDirectory {

    //a page table directory contains a list of page tables
    @Getter
    private final List<PageTable> pageTables;
    @Getter
    private final int directorySize; //how many tables are

    private final int ptiBits;
    @Getter
    private final int pageTableSize;

    public PageTableDirectory(int directorySize, int pageTableSize, int ptiBits) {
        this.directorySize = directorySize;
        this.pageTables = new ArrayList<>(directorySize);
        //initialize with null because only when allocating there will be objects at directory's index
        this.pageTableSize = pageTableSize;
        this.ptiBits = ptiBits;
        for (int i = 0; i < directorySize; i++) {
            this.pageTables.add(null);
        }
    }

    public PageTableEntry findPageTableEntry(int vpn) {
        int pdi = BitUtils.getPdi(vpn, this.ptiBits);
        int pti = BitUtils.getPti(vpn, this.ptiBits);

        PageTable pt = getPageTable(pdi);
        if (pt == null) {
            return null;// The second-level table doesn't exist yet.
        }
        return pt.getEntry(pti);
    }

    public PageTable getPageTable(int pageDirectoryIndex) {
        if (pageDirectoryIndex >= 0 && pageDirectoryIndex < directorySize) {
            return this.pageTables.get(pageDirectoryIndex);
        }
        throw new IndexOutOfBoundsException("Invalid Page Directory Index: " + pageDirectoryIndex);
    }

    //this method is used by the  OS ( in our case the user ) when allocating the memory to set the PageTable valid
    public void setPageTable(int pageDirectoryIndex, PageTable pageTable) {
        if (pageDirectoryIndex >= 0 && pageDirectoryIndex < directorySize) {
            this.pageTables.set(pageDirectoryIndex, pageTable);
        } else {
            throw new IndexOutOfBoundsException("Invalid Page Directory Index: " + pageDirectoryIndex);
        }
    }

}