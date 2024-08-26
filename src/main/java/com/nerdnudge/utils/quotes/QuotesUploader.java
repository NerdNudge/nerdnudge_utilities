package com.nerdnudge.utils.quotes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nerdnudge.utils.commons.Utilities;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class QuotesUploader {

    private NerdPersistClient quotesPersistClient;

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("[ERROR] Please provide the config file.");
            System.exit(0);
        }

        System.out.println("--------------------NERD NUDGE QUOTES UPLOAD -> START--------------------");
        String configFile = args[0];
        QuotesUploader quotesUploader = new QuotesUploader();
        try {
            quotesUploader.updateConfigurations(configFile);
            quotesUploader.quotesPersistClient = Utilities.getQuotesPersistClient();
            quotesUploader.uploadQuotes();
            Thread.sleep(4000);
        }
        catch (Exception ex) {
            System.out.println("[ERROR] Issue Uploading Quotes");
            ex.printStackTrace();
        }
        System.out.println("--------------------NERD NUDGE QUOTES UPLOAD -> END--------------------");
        System.exit(0);
    }

    private void uploadQuotes() throws IOException {
        QuotesUploaderConfiguration quotesUploaderConfiguration = QuotesUploaderConfiguration.getInstance();
        JsonObject quotesDocument = quotesPersistClient.get(quotesUploaderConfiguration.getQuotesDocumentId());
        if(quotesDocument == null)
            quotesDocument = new JsonObject();

        Map<String, QuotesEntity> quotesEntityMap = getQuotesEntityMap(quotesDocument);
        addFileQuotes(quotesEntityMap);
        saveToPersist(quotesEntityMap);
    }

    private void saveToPersist(Map<String, QuotesEntity> quotesEntityMap) {
        JsonObject quotesDocument = new JsonObject();

        int idSequence = 1;
        Iterator<Map.Entry<String, QuotesEntity>> quotesIterator = quotesEntityMap.entrySet().iterator();
        while(quotesIterator.hasNext()) {
            Map.Entry<String, QuotesEntity> thisEntry = quotesIterator.next();
            QuotesEntity thisQuote = thisEntry.getValue();

            JsonObject currentQuoteObject = new JsonObject();
            currentQuoteObject.addProperty("quote", thisQuote.getQuote());
            currentQuoteObject.addProperty("author", thisQuote.getAuthor());
            currentQuoteObject.addProperty("author_credentials", thisQuote.getAuthorCredentials());

            quotesDocument.add("q" + idSequence, currentQuoteObject);
            idSequence ++;
        }

        System.out.println("Total unique quotes added: " + quotesDocument.entrySet().size());
        quotesPersistClient.set(QuotesUploaderConfiguration.getInstance().getQuotesDocumentId(), quotesDocument);
    }

    private void addFileQuotes(Map<String, QuotesEntity> quotesEntityMap) throws IOException {
        String fileContents = readFile(QuotesUploaderConfiguration.getInstance().getQuotesInputFile());

        JsonObject fileContentsObject = (JsonObject) new JsonParser().parse(fileContents);
        if(! fileContentsObject.has("quotes"))
            return;

        JsonArray quotesMainArray = fileContentsObject.get("quotes").getAsJsonArray();
        for(int i = 0; i < quotesMainArray.size(); i ++) {
            JsonArray actualQuotesArray = quotesMainArray.get(i).getAsJsonArray();
            for(int j = 0; j < actualQuotesArray.size(); j ++) {
                JsonObject currentQuoteObject = actualQuotesArray.get(j).getAsJsonObject();
                quotesEntityMap.put(currentQuoteObject.get("quote").getAsString(),
                        new QuotesEntity(currentQuoteObject.get("id").getAsString(),
                                currentQuoteObject.get("quote").getAsString(),
                                currentQuoteObject.get("author").getAsString(),
                                currentQuoteObject.get("author_credentials").getAsString()));
            }
        }
    }

    private String readFile(String fileName) throws IOException {
        BufferedReader br = null;
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            br.close();
        }
        return null;
    }

    private Map<String, QuotesEntity> getQuotesEntityMap(JsonObject quotesDocument) {
        if(! quotesDocument.has("quotes"))
            return new HashMap<>();

        Map<String, QuotesEntity> quotesEntityMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonElement>> quotesIterator = quotesDocument.entrySet().iterator();
        while(quotesIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = quotesIterator.next();
            String quoteId = thisEntry.getKey();
            JsonObject thisQuoteObject = thisEntry.getValue().getAsJsonObject();

            QuotesEntity currentQuoteEntity = new QuotesEntity(quoteId,
                    thisQuoteObject.get("quote").getAsString(),
                    thisQuoteObject.get("author").getAsString(),
                    thisQuoteObject.get("author_credentials").getAsString()
            );

            quotesEntityMap.put(currentQuoteEntity.getQuote(), currentQuoteEntity);
        }

        return quotesEntityMap;
    }

    private void updateConfigurations(final String configFileName) {
        QuotesUploaderConfiguration quotesUploaderConfiguration = QuotesUploaderConfiguration.getInstance();
        Properties properties = Utilities.getMigrationProperties(configFileName);

        quotesUploaderConfiguration.setQuotesInputFile(properties.getProperty("QUOTES_INPUT_FILE"));
        quotesUploaderConfiguration.setQuotesDocumentId(properties.getProperty("QUOTES_DOCUMENT_ID"));
        quotesUploaderConfiguration.setPersistAddress(properties.getProperty("PERSIST_ADDRESS"));
        quotesUploaderConfiguration.setPersistUsername(properties.getProperty("PERSIST_USERNAME"));
        quotesUploaderConfiguration.setPersistPassword(properties.getProperty("PERSIST_PASSWORD"));
        quotesUploaderConfiguration.setPersistBucketName(properties.getProperty("PERSIST_BUCKET"));
        quotesUploaderConfiguration.setPersistScopeName(properties.getProperty("PERSIST_SCOPE"));
        quotesUploaderConfiguration.setPersistCollectionName(properties.getProperty("PERSIST_COLLECTION"));
    }

    private void connectPersist() {
        QuotesUploaderConfiguration quotesUploaderConfiguration = QuotesUploaderConfiguration.getInstance();
        quotesPersistClient = new NerdPersistClient(
                quotesUploaderConfiguration.getPersistAddress(),
                quotesUploaderConfiguration.getPersistUsername(),
                quotesUploaderConfiguration.getPersistPassword(),
                quotesUploaderConfiguration.getPersistBucketName(),
                quotesUploaderConfiguration.getPersistScopeName(),
                quotesUploaderConfiguration.getPersistCollectionName()
        );
    }
}
