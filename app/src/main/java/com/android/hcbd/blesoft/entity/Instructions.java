package com.android.hcbd.blesoft.entity;

import java.io.Serializable;

public class Instructions implements Serializable {

    private static final long serialVersionUID = 5828942700873158792L;

    private String name;
    private String content;

    public Instructions() {
    }

    public Instructions(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
