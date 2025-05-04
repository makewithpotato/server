package org.dgu.programbook.global.error.exception;


import org.dgu.programbook.global.error.ErrorCode;

public class InternalServerException extends BusinessException {
    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}