package com.nuctech.securitycheck.backgroundservice.config;

import com.nuctech.securitycheck.backgroundservice.common.utils.BackgroundServiceUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQConfig
 *
 * @author PiaoCangGe
 * @version v1.0
 * @since 2019-11-27
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    TopicExchange devSysTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.dev.sys"));
    }

    @Bean
    TopicExchange sysDevTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.sys.dev"));
    }

    @Bean
    TopicExchange remSysTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.rem.sys"));
    }

    @Bean
    TopicExchange sysRemTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.sys.rem"));
    }

    @Bean
    TopicExchange manSysTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.man.sys"));
    }

    @Bean
    TopicExchange sysManTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.sys.man"));
    }

    @Bean
    TopicExchange devSysDataTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.dev.sys.data"));
    }

    @Bean
    TopicExchange devSysStatusTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.dev.sys.status"));
    }

    @Bean
    TopicExchange remSysDataTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.rem.sys.data"));
    }

    @Bean
    TopicExchange remSysStatusTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.rem.sys.status"));
    }

    @Bean
    TopicExchange manSysDataTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.man.sys.data"));
    }

    @Bean
    TopicExchange manSysStatusTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.inter.man.sys.status"));
    }

    @Bean
    TopicExchange zabbixTopicExchange() {
        return new TopicExchange(BackgroundServiceUtil.getConfig("topic.zabbix"));
    }

    @Bean
    Queue devSysQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.queue"), false);
    }

    @Bean
    Queue devSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.reply.queue"), false);
    }

    @Bean
    Queue sysDevQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.dev.queue"), false);
    }

    @Bean
    Queue sysDevReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.dev.reply.queue"), false);
    }

    @Bean
    Queue remSysQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.queue"), false);
    }

    @Bean
    Queue remSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.reply.queue"), false);
    }


    @Bean
    Queue sysRemQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.rem.queue"), false);
    }

    @Bean
    Queue sysRemReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.rem.reply.queue"), false);
    }


    @Bean
    Queue manSysQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.queue"), false);
    }

    @Bean
    Queue manSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.reply.queue"), false);
    }



    @Bean
    Queue sysManQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.man.queue"), false);
    }

    @Bean
    Queue sysManReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.man.reply.queue"), false);
    }

    @Bean
    Queue devSysDataQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.data.queue"), false);
    }

    @Bean
    Queue devSysDataReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.data.reply.queue"), false);
    }


    @Bean
    Queue devSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.status.queue"), false);
    }

    @Bean
    Queue devSysStatusReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.status.reply.queue"), false);
    }

    @Bean
    Queue manSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.status.queue"), false);
    }

    @Bean
    Queue manSysStatusReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.status.reply.queue"), false);
    }

    @Bean
    Queue remSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.status.queue"), false);
    }

    @Bean
    Queue remSysStatusReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.status.reply.queue"), false);
    }


    @Bean
    Queue devSysCurrentStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.currentstatus.queue"), false);
    }

    @Bean
    Queue devSysCurrentStatusReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.currentstatus.reply.queue"), false);
    }

    @Bean
    Queue devSysHardwareStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.hardwarestatus.queue"), false);
    }

    @Bean
    Queue devSysHardwareStatusReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.hardwarestatus.reply.queue"), false);
    }


    @Bean
    Queue zabbixQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("zabbix.queue"), false);
    }

    @Bean
    Binding devSysBinding(Queue devSysQueue, TopicExchange devSysTopicExchange) {
        return BindingBuilder.bind(devSysQueue).to(devSysTopicExchange).with("sys.#");
    }

    @Bean
    Binding devSysReplyBinding(Queue devSysReplyQueue, TopicExchange devSysTopicExchange) {
        return BindingBuilder.bind(devSysReplyQueue).to(devSysTopicExchange).with("reply.sys.#");
    }



    @Bean
    Binding sysDevBinding(Queue sysDevQueue, TopicExchange sysDevTopicExchange) {
        return BindingBuilder.bind(sysDevQueue).to(sysDevTopicExchange).with("dev.#");
    }

    @Bean
    Binding sysDevReplyBinding(Queue sysDevReplyQueue, TopicExchange sysDevTopicExchange) {
        return BindingBuilder.bind(sysDevReplyQueue).to(sysDevTopicExchange).with("reply.dev.#");
    }



    @Bean
    Binding remSysBinding(Queue remSysQueue, TopicExchange remSysTopicExchange) {
        return BindingBuilder.bind(remSysQueue).to(remSysTopicExchange).with("sys.#");
    }

    @Bean
    Binding remSysReplyBinding(Queue remSysReplyQueue, TopicExchange remSysTopicExchange) {
        return BindingBuilder.bind(remSysReplyQueue).to(remSysTopicExchange).with("reply.sys.#");
    }



    @Bean
    Binding sysRemBinding(Queue sysRemQueue, TopicExchange sysRemTopicExchange) {
        return BindingBuilder.bind(sysRemQueue).to(sysRemTopicExchange).with("rem.#");
    }

    @Bean
    Binding sysRemReplyBinding(Queue sysRemReplyQueue, TopicExchange sysRemTopicExchange) {
        return BindingBuilder.bind(sysRemReplyQueue).to(sysRemTopicExchange).with("reply.rem.#");
    }



    @Bean
    Binding manSysBinding(Queue manSysQueue, TopicExchange manSysTopicExchange) {
        return BindingBuilder.bind(manSysQueue).to(manSysTopicExchange).with("sys.#");
    }



    @Bean
    Binding manSysReplyBinding(Queue manSysReplyQueue, TopicExchange manSysTopicExchange) {
        return BindingBuilder.bind(manSysReplyQueue).to(manSysTopicExchange).with("reply.sys.#");
    }

    @Bean
    Binding sysManBinding(Queue sysManQueue, TopicExchange sysManTopicExchange) {
        return BindingBuilder.bind(sysManQueue).to(sysManTopicExchange).with("man.#");
    }

    @Bean
    Binding sysManReplyBinding(Queue sysManReplyQueue, TopicExchange sysManTopicExchange) {
        return BindingBuilder.bind(sysManReplyQueue).to(sysManTopicExchange).with("reply.man.#");
    }

    @Bean
    Binding devSysDataBinding(Queue devSysDataQueue, TopicExchange devSysDataTopicExchange) {
        return BindingBuilder.bind(devSysDataQueue).to(devSysDataTopicExchange).with("sys.#");
    }

    @Bean
    Binding devSysDataReplyBinding(Queue devSysDataReplyQueue, TopicExchange devSysDataTopicExchange) {
        return BindingBuilder.bind(devSysDataReplyQueue).to(devSysDataTopicExchange).with("reply.sys.#");
    }

    @Bean
    Binding devSysStatusBinding(Queue devSysStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysStatusQueue).to(devSysStatusTopicExchange).with("sys.heartbeat");
    }

    @Bean
    Binding devSysStatusReplyBinding(Queue devSysStatusReplyQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysStatusReplyQueue).to(devSysStatusTopicExchange).with("reply.sys.heartbeat");
    }

    @Bean
    Binding manSysStatusBinding(Queue manSysStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(manSysStatusQueue).to(devSysStatusTopicExchange).with("sys.man.heartbeat");
    }

    @Bean
    Binding manSysStatusReplyBinding(Queue manSysStatusReplyQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(manSysStatusReplyQueue).to(devSysStatusTopicExchange).with("reply.sys.man.heartbeat");
    }


    @Bean
    Binding remSysStatusBinding(Queue remSysStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(remSysStatusQueue).to(devSysStatusTopicExchange).with("sys.rem.heartbeat");
    }

    @Bean
    Binding remSysStatusReplyBinding(Queue remSysStatusReplyQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(remSysStatusReplyQueue).to(devSysStatusTopicExchange).with("reply.sys.rem.heartbeat");
    }


    @Bean
    Binding devSysCurrentStatusBinding(Queue devSysCurrentStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysCurrentStatusQueue).to(devSysStatusTopicExchange).with("sys.currentstatus");
    }

    @Bean
    Binding devSysCurrentStatusReplyBinding(Queue devSysCurrentStatusReplyQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysCurrentStatusReplyQueue).to(devSysStatusTopicExchange).with("reply.sys.currentstatus");
    }

    @Bean
    Binding devSysHardwareStatusBinding(Queue devSysHardwareStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysHardwareStatusQueue).to(devSysStatusTopicExchange).with("sys.hardwarestatus");
    }

    @Bean
    Binding devSysHardwareStatusReplyBinding(Queue devSysHardwareStatusReplyQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysHardwareStatusReplyQueue).to(devSysStatusTopicExchange).with("reply.sys.hardwarestatus");
    }



    @Bean
    Binding zabbixBinding(Queue zabbixQueue, TopicExchange zabbixTopicExchange) {
        return BindingBuilder.bind(zabbixQueue).to(zabbixTopicExchange).with("zabbix");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListener listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(BackgroundServiceUtil.getConfig("dev.sys.queue"), BackgroundServiceUtil.getConfig("dev.sys.reply.queue"),
                BackgroundServiceUtil.getConfig("sys.dev.queue"), BackgroundServiceUtil.getConfig("sys.dev.reply.queue"),
                BackgroundServiceUtil.getConfig("rem.sys.queue"), BackgroundServiceUtil.getConfig("rem.sys.reply.queue"),
                BackgroundServiceUtil.getConfig("sys.rem.queue"), BackgroundServiceUtil.getConfig("sys.rem.reply.queue"),
                BackgroundServiceUtil.getConfig("man.sys.queue"), BackgroundServiceUtil.getConfig("man.sys.reply.queue"),
                BackgroundServiceUtil.getConfig("sys.man.queue"), BackgroundServiceUtil.getConfig("sys.man.reply.queue"),
                BackgroundServiceUtil.getConfig("dev.sys.data.queue"), BackgroundServiceUtil.getConfig("dev.sys.data.reply.queue"),
                BackgroundServiceUtil.getConfig("dev.sys.status.queue"), BackgroundServiceUtil.getConfig("dev.sys.status.reply.queue"),
                BackgroundServiceUtil.getConfig("man.sys.status.queue"), BackgroundServiceUtil.getConfig("man.sys.status.reply.queue"),
                BackgroundServiceUtil.getConfig("rem.sys.status.queue"), BackgroundServiceUtil.getConfig("rem.sys.status.reply.queue"),
                BackgroundServiceUtil.getConfig("dev.sys.currentstatus.queue"), BackgroundServiceUtil.getConfig("dev.sys.currentstatus.reply.queue"),
                BackgroundServiceUtil.getConfig("dev.sys.hardwarestatus.queue"), BackgroundServiceUtil.getConfig("dev.sys.hardwarestatus.reply.queue"),

                BackgroundServiceUtil.getConfig("zabbix.queue"));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
