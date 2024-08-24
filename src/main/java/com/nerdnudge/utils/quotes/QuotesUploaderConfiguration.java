package com.nerdnudge.utils.quotes;

import lombok.Data;

@Data
public class QuotesUploaderConfiguration {
    private String persistAddress;
    private String persistUsername;
    private String persistPassword;
    private String persistBucketName;
    private String persistScopeName;
    private String persistCollectionName;
    private String quotesDocumentId;
    private String quotesInputFile;

    private static QuotesUploaderConfiguration quotesUploaderConfiguration = null;

    private QuotesUploaderConfiguration() {
    }

    public static QuotesUploaderConfiguration getInstance() {
        if(quotesUploaderConfiguration == null)
            quotesUploaderConfiguration = new QuotesUploaderConfiguration();

        return quotesUploaderConfiguration;
    }
}
