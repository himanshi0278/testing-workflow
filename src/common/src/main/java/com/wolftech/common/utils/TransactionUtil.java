package com.wolftech.common.utils;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.wolftech.common.dto.TransactionRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransactionUtil {

    private static final Logger logger = LogManager.getLogger(LambdaClientUtil.class);

    private static final String PARTITION_KEY = "transactionId";
    private static final String SORT_KEY = "accountNumber";
    private final HelperUtils helperUtils = new HelperUtils();

    public enum Status {
        VALIDATING, PROCESSING, SUCCESS, FAILED
    }

    private final DynamoDbClient dynamoDbClient;

    public TransactionUtil() {
        this.dynamoDbClient = DynamoDbClient.create();
    }

    // Insert or Update Transaction Record
    public void putTransaction(TransactionRequest transactionRequest, String traceId, Status status) {
        logger.info("Adding transaction " + transactionRequest+ " with status :"+status.name());
        Map<String, AttributeValue> item = new HashMap<>();

        item.put(PARTITION_KEY, AttributeValue.builder().s(transactionRequest.getTransactionId()).build());
        item.put(SORT_KEY, AttributeValue.builder().s(transactionRequest.getAccountNumber()).build());
        item.put("traceId", AttributeValue.builder().s(traceId).build());
        item.put("currency", AttributeValue.builder().s(transactionRequest.getCurrency()).build());
        item.put("amount", AttributeValue.builder().n(transactionRequest.getAmount().toString()).build());
        item.put("thirdPartyId", AttributeValue.builder().s(transactionRequest.getThirdPartyId()).build());
        item.put("reason", AttributeValue.builder().s(transactionRequest.getReason()).build());
        item.put("status", AttributeValue.builder().s(status.name()).build());
        item.put("created_datetime", AttributeValue.builder().s(Instant.now().toString()).build());
        item.put("updated_datetime", AttributeValue.builder().s(Instant.now().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(getTransactionTableName())
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        logger.info("Transaction added successfully");
        
    }

    // Update Status of a Transaction
    public void updateTransactionStatus(String transactionId, String accountNumber, Status status) {
        logger.info("Updating transaction " + transactionId+ ", accountNumber: "+ accountNumber +" with status :"+status.name());
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PARTITION_KEY, AttributeValue.builder().s(transactionId).build());
        key.put(SORT_KEY, AttributeValue.builder().s(accountNumber).build());

        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("status", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(status.name()).build())
                .action(AttributeAction.PUT)
                .build());
        updates.put("updated_datetime", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(Instant.now().toString()).build())
                .action(AttributeAction.PUT)
                .build());
        
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(getTransactionTableName())
                .key(key)
                .attributeUpdates(updates)
                .build();

        dynamoDbClient.updateItem(updateRequest);
        logger.info("Transaction updated successfully");
        
    }

    // Retrieve a Transaction
    public Map<String, AttributeValue> getTransaction(String transactionId, String accountNumber) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PARTITION_KEY, AttributeValue.builder().s(transactionId).build());
        key.put(SORT_KEY, AttributeValue.builder().s(accountNumber).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(getTransactionTableName())
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.item();
    }

    private String getTransactionTableName() {
        return helperUtils.getEnvVariable("TRANSACTION_TABLE_NAME", "makuta-pg");
    }

}
