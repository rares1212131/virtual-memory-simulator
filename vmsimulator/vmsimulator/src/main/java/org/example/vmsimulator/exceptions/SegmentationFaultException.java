package org.example.vmsimulator.exceptions;

public class SegmentationFaultException extends RuntimeException {
    public SegmentationFaultException(String message) {
        super(message);
    }
}