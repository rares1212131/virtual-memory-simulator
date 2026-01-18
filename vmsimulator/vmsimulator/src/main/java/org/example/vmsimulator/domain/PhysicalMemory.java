package org.example.vmsimulator.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


//Represents the entire Physical Memory (RAM) of the system.
//It is a collection of Frames.

public class PhysicalMemory {

    //A getter for the entire list, for sending state to the frontend.
    @Getter
    private final List<Frame> frames;
    private final int numPhysicalFrames;

    //numPhysicalFrames The total number of frames, calculated from user input.
    //pageSize The size of each frame, needed to construct the individual Frame objects.

    public PhysicalMemory(int numPhysicalFrames, int pageSize) {
        this.numPhysicalFrames = numPhysicalFrames;
        this.frames = new ArrayList<>(numPhysicalFrames);

        // Initialize physical memory by creating all the Frame objects.
        for (int i = 0; i < numPhysicalFrames; i++) {
            this.frames.add(new Frame(pageSize));
        }
    }

    /**
     * Gets the Frame object at a specific Physical Page Number (PPN).
     * @param physicalPageNumber The index of the frame to retrieve.
     * @return The Frame object.
     */
    public Frame getFrame(int physicalPageNumber) {
        if (physicalPageNumber >= 0 && physicalPageNumber < numPhysicalFrames) {
            return this.frames.get(physicalPageNumber);
        }
        throw new IndexOutOfBoundsException("Invalid Physical Page Number: " + physicalPageNumber);
    }

    /**
     * A crucial helper method for the OS. It searches for the first available frame.
     * @return The index (PPN) of the first free frame, or -1 if memory is full.
     */
    public int findFreeFrame() {
        for (int i = 0; i < numPhysicalFrames; i++) {
            if (frames.get(i).isFree()) {
                return i; //Return the PPN of the free frame
            }
        }
        return -1; //Sentinel value indicating memory is full
    }

}