package com.optic.socialmedia.models;

public class SliderModel {
    String imagen;
    long timesamp;

    public SliderModel(String imagen, long timesamp) {
        this.imagen = imagen;
        this.timesamp = timesamp;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getTimesamp() {
        return timesamp;
    }

    public void setTimesamp(long timesamp) {
        this.timesamp = timesamp;
    }
}
