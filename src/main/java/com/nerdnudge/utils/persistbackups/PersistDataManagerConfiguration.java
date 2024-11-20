package com.nerdnudge.utils.persistbackups;

import lombok.Data;

@Data
public class PersistDataManagerConfiguration {
    private String persistAddressSource;
    private String persistUsernameSource;
    private String persistPasswordSource;
    private String persistBucketNameSource;
    private String persistScopeNameSource;
    private String persistCollectionNameSource;

    private String persistAddressDestination;
    private String persistUsernameDestination;
    private String persistPasswordDestination;
    private String persistBucketNameDestination;
    private String persistScopeNameDestination;
    private String persistCollectionNameDestination;

    private String operation;
    private boolean isDeduplicationNeeded;

    private static PersistDataManagerConfiguration quizflexUploaderConfiguration = null;

    private PersistDataManagerConfiguration() {
    }

    public static PersistDataManagerConfiguration getInstance() {
        if(quizflexUploaderConfiguration == null)
            quizflexUploaderConfiguration = new PersistDataManagerConfiguration();

        return quizflexUploaderConfiguration;
    }
}
