package com.nerdnudge.utils.quotes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuotesEntity {
    private String quotesId;
    private String quote;
    private String author;
    private String authorCredentials;
}
