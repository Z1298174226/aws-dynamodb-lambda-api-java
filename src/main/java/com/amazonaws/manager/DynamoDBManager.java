package com.amazonaws.manager;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


public class DynamoDBManager {
    private static volatile DynamoDBManager instance;

    private static DynamoDBMapper mapper;

    private DynamoDBManager() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().
                withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-east-1"))
                .build();
        mapper = new DynamoDBMapper(client);
    }

    public static DynamoDBManager instance() {

        if (instance == null) {
            synchronized(DynamoDBManager.class) {
                if (instance == null)
                    instance = new DynamoDBManager();
            }
        }

        return instance;
    }

    public static DynamoDBMapper mapper() {

        DynamoDBManager manager = instance();
        return manager.mapper;
    }

}
