package org.example.vmsimulator.domain;

import lombok.Getter;
import java.util.LinkedHashMap;
import java.util.Map;

public class TLB {

    @Getter
    private final Map<Integer, TLBEntry>[] sets;
    private final int numSets;
    private final int associativity;
    private final int indexBits;
    private final int indexMask;

    public TLB(int size, int associativity, int vpnBits) {
        this.associativity = associativity;
        if (associativity <= 0) throw new IllegalArgumentException("Associativity must be positive.");
        if (size <= 0 || size % associativity != 0) throw new IllegalArgumentException("Size must be a multiple of associativity.");

        this.numSets = size / associativity;
        // Calculate bits needed to index the sets (e.g. 4 sets = 2 bits)
        this.indexBits = (int) (Math.log(numSets) / Math.log(2));
        this.indexMask = (1 << indexBits) - 1;

        this.sets = new LinkedHashMap[numSets];
        for (int i = 0; i < numSets; i++) {
            // LRU Cache implementation using LinkedHashMap
            this.sets[i] = new LinkedHashMap<>(associativity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, TLBEntry> eldest) {
                    return size() > TLB.this.associativity;
                }
            };
        }
    }

    public TLBEntry lookup(int vpn) {
        int tag = vpn >> indexBits;
        int index = vpn & indexMask;
        return sets[index].get(tag);
    }

    public void insert(int vpn, PageTableEntry pte) {
        int tag = vpn >> indexBits;
        int index = vpn & indexMask;
        TLBEntry newEntry = new TLBEntry(vpn, tag, pte);
        sets[index].put(tag, newEntry);
    }

    /**
     * Removes a specific VPN from the TLB.
     * Used when the OS evicts a page, making the TLB entry stale.
     */
    public void invalidate(int vpn) {
        int tag = vpn >> indexBits;
        int index = vpn & indexMask;
        sets[index].remove(tag);
    }
}