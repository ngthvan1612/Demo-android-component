package com.hcmute.android.project.week9.toeic1234test01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.hcmute.android.project.week9.toeic1234test01.activities.BaseActivity;
import com.hcmute.android.project.week9.toeic1234test01.activities.ListPartActivity;
import com.hcmute.android.project.week9.toeic1234test01.component.list.ListViewAllToeicTestsComponent;
import com.hcmute.android.project.week9.toeic1234test01.infrastructure.toeictest.ToeicTestInternalStorage;
import com.hcmute.android.project.week9.toeic1234test01.infrastructure.toeictest.ToeicTestInternalStorageImpl;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicFullTest;

import java.util.List;

public class MainActivity extends BaseActivity {
    private ToeicTestInternalStorage toeicTestInternalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toeicTestInternalStorage = new ToeicTestInternalStorageImpl(this, "ahihi-xin-chao-ne");
        List<ToeicFullTest> toeicTests = this.toeicTestInternalStorage.listAllToeicTests();

        ListViewAllToeicTestsComponent toeicTestListView = findViewById(R.id.toeicTestListView);
        toeicTestListView.renderListToeicTests(toeicTests);

        toeicTestListView.setOnListViewItemClicked(test -> {
            Intent intent = new Intent(this, ListPartActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("test", test);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}