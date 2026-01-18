// FILE: D:\PROJECTSCS\vmsimulator\vmsimulator\src\main\java\org\example\vmsimulator\dto\PageTableEntryDTO.java

package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vmsimulator.domain.PageTableEntry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageTableEntryDTO {
    private boolean allocated;
    private boolean validBit;
    private boolean dirtyBit;
    private boolean referenceBit;
    private boolean writeBit;
    private int physicalPageNumber;
    private int swapSpaceNumber;

    // Custom constructor to map from the domain object
    public PageTableEntryDTO(PageTableEntry pte) {
        // FIX: Add a null check to prevent crashes
        if (pte != null) {
            this.allocated = pte.isAllocated();
            this.validBit = pte.isValidBit();
            this.dirtyBit = pte.isDirtyBit();
            this.referenceBit = pte.isReferenceBit();
            this.writeBit = pte.isWriteBit();
            this.physicalPageNumber = pte.getPhysicalPageNumber();
            this.swapSpaceNumber = pte.getSwapSpaceNumber();
        }
    }
}