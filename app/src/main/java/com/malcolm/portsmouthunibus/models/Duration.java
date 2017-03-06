package com.malcolm.portsmouthunibus.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Duration implements Serializable {
    @SerializedName("value")
    private Double value;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -2907738741189184610L;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
