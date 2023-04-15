package com.hcmute.android.project.week9.toeic1234test01.model;

import java.io.Serializable;

public class ToeicAnswerChoice implements Serializable {
    private String label;
    private String content;

    public ToeicAnswerChoice() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
