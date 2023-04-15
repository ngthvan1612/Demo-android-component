package com.hcmute.android.project.week9.toeic1234test01.component.part;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.hcmute.android.project.week9.toeic1234test01.R;
import com.hcmute.android.project.week9.toeic1234test01.component.utils.ToeicAudioPlayerComponent;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicItemContent;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestion;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestionGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToeicQuestionPart1ViewComponent extends ToeicQuestionBaseComponent {
    ///Component
    private TextView txtQuestionTitle;
    private ImageView imgQuestionMain;
    private RadioGroup radioGroupAnswer;
    private List<RadioButton> answerSelectionRadioButtons;

    // Resource & Model
    private ToeicQuestion toeicQuestion;
    private ToeicQuestionGroup toeicQuestionGroup;
    private File fileResourceDirectory;
    private ToeicAudioPlayerComponent toeicAudioPlayerComponent;

    public ToeicQuestionPart1ViewComponent(Context context) {
        super(context);
        this.initComponent(context);
    }

    public ToeicQuestionPart1ViewComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initComponent(context);
    }

    @Override
    public void renderToeicQuestionModel(@NonNull ToeicQuestionGroup toeicQuestionGroup) {
        assert toeicQuestionGroup.getQuestionGroupDirectory() != null;
        assert toeicQuestionGroup.getQuestions().size() == 1;
        assert toeicQuestionGroup.getQuestionContent().size() == 1;

        this.toeicQuestionGroup = toeicQuestionGroup;
        this.toeicQuestion = toeicQuestionGroup.getQuestions().get(0);
        this.fileResourceDirectory = toeicQuestionGroup.getQuestionGroupDirectory();

        assert 1 <= toeicQuestion.getQuestionId() && toeicQuestion.getQuestionId() <= 6;

        this.txtQuestionTitle.setText("Question #" + toeicQuestion.getQuestionId());
        this.loadMainImage();
        this.loadAudio();
    }

    @Override
    public void checkAndShowAnswer() {
        final ToeicItemContent transcript = this.toeicQuestionGroup.getTranscript().get(0);
        assert transcript.getType().equals("SCRIPT-PART-01");
        Gson gson = new Gson();
        List<String> answers = Arrays.asList(gson.fromJson(transcript.getContent(), String[].class));
        assert answers.size() == 4;

        int selectedAnswerPosition = -2;
        for (int i = 0; i < 4; ++i) {
            if (this.answerSelectionRadioButtons.get(i).isChecked()) {
                selectedAnswerPosition = i;
            }
        }

        this.radioGroupAnswer.removeAllViews();
        for (int i = 0; i < 4; ++i) {
            final char answerLabel = (char) ('A' + i);
            final RadioButton answerRadioButton = new RadioButton(new ContextThemeWrapper(this.getContext(), R.style.answer_radio_button));

            if (selectedAnswerPosition == i && !this.toeicQuestion.getCorrectAnswer().equals(answerLabel + "")) {
                answerRadioButton.setTextColor(Color.RED);
                answerRadioButton.setTypeface(null, Typeface.BOLD);
            }
            else if (this.toeicQuestion.getCorrectAnswer().equals(answerLabel + "")) {
                answerRadioButton.setTextColor(Color.GREEN);
                answerRadioButton.setTypeface(null, Typeface.BOLD_ITALIC);
                if (selectedAnswerPosition == i)
                    answerRadioButton.setChecked(true);
            }

            answerRadioButton.setId(generateViewId());
            answerRadioButton.setText(answerLabel + ". " + answers.get(i));
            answerRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));

            answerRadioButton.setEnabled(false);

            this.radioGroupAnswer.addView(answerRadioButton);
        }
    }

    @Override
    public void playAudio() {
        this.toeicAudioPlayerComponent.playAudio();
    }

    private void loadMainImage() {
        final ToeicItemContent toeicItemContent = this.toeicQuestionGroup.getQuestionContent().get(0);
        assert toeicItemContent.getType().equals("IMG");
        final String fileName = toeicItemContent.getContent();

        File imgFile = new File(this.fileResourceDirectory, fileName);
        if (!imgFile.exists()) {
            Toast.makeText(this.getContext(), "Không tìm thấy hình ảnh " + imgFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        this.imgQuestionMain.setImageBitmap(bitmap);
        this.renderAnswerSelection();
    }

    private void loadAudio() {
        final String audioFileName = this.toeicQuestionGroup.getAudio();
        assert audioFileName != null && audioFileName.length() > 0;
        File audioFile = new File(this.toeicQuestionGroup.getQuestionGroupDirectory(), audioFileName);
        this.toeicAudioPlayerComponent.setAudioFileSource(audioFile);
    }

    private void renderAnswerSelection() {
        this.radioGroupAnswer.removeAllViews();
        this.answerSelectionRadioButtons = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            char answerLabel = (char) ('A' + i);
            RadioButton answerRadioButton = new RadioButton(new ContextThemeWrapper(this.getContext(), R.style.answer_radio_button));
            answerRadioButton.setId(generateViewId());
            answerRadioButton.setText(answerLabel + "");
            answerRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
            ));
            this.radioGroupAnswer.addView(answerRadioButton);
            this.answerSelectionRadioButtons.add(answerRadioButton);
        }
    }

    private void initComponent(Context context) {
        inflate(this.getContext(), R.layout.component_toeic_question_part_1_item, this);

        this.txtQuestionTitle = findViewById(R.id.component_toeic_question_part_1_item_title);
        this.imgQuestionMain = findViewById(R.id.component_toeic_question_part_1_item_main_image);
        this.radioGroupAnswer = findViewById(R.id.component_toeic_question_part_1_item_group_answer);
        this.toeicAudioPlayerComponent = findViewById(R.id.component_toeic_question_part_1_audio_player);

        assert this.txtQuestionTitle != null;
        assert this.imgQuestionMain != null;
        assert this.radioGroupAnswer != null;
        assert this.toeicAudioPlayerComponent != null;
    }
}
