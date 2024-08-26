package com.nerdnudge.utils.quizflex;

import lombok.Data;

@Data
public class QuizflexUploaderConfiguration {
    private String persistAddress;
    private String persistUsername;
    private String persistPassword;
    private String persistBucketName;
    private String persistScopeName;
    private String persistCollectionName;
    private String quizflexInputFile;
    private String subtopic;

    private static QuizflexUploaderConfiguration quizflexUploaderConfiguration = null;

    private QuizflexUploaderConfiguration() {
    }

    public static QuizflexUploaderConfiguration getInstance() {
        if(quizflexUploaderConfiguration == null)
            quizflexUploaderConfiguration = new QuizflexUploaderConfiguration();

        return quizflexUploaderConfiguration;
    }
}
