package org.example.vmsimulator.domain.algorithms;
import org.example.vmsimulator.domain.*;

public class LRUAlgorithm implements IPageReplacementAlgorithm {
    @Override
    public int findVictim(PhysicalMemory pm, PageTableDirectory pd) {
        int victimPpn = -1;
        long oldestTime = Long.MAX_VALUE;
        for (int i = 0; i < pm.getFrames().size(); i++) {
            Frame frame = pm.getFrame(i);
            if (frame.isFree()) continue;
            PageTableEntry pte = pd.findPageTableEntry(frame.getOccupiedByVpn());
            if (pte != null && pte.getLastAccessTime() < oldestTime) {
                oldestTime = pte.getLastAccessTime();
                victimPpn = i;
            }
        }
        return (victimPpn == -1) ? 0 : victimPpn; // Failsafe
    }
}