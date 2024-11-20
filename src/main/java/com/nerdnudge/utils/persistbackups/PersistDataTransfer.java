package com.nerdnudge.utils.persistbackups;

import com.couchbase.client.java.query.QueryResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nerdnudge.utils.commons.Utilities;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PersistDataTransfer {

    private NerdPersistClient sourcePersistClient;
    private NerdPersistClient destinationPersistClient;
    private PersistDataManagerConfiguration persistDataManagerConfiguration;
    private JsonParser jsonParser = new JsonParser();
    private int pageSize = 5000;
    private Set<String> duplicateTracker = new HashSet<>();

    public void transferData() {
        persistDataManagerConfiguration = PersistDataManagerConfiguration.getInstance();
        sourcePersistClient = Utilities.getPersistDataManagerSourceClient();
        destinationPersistClient = Utilities.getPersistDataManagerDestinationClient();

        int totalPages = getTotalPages();
        System.out.println(new Date() + "Total Pages: " + totalPages);
        int documentCount = 0;
        int jsonDocuments = 0;
        int counters = 0;
        int duplicateCount = 0;

        for (int k = 1; k <= totalPages; k++) {
            int offset = (k - 1) * pageSize;
            String documentIdsQuery = getDocumentIdsQuery(offset);
            QueryResult result = sourcePersistClient.getDocumentsByQuery(documentIdsQuery);
            for (com.couchbase.client.java.json.JsonObject row : result.rowsAsObject()) {
                JsonObject thisResult = jsonParser.parse(row.toString()).getAsJsonObject();
                documentCount ++;
                String documentId = thisResult.get("id").getAsString();
                try {
                    JsonObject sourceDocument = sourcePersistClient.get(documentId);
                    if(sourceDocument != null) {
                        jsonDocuments ++;
                        if(persistDataManagerConfiguration.isDeduplicationNeeded() && isDuplicate(sourceDocument)) {
                            duplicateCount ++;
                            continue;
                        }
                        destinationPersistClient.set(documentId, sourceDocument);
                    }
                    else {
                        long sourceCounterValue = sourcePersistClient.getCounter(documentId);
                        destinationPersistClient.delete(documentId);
                        destinationPersistClient.incr(documentId, (int) sourceCounterValue);
                        counters ++;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("TRANSFERRED TOTAL " + documentCount + " DOCUMENTS.");
        System.out.println("DUPLICATE DOCUMENTS IGNORED: " + duplicateCount);

        System.out.println("Transferred " + jsonDocuments + " Json Documents.");
        System.out.println("Transferred " + counters + " Counters.");
    }


    private boolean isDuplicate(JsonObject currentQuizflex) {
        if(! currentQuizflex.has("question")) {
            System.out.println("Something wrong with the doc: " + currentQuizflex);
            return true;
        }
        String question = currentQuizflex.get("question").getAsString();
        String title = currentQuizflex.get("title").getAsString();

        if(duplicateTracker.contains(title + ":" + question))
            return true;

        duplicateTracker.add(title + ":" + question);
        return false;
    }


    private String getCountsQuery() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(1) AS count FROM `");
        queryBuilder.append(persistDataManagerConfiguration.getPersistBucketNameSource());
        queryBuilder.append("`.`");
        queryBuilder.append(persistDataManagerConfiguration.getPersistScopeNameSource());
        queryBuilder.append("`.`");
        queryBuilder.append(persistDataManagerConfiguration.getPersistCollectionNameSource());
        queryBuilder.append("`");

        return queryBuilder.toString();
    }

    private String getDocumentIdsQuery(int offset) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT META().id as id");
        queryBuilder.append(" FROM `");
        queryBuilder.append(persistDataManagerConfiguration.getPersistBucketNameSource());
        queryBuilder.append("`.`");
        queryBuilder.append(persistDataManagerConfiguration.getPersistScopeNameSource());
        queryBuilder.append("`.`");
        queryBuilder.append(persistDataManagerConfiguration.getPersistCollectionNameSource());
        queryBuilder.append("`");
        queryBuilder.append(" LIMIT ");
        queryBuilder.append(pageSize);
        queryBuilder.append(" OFFSET ");
        queryBuilder.append(offset);

        return queryBuilder.toString();
    }

    private int getTotalPages() {
        String queryString = getCountsQuery();
        QueryResult result = sourcePersistClient.getDocumentsByQuery(queryString);
        for (com.couchbase.client.java.json.JsonObject row : result.rowsAsObject()) {
            JsonObject thisResult = jsonParser.parse(row.toString()).getAsJsonObject();
            if(thisResult.has("count")) {
                int count = thisResult.get("count").getAsInt();
                System.out.println("Total count of documents: " + count);
                return (int) Math.ceil((double) count / pageSize);
            }
        }
        return 1;
    }
}
