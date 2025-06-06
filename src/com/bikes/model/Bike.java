package com.bikes.model;

public class Bike<T> {

    private T model;
    private int cc;

    public Bike(T model, int cc) {
        this.model = model;
        this.cc = cc;
    }

    public T getModel() {
        return model;
    }

    public int getCc() {
        return cc;
    }
}
