package org.example.vmsimulator.service;

import org.example.vmsimulator.domain.*;
import org.example.vmsimulator.domain.algorithms.*;
import org.example.vmsimulator.dto.*;
import org.example.vmsimulator.dto.request.MemoryAccessRequest;
import org.example.vmsimulator.dto.request.SimulationConfigRequest;
import org.example.vmsimulator.exceptions.SegmentationFaultException;
import org.example.vmsimulator.utils.BitUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
public class SimulatorService {

    private PhysicalMemory physicalMemory;
    private PageTableDirectory pageDirectory;
    private TLB tlb;
    private OS os;
    private MMU mmu;
    private SimulationConfigRequest config;

    private int offsetBits, totalPages, pageSize;
    private int tlbHits = 0, tlbMisses = 0;
    private int pageTableHits = 0;
    private boolean isInitialized = false;

    public SimulationStateDTO initialize(SimulationConfigRequest config) {
        this.config = config; // Save config

        int virtualAddressWidth = config.getVirtualAddressWidth();
        this.pageSize = config.getPageSize();
        this.offsetBits = (int) (Math.log(pageSize) / Math.log(2));

        int vpnBits = virtualAddressWidth - offsetBits;
        this.totalPages = 1 << vpnBits;

        int ptiBits = vpnBits / 2;
        int pdiBits = vpnBits - ptiBits;
        int directorySize = 1 << pdiBits;
        int pageTableSize = 1 << ptiBits;
        int numPhysicalFrames = config.getPhysicalMemorySize() / pageSize;

        this.physicalMemory = new PhysicalMemory(numPhysicalFrames, pageSize);
        this.pageDirectory = new PageTableDirectory(directorySize, pageTableSize, ptiBits);
        this.tlb = new TLB(config.getTlbSize(), config.getTlbAssociativity(), vpnBits);

        IPageReplacementAlgorithm algorithm;
        switch (config.getAlgorithm().toUpperCase()) {
            case "FIFO" -> algorithm = new FIFOAlgorithm();
            case "LRU" -> algorithm = new LRUAlgorithm();
            case "CLOCK" -> algorithm = new ClockAlgorithm();
            default -> algorithm = new RandomAlgorithm();
        }

        this.os = new OS(physicalMemory, pageDirectory, algorithm, ptiBits);
        this.mmu = new MMU(tlb, pageDirectory, os, offsetBits);

        this.tlbHits = 0; this.tlbMisses = 0; this.pageTableHits = 0;
        this.isInitialized = true;
        return createSimulationStateDTO(List.of("System initialized with " + config.getAlgorithm() + "."));
    }

    public SimulationStateDTO allocatePage(int vpn, boolean isWriteProtected) {
        if (!isInitialized) throw new IllegalStateException("Not initialized.");
        os.allocatePageOnDisk(vpn);
        PageTableEntry pte = pageDirectory.findPageTableEntry(vpn);
        if (pte != null) pte.setWriteBit(!isWriteProtected);
        return createSimulationStateDTO(List.of("Page " + vpn + " allocated."));
    }

