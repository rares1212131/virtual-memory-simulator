package org.example.vmsimulator.domain.algorithms;
import org.example.vmsimulator.domain.*;
import java.util.*;

public class RandomAlgorithm implements IPageReplacementAlgorithm {
    private final Random random = new Random();
    @Override
    public int findVictim(PhysicalMemory pm, PageTableDirectory pd) {
        return random.nextInt(pm.getFrames().size());
    }
}