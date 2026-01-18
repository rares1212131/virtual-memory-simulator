package org.example.vmsimulator.domain;

import lombok.Getter;
import org.example.vmsimulator.domain.algorithms.IPageReplacementAlgorithm;
import org.example.vmsimulator.exceptions.SegmentationFaultException;
import org.example.vmsimulator.utils.BitUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random; // Import Random

public class OS {
    private final PhysicalMemory physicalMemory;
    private final PageTableDirectory pageDirectory;
    private final IPageReplacementAlgorithm replacementAlgorithm;
    private int nextAvailableSwapSpaceNumber = 0;
    private final int ptiBits;

    // The "Hard Drive"
    private final Map<Integer, byte[]> backingStore = new HashMap<>();

    @Getter
    private int pageFaultCount = 0;

    public OS(PhysicalMemory pm, PageTableDirectory pd, IPageReplacementAlgorithm alg, int ptiBits) {
        this.physicalMemory = pm;
        this.pageDirectory = pd;
        this.replacementAlgorithm = alg;
        this.ptiBits = ptiBits;
    }

    public void allocatePageOnDisk(int vpn) {
        PageTableEntry pte = pageDirectory.findPageTableEntry(vpn);
        if (pte == null) {
            //here is created the page table directory, the page table , and then taken the pagetableentry but here is before assigning to ram
            int pdi = BitUtils.getPdi(vpn, this.ptiBits);
            PageTable newTable = new PageTable(pageDirectory.getPageTableSize());
            pageDirectory.setPageTable(pdi, newTable);
            pte = pageDirectory.findPageTableEntry(vpn);
        }
        if (pte.isAllocated()) return;
        //sets the page to be alocated
        pte.setAllocated(true);
        pte.setSwapSpaceNumber(nextAvailableSwapSpaceNumber++);


        //creates the mockup data for the virtual page
        int pageSize = physicalMemory.getFrames().get(0).getData().length;
        byte[] initialData = new byte[pageSize];

        new Random().nextBytes(initialData);

        //this saves on the disk at first allocation soo that when reading/writing from it to have  the data.
        backingStore.put(vpn, initialData);
    }

    public void handlePageFault(int vpn) {
        this.pageFaultCount++;
        PageTableEntry pte = pageDirectory.findPageTableEntry(vpn);
        if (pte == null || !pte.isAllocated()) {
            throw new SegmentationFaultException("Segfault: Access to unallocated memory at VPN " + vpn);
        } //this throws s segmentation error if the page that we try to read/write is not allocated
        int frameNum = physicalMemory.findFreeFrame();
        if (frameNum == -1) {  // if no page available, we swap
            frameNum = replacementAlgorithm.findVictim(physicalMemory, pageDirectory);
            handleVictim(frameNum);
        }
        loadPageIntoFrame(vpn, frameNum);
    }

    private void handleVictim(int victimPpn) {

        //identify the victim
        Frame victimFrame = physicalMemory.getFrame(victimPpn);
        int victimVpn = victimFrame.getOccupiedByVpn();

        //get page table entry
        PageTableEntry victimPte = pageDirectory.findPageTableEntry(victimVpn);

        // Write-Back Logic
        if (victimPte.isDirtyBit()) {
            //if data has been modified, we save the data from ram back to the disk
            System.out.println("OS: Victim " + victimVpn + " is dirty. Saving data to backing store.");
            byte[] dataToSave = Arrays.copyOf(victimFrame.getData(), victimFrame.getData().length);
            backingStore.put(victimVpn, dataToSave);
        }

        victimPte.setValidBit(false);   //no longer in ram
        victimPte.setPhysicalPageNumber(-1);  //no frame
        victimPte.setDirtyBit(false);  //reset status
        victimPte.setReferenceBit(false);
        victimFrame.setOccupiedByVpn(-1);
    }

    private void loadPageIntoFrame(int vpn, int frameNum) {
        Frame targetFrame = physicalMemory.getFrame(frameNum);

        // Load from Disk
        if (backingStore.containsKey(vpn)) {
            byte[] dataFromDisk = backingStore.get(vpn);
            System.arraycopy(dataFromDisk, 0, targetFrame.getData(), 0, dataFromDisk.length);
        } else {
            // Should not happen now given allocate logic, but safety first
            Arrays.fill(targetFrame.getData(), (byte) 0);
        }

        PageTableEntry pte = pageDirectory.findPageTableEntry(vpn);
        targetFrame.setOccupiedByVpn(vpn);  // set the physical page number to the virtual page number.
        pte.setValidBit(true);
        pte.setPhysicalPageNumber(frameNum);
        pte.setReferenceBit(true);
        pte.setDirtyBit(false);
        pte.setLoadTime(System.currentTimeMillis());
        pte.setLastAccessTime(System.currentTimeMillis());
    }
}