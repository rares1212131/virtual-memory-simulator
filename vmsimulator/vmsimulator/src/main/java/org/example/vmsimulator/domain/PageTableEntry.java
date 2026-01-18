package org.example.vmsimulator.domain;

import lombok.Getter;
import lombok.Setter;

//this class represents the single line of a page table which has the connection between the virtual page and the physical page
@Getter
@Setter
public class PageTableEntry {


    private boolean allocated = false; // to check if the page is allocated
    private boolean validBit = false;   // valid bit - if the page is in ram or not
    private boolean dirtyBit = false;   // dirty bit - if the page was written to or not
    private boolean referenceBit = false;  // refference bit - if the page was recently modified/accessed
    private boolean writeBit = false; // write bit - if the page is writable
    private long lastAccessTime = 0; // for the lru algorithm
    private long loadTime = 0;       //load time for the fifo algorithm
    private int physicalPageNumber = -1;//this represents the frame number of the virtual page when the page is in RAM
    private int swapSpaceNumber = -1; // this represents the place on the disk where the copy of the page is situated;


    public PageTableEntry() {
        //default constructor which initialize everything as it is
    }

}