    public SimulationStateDTO performMemoryAccess(MemoryAccessRequest request) {
        if (!isInitialized) throw new IllegalStateException("Simulator not initialized.");

        List<String> messages = new ArrayList<>();
        int va = request.getVirtualAddress();

        // --- STEP 1: DECODE THE ADDRESS ---
        String binaryAddr = String.format("%12s", Integer.toBinaryString(va)).replace(' ', '0');

        int vpn = BitUtils.getVpn(va, offsetBits);
        int offset = BitUtils.getOffset(va, offsetBits);

        // Calculate PDI and PTI for visualization
        int ptiBits = (config.getVirtualAddressWidth() - offsetBits) / 2;
        int pdi = BitUtils.getPdi(vpn, ptiBits);
        int pti = BitUtils.getPti(vpn, ptiBits);

        messages.add("----------------------------------------------------------------");
        messages.add(String.format("1. ADDRESS DECODING: 0x%X (Binary: %s)", va, binaryAddr));
        messages.add(String.format("   -> VPN: %d (Dec) | Offset: %d (Dec)", vpn, offset));
        messages.add(String.format("   -> Hierarchy: PDI [%d] -> PTI [%d]", pdi, pti));

        int pageFaultsBefore = os.getPageFaultCount();

        messages.add("2. TLB LOOKUP (Associative Search):");

        int numSets = tlb.getSets().length;
        // TLB Index = VPN % Sets
        int tlbSetIdx = vpn % numSets;
        // TLB Tag = VPN / Sets (Integer division shifts bits)
        int tlbTag = vpn / numSets;

        messages.add(String.format("   -> CALCULATION: VPN %d", vpn));
        messages.add(String.format("      - Index (VPN %% %d) = Set %d", numSets, tlbSetIdx));
        messages.add(String.format("      - Tag   (VPN / %d) = %d", numSets, tlbTag));

        messages.add(String.format("   -> ACTION: Searching Set %d for Tag %d...", tlbSetIdx, tlbTag));

        Map<Integer, TLBEntry> setEntries = tlb.getSets()[tlbSetIdx];
        if (setEntries.isEmpty()) {
            messages.add("      - Set is EMPTY.");
        } else {
            for (TLBEntry entry : setEntries.values()) {
                String match = (entry.getTag() == tlbTag) ? "MATCH!" : "No Match";
                messages.add(String.format("      - Compare Entry [Tag %d] vs [Target %d] -> %s", entry.getTag(), tlbTag, match));
            }
        }

        boolean wasTlbHit = tlb.lookup(vpn) != null;
        if (wasTlbHit) {
            messages.add("   -> RESULT: TLB HIT! (Found PPN immediately)");
            tlbHits++;
        } else {
            messages.add("   -> RESULT: TLB MISS. (Must proceed to Page Table)");
            tlbMisses++;
        }

        try {
            if (!wasTlbHit) {
                messages.add("3. PAGE TABLE WALK (Slow Path):");
                messages.add(String.format("   -> L1: Checking Page Directory Index %d...", pdi));
                PageTable pt = pageDirectory.getPageTable(pdi);
                if (pt == null) {
                    messages.add("   -> L1 Result: NULL (Directory Entry Empty)");
                } else {
                    messages.add(String.format("   -> L1 Result: Found Table. Checking L2 Index %d...", pti));
                }
            }

            PageTableEntry pte = mmu.translateAddress(va, request.isWrite());
            int ppn = pte.getPhysicalPageNumber();
            int physicalAddress = (ppn << offsetBits) | offset;

            if (!wasTlbHit && os.getPageFaultCount() > pageFaultsBefore) {
                messages.add("   -> L2 Result: INVALID (Valid Bit = 0)");
                messages.add("   -> ACTION: *** PAGE FAULT TRIGGERED ***");
                messages.add("   -> OS: Allocating Frame -> Loading Disk Data -> Updating Page Table");
                messages.add("   -> RETRY: Page Table Walk successful (Valid Bit = 1).");
            } else if (!wasTlbHit) {
                messages.add("   -> L2 Result: VALID (Valid Bit = 1). Frame found.");
                pageTableHits++;
            }

            messages.add("4. PHYSICAL ACCESS:");
            messages.add(String.format("   -> Phys Addr: 0x%X (Frame %d + Offset %d)", physicalAddress, ppn, offset));

            Frame frame = physicalMemory.getFrame(ppn);
            if (request.isWrite()) {
                frame.writeByte(offset, (byte) request.getValue());
                messages.add(String.format("   -> OP: WRITE 0x%X", request.getValue()));
                messages.add("   -> STATE: Dirty Bit set to 1");
            } else {
                byte val = frame.readByte(offset);
                messages.add(String.format("   -> OP: READ Value 0x%02X", val));
            }

        } catch (SegmentationFaultException e) {
            messages.add("   -> RESULT: SEGMENTATION FAULT!");
            messages.add("   -> Reason: " + e.getMessage());
            if (!wasTlbHit) tlbMisses++;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createSimulationStateDTO(messages);
    }

    private SimulationStateDTO createSimulationStateDTO(List<String> messages) {
        if (!isInitialized) return null;

        List<FrameDTO> frameDTOs = new ArrayList<>();
        physicalMemory.getFrames().forEach(frame -> {
            int[] intData = new int[this.pageSize];
            for(int i = 0; i < this.pageSize; i++) intData[i] = frame.readByte(i) & 0xFF;
            frameDTOs.add(new FrameDTO(physicalMemory.getFrames().indexOf(frame), frame.getOccupiedByVpn(), intData));
        });

        List<PageTableDTO> pageTableDTOs = new ArrayList<>();
        for (int pdi = 0; pdi < pageDirectory.getDirectorySize(); pdi++) {
            PageTable pt = pageDirectory.getPageTable(pdi);
            List<PageTableEntryDTO> pteDTOs = new ArrayList<>();
            if (pt != null) pt.getEntries().forEach(pte -> pteDTOs.add(new PageTableEntryDTO(pte)));
            pageTableDTOs.add(new PageTableDTO(pdi, pt != null, pteDTOs));
        }

        List<DiskPageDTO> diskDTOs = new ArrayList<>();
        for (int i = 0; i < this.totalPages; i++) {
            PageTableEntry pte = pageDirectory.findPageTableEntry(i);
            String status;
            boolean writeProtected = true;
            if (pte == null || !pte.isAllocated()) status = "Unallocated";
            else {
                writeProtected = !pte.isWriteBit();
                status = pte.isValidBit() ? "In Memory (Frame " + pte.getPhysicalPageNumber() + ")" : "On Disk";
            }
            diskDTOs.add(new DiskPageDTO(i, status, writeProtected));
        }

        List<TlbSetDTO> tlbSets = new ArrayList<>();
        Map<Integer, TLBEntry>[] sets = tlb.getSets();
        for (int i = 0; i < sets.length; i++) {
            List<TlbEntryDTO> entries = new ArrayList<>();
            sets[i].values().forEach(entry -> entries.add(new TlbEntryDTO(entry)));
            tlbSets.add(new TlbSetDTO(i, entries));
        }
        return new SimulationStateDTO(tlbSets, frameDTOs, pageTableDTOs, diskDTOs, messages, tlbHits, tlbMisses, os.getPageFaultCount(), this.pageTableHits);
    }
}