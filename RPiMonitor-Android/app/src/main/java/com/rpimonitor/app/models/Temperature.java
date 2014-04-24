package com.rpimonitor.app.models;

import java.io.Serializable;

/**
 * Esta clase recoje la temperatura del dispositivo. Se crea como una clase
 * a parte por si en un futuro se agrega mas informacion sobre la temperatura
 */
public class Temperature implements Serializable {

    private float temperature;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
