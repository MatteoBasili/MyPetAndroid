package com.mysql.fabric.xmlrpc.base;

public class Array {
    protected Data data;

    public Data getData() {
        return this.data;
    }

    public void setData(Data value) {
        this.data = value;
    }

    public void addValue(Value v) {
        if (this.data == null) {
            this.data = new Data();
        }
        this.data.addValue(v);
    }

    public String toString() {
        return "<array>" + this.data.toString() + "</array>";
    }
}
