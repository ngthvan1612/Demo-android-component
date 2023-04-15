package com.hcmute.android.project.week9.toeic1234test01.model;

import java.io.Serializable;
import java.util.List;

public class ToeicFullTest implements Serializable {
    private String slug;
    private String fullName;
    private List<ToeicPart> parts;

    public ToeicFullTest() {

    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<ToeicPart> getParts() {
        return parts;
    }

    public void setParts(List<ToeicPart> parts) {
        this.parts = parts;
    }
}
