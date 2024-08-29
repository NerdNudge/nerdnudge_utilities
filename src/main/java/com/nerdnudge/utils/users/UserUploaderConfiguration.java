package com.nerdnudge.utils.users;

import lombok.Data;

@Data
public class UserUploaderConfiguration {
    private String persistAddress;
    private String persistUsername;
    private String persistPassword;
    private String persistBucketName;
    private String persistScopeName;
    private String persistCollectionName;
    private int numUsers;

    private static UserUploaderConfiguration userUploaderConfiguration = null;

    private UserUploaderConfiguration() {
    }

    public static UserUploaderConfiguration getInstance() {
        if(userUploaderConfiguration == null)
            userUploaderConfiguration = new UserUploaderConfiguration();

        return userUploaderConfiguration;
    }
}
