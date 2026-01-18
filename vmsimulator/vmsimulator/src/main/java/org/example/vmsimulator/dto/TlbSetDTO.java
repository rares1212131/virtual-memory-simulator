package org.example.vmsimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TlbSetDTO {
    private int index;
    private List<TlbEntryDTO> entries;
}