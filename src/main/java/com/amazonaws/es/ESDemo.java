package com.amazonaws.es;

import com.amazonaws.auth.AWS4Signer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ESDemo {
    private final static String SERVICENAME = "es";
    private final static String REGION = "us-east-1";
    private final static String AESENDPOINT = "http://localhost:4571";
    private final static String INDEX = "match";
    private final static String TYPE = "_doc";
    private final static String ID = "1";

    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = esClient(SERVICENAME, REGION);

        // Create the document as a hash map
        Map<String, Object> document = new HashMap<>();
        document.put("homeTeam", "German");
        document.put("eventDate", new Long(20190622));

        // Form the indexing request, send it, and print the response
        IndexRequest request = new IndexRequest(INDEX, TYPE, ID).source(document);
        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    public static RestHighLevelClient esClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(AESENDPOINT)));
    }
}
