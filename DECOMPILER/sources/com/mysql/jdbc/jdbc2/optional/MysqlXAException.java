package com.mysql.jdbc.jdbc2.optional;

import javax.transaction.xa.XAException;

class MysqlXAException extends XAException {
    private static final long serialVersionUID = -9075817535836563004L;
    private String message;
    protected String xidAsString;

    public MysqlXAException(int errorCode, String message2, String xidAsString2) {
        super(errorCode);
        this.message = message2;
        this.xidAsString = xidAsString2;
    }

    public MysqlXAException(String message2, String xidAsString2) {
        this.message = message2;
        this.xidAsString = xidAsString2;
    }

    public String getMessage() {
        String superMessage = MysqlXAException.super.getMessage();
        StringBuilder returnedMessage = new StringBuilder();
        if (superMessage != null) {
            returnedMessage.append(superMessage);
            returnedMessage.append(":");
        }
        String str = this.message;
        if (str != null) {
            returnedMessage.append(str);
        }
        return returnedMessage.toString();
    }
}
