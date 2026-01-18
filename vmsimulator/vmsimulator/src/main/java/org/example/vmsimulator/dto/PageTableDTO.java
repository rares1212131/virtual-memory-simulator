package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a single second-level Page Table for the frontend.
 * It corresponds to one entry in the top-level Page Directory.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageTableDTO {
    // The index in the first-level page directory (PDI)
    private int pageDirectoryIndex;

    // A simple flag to know if this second-level table has been created by the OS.
    private boolean allocated;

    // The list of all entries within this specific second-level table.
    private List<PageTableEntryDTO> entries;
}