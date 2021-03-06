package com.amazonaws.stream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreams;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import java.util.*;

public class StreamDemo {
    public static void main(String args[]) throws InterruptedException {

        AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-east-1"))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        AmazonDynamoDBStreams streamsClient = AmazonDynamoDBStreamsClientBuilder
                .standard()
                .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:4570", "us-east-1"))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        // Create a table, with a stream enabled
        String tableName = "TestTableForStreams";

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>(
                Arrays.asList(new AttributeDefinition()
                        .withAttributeName("Id")
                        .withAttributeType("N")));

        ArrayList<KeySchemaElement> keySchema = new ArrayList<>(
                Arrays.asList(new KeySchemaElement()
                        .withAttributeName("Id")
                        .withKeyType(KeyType.HASH))); // Partition key

        StreamSpecification streamSpecification = new StreamSpecification()
                .withStreamEnabled(true)
                .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                .withKeySchema(keySchema).withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(10L)
                        .withWriteCapacityUnits(10L))
                .withStreamSpecification(streamSpecification);

        System.out.println("Issuing CreateTable request for " + tableName);
        dynamoDBClient.createTable(createTableRequest);
        System.out.println("Waiting for " + tableName + " to be created...");

        try {
            TableUtils.waitUntilActive(dynamoDBClient, tableName);
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }

        // Print the stream settings for the table
        DescribeTableResult describeTableResult = dynamoDBClient.describeTable(tableName);
        String streamArn = describeTableResult.getTable().getLatestStreamArn();
        System.out.println("Current stream ARN for " + tableName + ": " +
                describeTableResult.getTable().getLatestStreamArn());
        StreamSpecification streamSpec = describeTableResult.getTable().getStreamSpecification();
        System.out.println("Stream enabled: " + streamSpec.getStreamEnabled());
        System.out.println("Update view type: " + streamSpec.getStreamViewType());
        System.out.println();

        // Generate write activity in the table

        System.out.println("Performing write activities on " + tableName);
        int maxItemCount = 10;
        for (Integer i = 1; i <= maxItemCount; i++) {
            System.out.println("Processing item " + i + " of " + maxItemCount);

            // Write a new item
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("Id", new AttributeValue().withN(i.toString()));
            item.put("Message", new AttributeValue().withS("New item!"));
            dynamoDBClient.putItem(tableName, item);


            // Update the item
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("Id", new AttributeValue().withN(i.toString()));
            Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();
            attributeUpdates.put("Message", new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue()
                            .withS("This item has changed")));
            dynamoDBClient.updateItem(tableName, key, attributeUpdates);

            // Delete the item
     //       dynamoDBClient.deleteItem(tableName, key);
        }

        // Get all the shard IDs from the stream.  Note that DescribeStream returns
        // the shard IDs one page at a time.
        String lastEvaluatedShardId = null;

        do {
            System.out.println(streamsClient);
            DescribeStreamResult describeStreamResult = streamsClient.describeStream(
                    new DescribeStreamRequest()
                            .withStreamArn(streamArn)
                            .withExclusiveStartShardId(lastEvaluatedShardId));
            List<Shard> shards = describeStreamResult.getStreamDescription().getShards();

            // Process each shard on this page

            for (Shard shard : shards) {
                String shardId = shard.getShardId();
                System.out.println("Shard: " + shard);

                // Get an iterator for the current shard

                GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest()
                        .withStreamArn(streamArn)
                        .withShardId(shardId)
                        .withShardIteratorType(ShardIteratorType.TRIM_HORIZON);
                GetShardIteratorResult getShardIteratorResult =
                        streamsClient.getShardIterator(getShardIteratorRequest);
                String currentShardIter = getShardIteratorResult.getShardIterator();

                // Shard iterator is not null until the Shard is sealed (marked as READ_ONLY).
                // To prevent running the loop until the Shard is sealed, which will be on average
                // 4 hours, we process only the items that were written into DynamoDB and then exit.
                int processedRecordCount = 0;
                while (currentShardIter != null && processedRecordCount < maxItemCount) {
                    System.out.println("    Shard iterator: " + currentShardIter.substring(380));

                    // Use the shard iterator to read the stream records

                    GetRecordsResult getRecordsResult = streamsClient.getRecords(new GetRecordsRequest()
                            .withShardIterator(currentShardIter));
                    List<Record> records = getRecordsResult.getRecords();
                    for (Record record : records) {
                        System.out.println("        " + record.getDynamodb());
                    }
                    processedRecordCount += records.size();
                    currentShardIter = getRecordsResult.getNextShardIterator();
                }
            }

            // If LastEvaluatedShardId is set, then there is
            // at least one more page of shard IDs to retrieve
            lastEvaluatedShardId = describeStreamResult.getStreamDescription().getLastEvaluatedShardId();

        } while (lastEvaluatedShardId != null);

        // Delete the table
        System.out.println("Deleting the table...");
        dynamoDBClient.deleteTable(tableName);

        System.out.println("Demo complete");

    }
}
