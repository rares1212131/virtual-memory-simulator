package org.example.vmsimulator.domain.algorithms;
import org.example.vmsimulator.domain.PageTableDirectory;
import org.example.vmsimulator.domain.PageTableEntry;
import org.example.vmsimulator.domain.PhysicalMemory;

/**
 * Implements the Clock (Second-Chance) page replacement algorithm.
 * It uses a circular pointer (clock hand) and a reference bit to find a victim.
 */
public class ClockAlgorithm implements IPageReplacementAlgorithm {

    private int clockHand = 0; // The index of the frame the clock hand is pointing to.

    /**
     * Finds a victim frame using the Clock algorithm.
     * It iterates through frames, giving a "second chance" to any page
     * whose reference bit is set to 1.
     *
     * @param physicalMemory A reference to the physical memory to inspect frames.
     * @param pageDirectory  A reference to the page directory to find the PTEs.
     * @return The physical page number (PPN) of the chosen victim frame.
     */
    @Override
    public int findVictim(PhysicalMemory physicalMemory, PageTableDirectory pageDirectory) {
        int numFrames = physicalMemory.getFrames().size();

        // This loop is guaranteed to terminate because in the worst case,
        // it will circle around once, clearing all reference bits, and then
        // find a victim on the second pass.
        while (true) {
            // Get the VPN of the page currently in the frame pointed to by the clock hand.
            int vpnInFrame = physicalMemory.getFrame(clockHand).getOccupiedByVpn();

            // Use the VPN to find the corresponding PageTableEntry.
            PageTableEntry pte = pageDirectory.findPageTableEntry(vpnInFrame);

            if (pte != null && pte.isReferenceBit()) {
                // Second Chance: If the reference bit is 1, clear it to 0.
                pte.setReferenceBit(false);
                // Advance the clock hand to the next frame.
                clockHand = (clockHand + 1) % numFrames;
            } else {
                // Victim Found: If the reference bit is 0 (or the PTE is null, which is an error state),
                // this is our victim.
                int victimPpn = clockHand;
                // Advance the hand so it's ready for the next page fault.
                clockHand = (clockHand + 1) % numFrames;
                return victimPpn;
            }
        }
    }
}