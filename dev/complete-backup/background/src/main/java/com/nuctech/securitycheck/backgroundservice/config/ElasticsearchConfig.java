package com.nuctech.securitycheck.backgroundservice.config;

////import org.elasticsearch.client.Client;
////import org.elasticsearch.client.transport.TransportClient;
////import org.elasticsearch.common.settings.Settings;
////import org.elasticsearch.common.transport.InetSocketTransportAddress;
////import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//
//import java.net.InetAddress;

/**
 * ElasticsearchConfig
 *
 * @author PiaoCangGe
 * @version v1.0
 * @since 2019-11-27
 */
//@Configuration
//@PropertySource(value = "classpath:application.properties")
//@EnableElasticsearchRepositories(basePackages = "com.nuctech.securitycheck.backgroundservice.es.repository")
public class ElasticsearchConfig {

//    @Value("${elasticsearch.host}")
//    private String esHost;
//
//    @Value("${elasticsearch.port}")
//    private int esPort;
//
//    @Value("${elasticsearch.clustername}")
//    private String esClusterName;
//
//    @Value("${elasticsearch.index.number_of_replicas}")
//    private int esReplicas;
//
//    @Value("${elasticsearch.index.refresh_interval}")
//    private int esRefreshInterval;
//
//    @Value("${elasticsearch.index.max_result_window}")
//    private int esMaxResultWindow;
//
//    @Value("${elasticsearch.index.number_of_shards}")
//    private int esIndexNumberOfShards;
//
//    @Value("${elasticsearch.client.transport.sniff}")
//    private boolean esClientTransportSniff;
//
//    @Value("${elasticsearch.client.transport.ping_timeout}")
//    private String esClientTransportPingTimeout;
//
//    @Bean
//    public Client client() throws Exception {
//        Settings esSettings = Settings.builder()
//                .put("cluster.name", esClusterName)
//                .build();
//
//        //https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html
////        return PreBuiltTransportClient.builder()
////                .settings(esSettings)
////                .build()
////                .addTransportAddress(
////                        new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
////        Settings esSettings = Settings.builder()
////                .put("cluster.name", esClusterName)
////                .put("index.number_of_replicas", esReplicas)
////                .put("index.refresh_interval", esRefreshInterval)
////                .put("index.max_result_window", esMaxResultWindow)
////                .put("index.number_of_shards", esIndexNumberOfShards)
////                .put("client.transport.sniff", esClientTransportSniff)
////                .put("client.transport.ping_timeout", esClientTransportPingTimeout)
//////                .put("node.client", true)
////                .build();
//        TransportClient client = new PreBuiltTransportClient(esSettings);
//        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
//        return client;
//
//    }
////
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//        return new ElasticsearchTemplate(client());
//    }

}

