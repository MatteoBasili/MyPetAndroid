package com.mysql.fabric.xmlrpc.base;

public class Member {
    protected String name;
    protected Value value;

    public Member() {
    }

    public Member(String name2, Value value2) {
        setName(name2);
        setValue(value2);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value2) {
        this.name = value2;
    }

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value2) {
        this.value = value2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<member>");
        sb.append("<name>" + this.name + "</name>");
        sb.append(this.value.toString());
        sb.append("</member>");
        return sb.toString();
    }
}
