package com.rpimonitor.app.models;

import java.io.Serializable;

/**
 * Created by angelmmiguel on 21/04/14.
 */
public class Cpu implements Serializable {

    private float user;
    private float system;
    private float free;
    private float other;

    public Cpu(float user, float system, float free){

        this.user = user;
        this.system = system;
        this.free = free;

        this.other = 100 - free - user - system;

    }

    public float getFree() {
        return free;
    }

    public void setFree(float free) {
        this.free = free;
    }

    public float getSystem() {
        return system;
    }

    public void setSystem(float system) {
        this.system = system;
    }

    public float getUser() {
        return user;
    }

    public void setUser(float user) {
        this.user = user;
    }

    public float getOther() {
        return other;
    }

    public void setOther(float other) {
        this.other = other;
    }
}
