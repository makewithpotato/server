package org.dgu.programbook.global.error.exception;


import org.dgu.programbook.global.error.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}