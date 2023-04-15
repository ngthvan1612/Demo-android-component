package com.hcmute.android.project.week9.toeic1234test01.model;

import java.io.Serializable;
import java.util.List;

public class ToeicPart implements Serializable {
    private Integer partId;
    private String slug;
    private List<ToeicQuestionGroup> toeicQuestionGroups;

    public ToeicPart() {

    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<ToeicQuestionGroup> getToeicQuestionGroups() {
        return toeicQuestionGroups;
    }

    public void setToeicQuestionGroups(List<ToeicQuestionGroup> toeicQuestionGroups) {
        this.toeicQuestionGroups = toeicQuestionGroups;
    }
}
