package com.hcmute.android.project.week9.toeic1234test01.component.part;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestion;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestionGroup;

public abstract class ToeicQuestionBaseComponent extends LinearLayout {
    public ToeicQuestionBaseComponent(Context context) {
        super(context);
    }

    public ToeicQuestionBaseComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void renderToeicQuestionModel(@NonNull ToeicQuestionGroup toeicQuestionGroup);
    public abstract void checkAndShowAnswer();
    public abstract void playAudio();
}
