package com.hcmute.android.project.week9.toeic1234test01.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hcmute.android.project.week9.toeic1234test01.R;
import com.hcmute.android.project.week9.toeic1234test01.component.part.ToeicQuestionBaseComponent;
import com.hcmute.android.project.week9.toeic1234test01.component.part.ToeicQuestionPart1ViewComponent;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicPart;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestionGroup;

public class ToeicLearningQuestionActivity extends BaseActivity {
    private ToeicPart toeicPart;
    private ViewPager testViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toeic_learning_question);
        this.toeicPart = this.getToeicPartFromIntent();

        AppCompatButton btnShowAnswer = findViewById(R.id.activity_toeic_learning_question_show_answer);
        this.testViewPager = findViewById(R.id.test_view_pager);
        this.testViewPager.setAdapter(new TestViewPagerAdapter());

        this.testViewPager.setOffscreenPageLimit(toeicPart.getToeicQuestionGroups().size());

        btnShowAnswer.setOnClickListener(view -> {
            final int currentPosition = this.testViewPager.getCurrentItem();
            ToeicQuestionBaseComponent currentQuestionView = this.testViewPager.findViewWithTag("question-" + currentPosition);
            assert currentQuestionView != null;
            currentQuestionView.checkAndShowAnswer();
        });

        this.testViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                playAudioPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void playAudioPage(final int position) {
        final String tag = "question-" + position;
        ToeicQuestionBaseComponent questionView = this.testViewPager.findViewWithTag(tag);
        if (questionView != null) {
            questionView.playAudio();
        }
    }

    private ToeicPart getToeicPartFromIntent() {
        ToeicPart toeicPart = (ToeicPart) this.getIntent().getExtras().get("part");
        assert toeicPart != null;
        return toeicPart;
    }

    @NonNull
    private ToeicQuestionBaseComponent getToeicQuestionRender() {
        if (this.toeicPart.getPartId() == 1) {
            return new ToeicQuestionPart1ViewComponent(this);
        }
        throw new RuntimeException("Unknown part id " + this.toeicPart.getPartId());
    }

    public class TestViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return toeicPart.getToeicQuestionGroups().size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ToeicQuestionGroup toeicQuestionGroup = toeicPart.getToeicQuestionGroups().get(position);
            ToeicQuestionBaseComponent questionView = getToeicQuestionRender();
            questionView.setTag("question-" + position);

            questionView.renderToeicQuestionModel(toeicQuestionGroup);
            container.addView(questionView);

            if (position == 0) {
                playAudioPage(0);
            }
            return questionView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ToeicQuestionBaseComponent)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}