package com.malcolm.portsmouthunibus.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Distance implements Serializable {
    @SerializedName("text")
    private String text;
    @SerializedName("value")
    private Integer value;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -3105138012835463825L;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
