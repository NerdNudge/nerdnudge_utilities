package com.nerdnudge.utils.persistbackups;

import com.nerdnudge.utils.commons.Utilities;

import java.util.Properties;

public class PersistDataManager {
    public static void main(String[] args) throws InterruptedException {
        if(args.length == 0) {
            System.out.println("[ERROR] Please provide the config file.");
            System.exit(0);
        }

        System.out.println("--------------------NERD NUDGE PERSIST DATA UPLOAD -> START--------------------");
        String configFile = args[0];
        PersistDataManager persistDataManager = new PersistDataManager();
        persistDataManager.updateConfigurations(configFile);
        PersistDataManagerConfiguration persistDataManagerConfiguration = PersistDataManagerConfiguration.getInstance();
        persistDataManager.handleOperation(persistDataManagerConfiguration.getOperation());
        Thread.sleep(30000);
    }

    private void handleOperation(String operation) {
        switch (operation) {
            case "DATA_TRANSFER":
                PersistDataTransfer persistDataTransfer = new PersistDataTransfer();
                persistDataTransfer.transferData();
                break;
        }
    }

    private void updateConfigurations(final String configFileName) {
        PersistDataManagerConfiguration persistDataManagerConfiguration = PersistDataManagerConfiguration.getInstance();
        Properties properties = Utilities.getMigrationProperties(configFileName);

        persistDataManagerConfiguration.setPersistAddressSource(properties.getProperty("PERSIST_ADDRESS_SOURCE"));
        persistDataManagerConfiguration.setPersistUsernameSource(properties.getProperty("PERSIST_USERNAME_SOURCE"));
        persistDataManagerConfiguration.setPersistPasswordSource(properties.getProperty("PERSIST_PASSWORD_SOURCE"));
        persistDataManagerConfiguration.setPersistBucketNameSource(properties.getProperty("PERSIST_BUCKET_SOURCE"));
        persistDataManagerConfiguration.setPersistScopeNameSource(properties.getProperty("PERSIST_SCOPE_SOURCE"));
        persistDataManagerConfiguration.setPersistCollectionNameSource(properties.getProperty("PERSIST_COLLECTION_SOURCE"));

        persistDataManagerConfiguration.setPersistAddressDestination(properties.getProperty("PERSIST_ADDRESS_DESTINATION"));
        persistDataManagerConfiguration.setPersistUsernameDestination(properties.getProperty("PERSIST_USERNAME_DESTINATION"));
        persistDataManagerConfiguration.setPersistPasswordDestination(properties.getProperty("PERSIST_PASSWORD_DESTINATION"));
        persistDataManagerConfiguration.setPersistBucketNameDestination(properties.getProperty("PERSIST_BUCKET_DESTINATION"));
        persistDataManagerConfiguration.setPersistScopeNameDestination(properties.getProperty("PERSIST_SCOPE_DESTINATION"));
        persistDataManagerConfiguration.setPersistCollectionNameDestination(properties.getProperty("PERSIST_COLLECTION_DESTINATION"));

        persistDataManagerConfiguration.setOperation(properties.getProperty("PERSIST_OPERATION"));
    }
}
