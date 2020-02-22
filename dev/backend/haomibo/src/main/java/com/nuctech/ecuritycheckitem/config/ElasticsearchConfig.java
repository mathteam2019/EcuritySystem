package com.nuctech.ecuritycheckitem.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;

/**
 * ElasticsearchConfig
 *
 * @author PiaoCangGe
 * @version v1.0
 * @since 2019-11-27
 */
//@Configuration
@PropertySource(value = "classpath:application.properties")
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.clustername}")
    private String esClusterName;

    @Value("${elasticsearch.index.number_of_replicas}")
    private int esReplicas;

    @Value("${elasticsearch.index.refresh_interval}")
    private int esRefreshInterval;

    @Value("${elasticsearch.index.max_result_window}")
    private int esMaxResultWindow;

    @Value("${elasticsearch.index.number_of_shards}")
    private int esIndexNumberOfShards;

    @Value("${elasticsearch.client.transport.sniff}")
    private boolean esClientTransportSniff;

    @Value("${elasticsearch.client.transport.ping_timeout}")
    private String esClientTransportPingTimeout;

    private static final int TIME_OUT = 5 * 60 * 1000;

//    @Bean
//    public Client client() throws Exception {
//        Settings esSettings = Settings.builder()
//                .put("cluster.name", esClusterName)
////                .put("index.number_of_replicas", esReplicas)
////                .put("index.refresh_interval", esRefreshInterval)
////                .put("index.max_result_window", esMaxResultWindow)
////                .put("index.number_of_shards", esIndexNumberOfShards)
//                .put("client.transport.sniff", esClientTransportSniff)
//                .put("client.transport.ping_timeout", esClientTransportPingTimeout)
//                .put("node.client", true)
//                .build();
//        TransportClient client = new PreBuiltTransportClient(esSettings);
//        client.addTransportAddress(new TransportAddress(InetAddress.getByName(esHost), esPort));
//        return client;
//
//    }
//@Bean
//public RestClientBuilder restClientBuilder() {
//    HttpHost[] hosts = {makeHttpHost()};
//    return RestClient.builder(hosts);
//}
//
//    @Bean(name = "highLevelClient")
//    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
//        restClientBuilder.setRequestConfigCallback(
//                new RestClientBuilder.RequestConfigCallback() {
//                    @Override
//                    public RequestConfig.Builder customizeRequestConfig(
//                            RequestConfig.Builder requestConfigBuilder) {
//                        return requestConfigBuilder.setSocketTimeout(TIME_OUT);
//                    }
//                });
//        //TODO 此处可以进行其它操作
//        return new RestHighLevelClient(restClientBuilder);
//    }
//
//
//    private HttpHost makeHttpHost() {
//        return new HttpHost(esHost, esPort, "http");
//    }
////
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate(@Autowired Client highLevelClient) throws Exception {
//        return new ElasticsearchTemplate(highLevelClient);
//    }

}

