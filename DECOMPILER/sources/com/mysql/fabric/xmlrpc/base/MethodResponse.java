package com.mysql.fabric.xmlrpc.base;

public class MethodResponse {
    protected Fault fault;
    protected Params params;

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params value) {
        this.params = value;
    }

    public Fault getFault() {
        return this.fault;
    }

    public void setFault(Fault value) {
        this.fault = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<methodResponse>");
        Params params2 = this.params;
        if (params2 != null) {
            sb.append(params2.toString());
        }
        Fault fault2 = this.fault;
        if (fault2 != null) {
            sb.append(fault2.toString());
        }
        sb.append("</methodResponse>");
        return sb.toString();
    }
}
