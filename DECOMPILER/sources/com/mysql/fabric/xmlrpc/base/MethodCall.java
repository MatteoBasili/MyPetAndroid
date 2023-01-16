package com.mysql.fabric.xmlrpc.base;

public class MethodCall {
    protected String methodName;
    protected Params params;

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String value) {
        this.methodName = value;
    }

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params value) {
        this.params = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<methodCall>");
        sb.append("\t<methodName>" + this.methodName + "</methodName>");
        Params params2 = this.params;
        if (params2 != null) {
            sb.append(params2.toString());
        }
        sb.append("</methodCall>");
        return sb.toString();
    }
}
