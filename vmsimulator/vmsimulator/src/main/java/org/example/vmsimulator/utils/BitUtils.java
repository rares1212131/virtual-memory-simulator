package org.example.vmsimulator.utils;

public class BitUtils {

    public static int getVpn(int virtualAddress, int offsetBits) {
        return virtualAddress >> offsetBits;
    }

    public static int getOffset(int virtualAddress, int offsetBits) {
        int offsetMask = (1 << offsetBits) - 1;
        return virtualAddress & offsetMask;
    }

    public static int getPdi(int vpn, int ptiBits) {
        return vpn >> ptiBits;
    }

    public static int getPti(int vpn, int ptiBits) {
        int ptiMask = (1 << ptiBits) - 1;
        return vpn & ptiMask;
    }
}