package org.example.quizizz.common.exception;

import org.example.quizizz.common.constants.MessageCode;

public class ApiException extends RuntimeException {
    private final int status;
    private final MessageCode messageCode;

    public ApiException(MessageCode messageCode) {
        super(messageCode.getMessage());
        this.status = 400; // Default status
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

    public int getStatus() {
        return status;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }
}
