package com.nerdnudge.utils.quizflex;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nerdnudge.utils.commons.Utilities;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;

import java.io.*;
import java.util.Properties;

public class QuizflexUploader {
    private NerdPersistClient quizflexPersistClient;

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("[ERROR] Please provide the config file.");
            System.exit(0);
        }

        System.out.println("--------------------NERD NUDGE QUIZFLEX UPLOAD -> START--------------------");
        String configFile = args[0];
        QuizflexUploader quizflexUploader = new QuizflexUploader();
        try {
            quizflexUploader.updateConfigurations(configFile);
            quizflexUploader.quizflexPersistClient = Utilities.getQuizflexPersistClient();
            quizflexUploader.uploadQuizflex();
            Thread.sleep(4000);
        }
        catch (Exception ex) {
            System.out.println("[ERROR] Issue Uploading Quotes");
            ex.printStackTrace();
        }
        System.out.println("--------------------NERD NUDGE QUIZFLEX UPLOAD -> END--------------------");
        System.exit(0);
    }

    private void uploadQuizflex() throws IOException {
        QuizflexUploaderConfiguration quizflexUploaderConfiguration = QuizflexUploaderConfiguration.getInstance();
        String inputFileContents = Utilities.readFile(quizflexUploaderConfiguration.getQuizflexInputFile());
        inputFileContents = inputFileContents.replaceAll("’", "'");
        JsonObject inputObject = (JsonObject) new JsonParser().parse(inputFileContents);
        JsonArray quizFlexMainArray = inputObject.get("quixflex").getAsJsonArray();
        int count = 0;
        for (int i = 0; i < quizFlexMainArray.size(); i++) {
            JsonArray quizFlexCurrentArray = quizFlexMainArray.get(i).getAsJsonArray();
            for (int j = 0; j < quizFlexCurrentArray.size(); j++) {
                JsonObject currentQuizflex = quizFlexCurrentArray.get(j).getAsJsonObject();
                String quizflexId = currentQuizflex.get("id").getAsString();
                currentQuizflex.addProperty("sub_topic", quizflexUploaderConfiguration.getSubtopic());
                quizflexPersistClient.set(quizflexId, currentQuizflex);
                count ++;
            }
        }
        System.out.println("Uploaded " + count + " quizflexes for subtopic: " + quizflexUploaderConfiguration.getSubtopic());
    }

    private void updateConfigurations(final String configFileName) {
        QuizflexUploaderConfiguration quizflexUploaderConfiguration = QuizflexUploaderConfiguration.getInstance();
        Properties properties = Utilities.getMigrationProperties(configFileName);

        quizflexUploaderConfiguration.setQuizflexInputFile(properties.getProperty("QUIZFLEX_INPUT_FILE"));
        quizflexUploaderConfiguration.setSubtopic(properties.getProperty("QUIZFLEX_SUBTOPIC"));
        quizflexUploaderConfiguration.setPersistAddress(properties.getProperty("PERSIST_ADDRESS"));
        quizflexUploaderConfiguration.setPersistUsername(properties.getProperty("PERSIST_USERNAME"));
        quizflexUploaderConfiguration.setPersistPassword(properties.getProperty("PERSIST_PASSWORD"));
        quizflexUploaderConfiguration.setPersistBucketName(properties.getProperty("PERSIST_BUCKET"));
        quizflexUploaderConfiguration.setPersistScopeName(properties.getProperty("PERSIST_SCOPE"));
        quizflexUploaderConfiguration.setPersistCollectionName(properties.getProperty("PERSIST_COLLECTION"));
    }
}
