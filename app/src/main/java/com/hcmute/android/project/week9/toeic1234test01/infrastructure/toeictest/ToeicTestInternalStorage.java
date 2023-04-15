package com.hcmute.android.project.week9.toeic1234test01.infrastructure.toeictest;

import com.hcmute.android.project.week9.toeic1234test01.model.ToeicFullTest;

import java.util.List;

public interface ToeicTestInternalStorage {
    List<ToeicFullTest> listAllToeicTests();
}
