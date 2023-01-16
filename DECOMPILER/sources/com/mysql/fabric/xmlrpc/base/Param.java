package com.mysql.fabric.xmlrpc.base;

public class Param {
    protected Value value;

    public Param() {
    }

    public Param(Value value2) {
        this.value = value2;
    }

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value2) {
        this.value = value2;
    }

    public String toString() {
        return "<param>" + this.value.toString() + "</param>";
    }
}
