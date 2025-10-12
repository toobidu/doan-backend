package org.example.quizizz.common.exception;

import lombok.Getter;
import org.example.quizizz.common.constants.MessageCode;

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final MessageCode messageCode;

    public ApiException(MessageCode messageCode) {
        super(messageCode.getMessage());
        this.status = 400;
        this.messageCode = messageCode;
    }

    public ApiException(int status, MessageCode messageCode) {
        super(messageCode.getMessage());
        this.status = status;
        this.messageCode = messageCode;
    }

    public ApiException(int status, MessageCode messageCode, String customMessage) {
        super(customMessage);
        this.status = status;
        this.messageCode = messageCode;
    }

}
