package org.example.vmsimulator.domain.algorithms;

import org.example.vmsimulator.domain.PageTableDirectory;
import org.example.vmsimulator.domain.PhysicalMemory;

public interface IPageReplacementAlgorithm {
    int findVictim(PhysicalMemory physicalMemory, PageTableDirectory pageDirectory);
}