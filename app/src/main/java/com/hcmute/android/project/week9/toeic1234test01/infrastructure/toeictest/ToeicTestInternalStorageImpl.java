package com.hcmute.android.project.week9.toeic1234test01.infrastructure.toeictest;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicFullTest;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicPart;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestion;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicQuestionGroup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ToeicTestInternalStorageImpl implements ToeicTestInternalStorage {
    private static final String LOG_TAG = "toeic-storage";
    private static final Gson gsonInstance = new Gson();
    private final Context context;
    private final String rootDirectory;
    private final ContextWrapper contextWrapper;

    public ToeicTestInternalStorageImpl(
            @NonNull Context context,
            String rootDirectory
    ) {
        this.context = context;
        this.rootDirectory = rootDirectory;
        this.contextWrapper = new ContextWrapper(this.context);
    }

    private void debugAllFileInRoot() {
        File file = this.contextWrapper.getDir(this.rootDirectory, Context.MODE_PRIVATE);

        List<File> subDirectories = this.listAllDirectoryInFolder(file);

        assert subDirectories != null;

        for (File dir : subDirectories) {
            Log.d(LOG_TAG, dir.getName());
        }
    }

    @NonNull
    private List<File> listAllDirectoryInFolder(@NonNull File parent) {
        return Arrays.asList(Objects.requireNonNull(parent.listFiles(File::isDirectory)));
    }

    private List<ToeicQuestionGroup> fillAllQuestionGroupForEachPart(
            ToeicPart toeicPart,
            File toeicPartRoot
    ) {
        List<ToeicQuestionGroup> toeicQuestionGroups = new ArrayList<>();

        for (File dir : this.listAllDirectoryInFolder(toeicPartRoot)) {
            final String questionGroupDirectoryName = dir.getName();
            final File questionGroupConfigurationFile = new File(dir, "config.json");
            if (!questionGroupConfigurationFile.exists()) {
                Log.d(LOG_TAG, "Missing configuration for " + dir.getAbsolutePath());
                continue;
            }

            String configJsonStr = "";

            try {
                FileInputStream questionGroupConfigurationFileInputStream = new FileInputStream(questionGroupConfigurationFile);
                byte[] buffer = new byte[(int) questionGroupConfigurationFile.length()];
                questionGroupConfigurationFileInputStream.read(buffer);
                configJsonStr = new String(buffer, StandardCharsets.UTF_8);
                questionGroupConfigurationFileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ToeicQuestionGroup toeicQuestionGroup = gsonInstance.fromJson(configJsonStr, ToeicQuestionGroup.class);
            toeicQuestionGroup.setQuestionGroupDirectory(dir);
            toeicQuestionGroups.add(toeicQuestionGroup);

            assert toeicQuestionGroup.getQuestions().size() > 0;

            for (ToeicQuestion question : toeicQuestionGroup.getQuestions()) {
                question.setResourceDirectory(dir);
                assert question.getCorrectAnswer() != null && question.getCorrectAnswer().length() == 1;
            }
        }

        toeicPart.setToeicQuestionGroups(toeicQuestionGroups);

        return toeicQuestionGroups;
    }

    private List<ToeicPart> fillAllPartsFromToeicTestRootFolder(
            ToeicFullTest toeicFullTest,
            File toeicRoot
    ) {
        final List<ToeicPart> parts = new ArrayList<>();

        for (File dir : this.listAllDirectoryInFolder(toeicRoot)) {
          final ToeicPart toeicPart = new ToeicPart();
          final String partDirectoryName = dir.getName();
          final Integer parsedPartId = Integer.parseInt(partDirectoryName.split("-")[1]);

          toeicPart.setPartId(parsedPartId);
          toeicPart.setSlug(partDirectoryName);

          parts.add(toeicPart);
        }

        toeicFullTest.setParts(parts);

        return parts;
    }

    private List<ToeicFullTest> getAllToeicTestsFromRootFolder(
            File root
    ) {
        final List<ToeicFullTest> tests = new ArrayList<>();

        for (File dir : this.listAllDirectoryInFolder(root)) {
            final ToeicFullTest test = new ToeicFullTest();
            final String partDirectoryName = dir.getName();
            List<String> tokens = Arrays.asList(partDirectoryName.split("-"));
            List<String> upperCasedTokens = tokens.stream().map(word -> {
                return Character.toUpperCase(word.charAt(0)) + word.substring(1);
            }).collect(Collectors.toList());
            final String titleDirectoryName = String.join(" ", upperCasedTokens);

            test.setFullName(titleDirectoryName);
            test.setSlug(partDirectoryName);

            tests.add(test);
        }

        return tests;
    }

    @Override
    public List<ToeicFullTest> listAllToeicTests() {
        File root = this.contextWrapper.getDir(this.rootDirectory, Context.MODE_PRIVATE);
        List<ToeicFullTest> toeicFullTests = this.getAllToeicTestsFromRootFolder(root);

        for (ToeicFullTest toeicFullTest : toeicFullTests) {
            final File toeicRootFolder = new File(root, toeicFullTest.getSlug());

            this.fillAllPartsFromToeicTestRootFolder(
                    toeicFullTest,
                    toeicRootFolder
            );

            for (ToeicPart toeicPart : toeicFullTest.getParts()) {
                final File partRootFolder = new File(toeicRootFolder, toeicPart.getSlug());
                this.fillAllQuestionGroupForEachPart(
                        toeicPart,
                        partRootFolder
                );
            }
        }
        return toeicFullTests;
    }
}
