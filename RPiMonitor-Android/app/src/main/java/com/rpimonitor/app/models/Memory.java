package com.rpimonitor.app.models;

import java.io.Serializable;

/**
 * Created by angelmmiguel on 23/04/14.
 */
public class Memory implements Serializable {

    private float used;
    private float percent;
    private float free;
    // Valor Buffered para memoria fisica y cached para swap
    private float cached;

    public Memory(float used, float free, float percent, float cached){

        this.used = used;
        this.percent = percent;
        this.free = free;
        this.cached = cached;

    }

    public float getUsed() {
        return used;
    }

    public void setUsed(float user) {
        this.used = user;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public float getCached() {
        return cached;
    }

    public void setCached(float cached) {
        this.cached = cached;
    }

    public float getFree() {
        return free;
    }

    public void setFree(float free) {
        this.free = free;
    }
}
