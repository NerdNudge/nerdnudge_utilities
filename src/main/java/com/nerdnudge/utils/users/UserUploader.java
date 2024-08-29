package com.nerdnudge.utils.users;

import com.google.gson.JsonObject;
import com.nerdnudge.utils.commons.Utilities;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class UserUploader {
    private NerdPersistClient usersPersistClient;
    private List<String> topics = new ArrayList<>();
    private Random random = new Random();
    private double min = -10.0;
    private double max = 100.0;

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("[ERROR] Please provide the config file.");
            System.exit(0);
        }

        System.out.println("--------------------NERD NUDGE USER UPLOAD -> START--------------------");
        String configFile = args[0];
        UserUploader userUploader = new UserUploader();
        try {
            userUploader.updateConfigurations(configFile);
            userUploader.usersPersistClient = Utilities.getUserPersistClient();
            userUploader.uploadUsers();
            Thread.sleep(4000);
        }
        catch (Exception ex) {
            System.out.println("[ERROR] Issue Uploading Users");
            ex.printStackTrace();
        }
        System.out.println("--------------------NERD NUDGE USER UPLOAD -> END--------------------");
        System.exit(0);
    }

    private void uploadUsers() {
        UserUploaderConfiguration userUploaderConfiguration = UserUploaderConfiguration.getInstance();
        for(int i = 0; i < userUploaderConfiguration.getNumUsers(); i ++) {
            JsonObject newUserObject = new JsonObject();
            newUserObject.addProperty("registrationDate", 1722578619);
            newUserObject.addProperty("type", "userProfile");
            newUserObject.addProperty("accountType", "freemium");
            newUserObject.addProperty("accountStartDate", "21524");

            JsonObject scoresObject = new JsonObject();
            double totalScore = 0.0;
            for(int k = 0; k < topics.size(); k ++) {
                double randomScore = min + (max - min) * random.nextDouble();
                BigDecimal roundedValue = new BigDecimal(randomScore).setScale(2, RoundingMode.HALF_UP);
                scoresObject.addProperty(topics.get(k), roundedValue.doubleValue());
                totalScore += randomScore;
            }

            BigDecimal roundedValue = new BigDecimal(totalScore).setScale(2, RoundingMode.HALF_UP);
            scoresObject.addProperty("global", roundedValue.doubleValue());
            newUserObject.add("scores", scoresObject);
            usersPersistClient.set("abc" + (i + 10) + "@gmail.com", newUserObject);
        }
        System.out.println("Uploaded " + userUploaderConfiguration.getNumUsers() + " users.");
    }

    private void updateConfigurations(final String configFileName) {
        UserUploaderConfiguration userUploaderConfiguration = UserUploaderConfiguration.getInstance();
        Properties properties = Utilities.getMigrationProperties(configFileName);

        userUploaderConfiguration.setNumUsers(Integer.parseInt(properties.getProperty("NUMBER_OF_USERS")));
        userUploaderConfiguration.setPersistAddress(properties.getProperty("PERSIST_ADDRESS"));
        userUploaderConfiguration.setPersistUsername(properties.getProperty("PERSIST_USERNAME"));
        userUploaderConfiguration.setPersistPassword(properties.getProperty("PERSIST_PASSWORD"));
        userUploaderConfiguration.setPersistBucketName(properties.getProperty("PERSIST_BUCKET"));
        userUploaderConfiguration.setPersistScopeName(properties.getProperty("PERSIST_SCOPE"));
        userUploaderConfiguration.setPersistCollectionName(properties.getProperty("PERSIST_COLLECTION"));

        topics.add("java");
        topics.add("sde");
        topics.add("dsa");
        topics.add("kafka");
        topics.add("k8s");
        topics.add("aws");
        topics.add("py");
    }
}
