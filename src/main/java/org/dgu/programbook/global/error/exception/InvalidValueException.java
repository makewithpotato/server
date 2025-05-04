package org.dgu.programbook.global.error.exception;


import org.dgu.programbook.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException() {
        super(ErrorCode.BAD_REQUEST);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}