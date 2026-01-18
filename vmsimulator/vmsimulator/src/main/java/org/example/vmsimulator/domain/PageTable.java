package org.example.vmsimulator.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

//this is the connection between the page table and the page table entry, it holds
//an entry for every virtual page wh
public class PageTable {

    @Getter
    private final List<PageTableEntry> entries;
    private final int tableSize;

    public PageTable(int tableSize) {
        this.tableSize = tableSize;
        this.entries = new ArrayList<>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            this.entries.add(new PageTableEntry());
        }
    }

    public PageTableEntry getEntry(int pageTableIndex) {//the page table index is got from the address, from which is taken the directory table index also
        if (pageTableIndex >= 0 && pageTableIndex < tableSize) {
            return this.entries.get(pageTableIndex);
        }
        throw new IndexOutOfBoundsException("Invalid Page Table Index: " + pageTableIndex);
    }
}