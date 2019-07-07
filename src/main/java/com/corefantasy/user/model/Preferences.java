package com.corefantasy.user.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Preferences implements Serializable {
    private String theme;

    public Preferences() {
        this.theme = "default";
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
