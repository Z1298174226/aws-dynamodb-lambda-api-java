package com.amazonaws.handles;


import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PutChangeToES implements RequestHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(PutChangeToES.class);

    private final static String SERVICENAME = "es";
    private final static String REGION = "us-east-1";
    private final static String AESENDPOINT = "http://localhost:4571";
    private final static String INDEX = "match";
    private final static String TYPE = "_doc";
    private final static String ID = "1";
    private final static Random RANDOM = new Random();

    @Override
    public Object handleRequest(Object o, Context context) {

        RestHighLevelClient esClient = esClient(SERVICENAME, REGION);


        // Create the document as a hash mapcd
        Map<String, Object> document = new HashMap<>();
        document.put("homeTeam", "German");
        document.put("eventDate", new Long(Long.valueOf("201906" + RANDOM.nextInt(30))));

        // Form the indexing request, send it, and print the response
        IndexRequest request = new IndexRequest(INDEX, TYPE, String.valueOf(RANDOM.nextInt(100))).source(document);
        IndexResponse response = null;

        try {
            response = esClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(response.toString());
        return null;
    }

    public static RestHighLevelClient esClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(AESENDPOINT)));
    }
}
