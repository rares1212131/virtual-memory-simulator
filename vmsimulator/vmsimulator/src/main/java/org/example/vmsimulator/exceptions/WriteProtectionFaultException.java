package org.example.vmsimulator.exceptions;

public class WriteProtectionFaultException extends SegmentationFaultException {
    public WriteProtectionFaultException(String message) {
        super(message);
    }
}