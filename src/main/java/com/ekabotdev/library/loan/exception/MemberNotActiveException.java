package com.ekabotdev.library.loan.exception;

public class MemberNotActiveException extends RuntimeException {
    public MemberNotActiveException(String message) {
        super(message);
    }
}
