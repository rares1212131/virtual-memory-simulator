package org.example.vmsimulator.domain;

import lombok.Getter;
import java.util.Arrays; // Import Arrays

public class Frame {

    @Getter
    private final byte[] data;
    private int occupiedByVpn = -1;

    public Frame(int pageSize) {
        this.data = new byte[pageSize];
        Arrays.fill(this.data, (byte) 0);
    }

    public byte readByte(int offset) {
        return this.data[offset];
    }

    public void writeByte(int offset, byte value) {
        this.data[offset] = value;
    }

    public int getOccupiedByVpn() {
        return occupiedByVpn;
    }

    public void setOccupiedByVpn(int vpn) {
        this.occupiedByVpn = vpn;
    }

    public boolean isFree() {
        return this.occupiedByVpn == -1;
    }
}