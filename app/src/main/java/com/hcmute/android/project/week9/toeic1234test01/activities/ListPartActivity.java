package com.hcmute.android.project.week9.toeic1234test01.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hcmute.android.project.week9.toeic1234test01.R;
import com.hcmute.android.project.week9.toeic1234test01.component.list.ListViewAllToeicPartsComponent;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicFullTest;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicPart;

public class ListPartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_part);

        ListViewAllToeicPartsComponent toeicPartsListView = findViewById(R.id.toeicPartsListView);
        toeicPartsListView.renderListToeicParts(this.getToeicFullTestFromIntent().getParts());
        toeicPartsListView.setOnToeicPartItemClicked(toeicPart -> {
            if (toeicPart.getPartId() != 1) {
                Toast.makeText(this, "chưa code part " + toeicPart.getPartId(), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ToeicLearningQuestionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("part", toeicPart);
            intent.putExtras(bundle);
            ToeicPart temp = (ToeicPart) bundle.getSerializable("part");
            startActivity(intent);
        });
    }

    private ToeicFullTest getToeicFullTestFromIntent() {
        return (ToeicFullTest) getIntent().getExtras().get("test");
    }
}