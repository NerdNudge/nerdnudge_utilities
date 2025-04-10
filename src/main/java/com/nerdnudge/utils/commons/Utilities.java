package com.nerdnudge.utils.commons;

import com.nerdnudge.utils.persistbackups.PersistDataManagerConfiguration;
import com.nerdnudge.utils.quizflex.QuizflexUploaderConfiguration;
import com.nerdnudge.utils.quotes.QuotesUploaderConfiguration;
import com.nerdnudge.utils.users.UserUploaderConfiguration;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;

import java.io.*;
import java.util.Properties;

public class Utilities {
    public static Properties getMigrationProperties(final String configFileName) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFileName)) {
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static NerdPersistClient getQuotesPersistClient() {
        QuotesUploaderConfiguration quotesUploaderConfiguration = QuotesUploaderConfiguration.getInstance();
        NerdPersistClient nerdPersistClient = new NerdPersistClient(
                quotesUploaderConfiguration.getPersistAddress(),
                quotesUploaderConfiguration.getPersistUsername(),
                quotesUploaderConfiguration.getPersistPassword(),
                quotesUploaderConfiguration.getPersistBucketName(),
                quotesUploaderConfiguration.getPersistScopeName(),
                quotesUploaderConfiguration.getPersistCollectionName()
        );

        return nerdPersistClient;
    }

    public static NerdPersistClient getQuizflexPersistClient() {
        QuizflexUploaderConfiguration quizflexUploaderConfiguration = QuizflexUploaderConfiguration.getInstance();
        NerdPersistClient nerdPersistClient = new NerdPersistClient(
                quizflexUploaderConfiguration.getPersistAddress(),
                quizflexUploaderConfiguration.getPersistUsername(),
                quizflexUploaderConfiguration.getPersistPassword(),
                quizflexUploaderConfiguration.getPersistBucketName(),
                quizflexUploaderConfiguration.getPersistScopeName(),
                quizflexUploaderConfiguration.getPersistCollectionName()
        );

        return nerdPersistClient;
    }

    public static NerdPersistClient getUserPersistClient() {
        UserUploaderConfiguration userUploaderConfiguration = UserUploaderConfiguration.getInstance();
        NerdPersistClient nerdPersistClient = new NerdPersistClient(
                userUploaderConfiguration.getPersistAddress(),
                userUploaderConfiguration.getPersistUsername(),
                userUploaderConfiguration.getPersistPassword(),
                userUploaderConfiguration.getPersistBucketName(),
                userUploaderConfiguration.getPersistScopeName(),
                userUploaderConfiguration.getPersistCollectionName()
        );

        return nerdPersistClient;
    }

    public static NerdPersistClient getPersistDataManagerSourceClient() {
        PersistDataManagerConfiguration persistDataManagerConfiguration = PersistDataManagerConfiguration.getInstance();
        NerdPersistClient nerdPersistClient = new NerdPersistClient(
                persistDataManagerConfiguration.getPersistAddressSource(),
                persistDataManagerConfiguration.getPersistUsernameSource(),
                persistDataManagerConfiguration.getPersistPasswordSource(),
                persistDataManagerConfiguration.getPersistBucketNameSource(),
                persistDataManagerConfiguration.getPersistScopeNameSource(),
                persistDataManagerConfiguration.getPersistCollectionNameSource()
        );

        return nerdPersistClient;
    }

    public static NerdPersistClient getPersistDataManagerDestinationClient() {
        PersistDataManagerConfiguration persistDataManagerConfiguration = PersistDataManagerConfiguration.getInstance();
        NerdPersistClient nerdPersistClient = new NerdPersistClient(
                persistDataManagerConfiguration.getPersistAddressDestination(),
                persistDataManagerConfiguration.getPersistUsernameDestination(),
                persistDataManagerConfiguration.getPersistPasswordDestination(),
                persistDataManagerConfiguration.getPersistBucketNameDestination(),
                persistDataManagerConfiguration.getPersistScopeNameDestination(),
                persistDataManagerConfiguration.getPersistCollectionNameDestination()
        );

        return nerdPersistClient;
    }

    public static String readFile(String fileName) throws IOException {
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
}
