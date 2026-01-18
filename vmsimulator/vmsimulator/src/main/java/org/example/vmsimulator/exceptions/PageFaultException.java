package org.example.vmsimulator.exceptions;

public class PageFaultException extends RuntimeException {
    private final int faultingVpn;

    public PageFaultException(String message, int faultingVpn) {
        super(message);
        this.faultingVpn = faultingVpn;
    }

    public int getFaultingVpn() {
        return faultingVpn;
    }
}