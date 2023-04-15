package com.hcmute.android.project.week9.toeic1234test01.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class ToeicQuestionGroup implements Serializable {
    private String type;
    @SerializedName("question_content")
    private List<ToeicItemContent> questionContent;
    private String audio;
    private List<ToeicItemContent> transcript;
    private List<ToeicQuestion> questions;
    private File questionGroupDirectory;

    public ToeicQuestionGroup() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ToeicItemContent> getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(List<ToeicItemContent> questionContent) {
        this.questionContent = questionContent;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public List<ToeicItemContent> getTranscript() {
        return transcript;
    }

    public void setTranscript(List<ToeicItemContent> transcript) {
        this.transcript = transcript;
    }

    public List<ToeicQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ToeicQuestion> questions) {
        this.questions = questions;
    }

    public File getQuestionGroupDirectory() {
        return questionGroupDirectory;
    }

    public void setQuestionGroupDirectory(File questionGroupDirectory) {
        this.questionGroupDirectory = questionGroupDirectory;
    }
}
