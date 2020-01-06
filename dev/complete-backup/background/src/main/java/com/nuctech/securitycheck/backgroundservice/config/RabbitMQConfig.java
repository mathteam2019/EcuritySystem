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
    Queue sysDevReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.dev.reply.queue"), false);
    }

    @Bean
    Queue sysDevQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.dev.queue"), false);
    }

    @Bean
    Queue devSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.reply.queue"), false);
    }

    @Bean
    Queue remSysQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.queue"), false);
    }

    @Bean
    Queue sysRemReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.rem.reply.queue"), false);
    }

    @Bean
    Queue sysRemQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.rem.queue"), false);
    }

    @Bean
    Queue remSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.reply.queue"), false);
    }

    @Bean
    Queue manSysQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.queue"), false);
    }

    @Bean
    Queue sysManReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.man.reply.queue"), false);
    }

    @Bean
    Queue sysManQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("sys.man.queue"), false);
    }

    @Bean
    Queue manSysReplyQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.reply.queue"), false);
    }

    @Bean
    Queue devSysDataQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.data.queue"), false);
    }

    @Bean
    Queue devSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("dev.sys.status.queue"), false);
    }

    @Bean
    Queue manSysDataQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.data.queue"), false);
    }

    @Bean
    Queue manSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("man.sys.status.queue"), false);
    }

    @Bean
    Queue remSysDataQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.data.queue"), false);
    }

    @Bean
    Queue remSysStatusQueue() {
        return new Queue(BackgroundServiceUtil.getConfig("rem.sys.status.queue"), false);
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
    Binding sysDevReplyBinding(Queue sysDevReplyQueue, TopicExchange devSysTopicExchange) {
        return BindingBuilder.bind(sysDevReplyQueue).to(devSysTopicExchange).with("reply.sys.#");
    }

    @Bean
    Binding sysDevBinding(Queue sysDevQueue, TopicExchange sysDevTopicExchange) {
        return BindingBuilder.bind(sysDevQueue).to(sysDevTopicExchange).with("dev.#");
    }

    @Bean
    Binding devSysReplyBinding(Queue devSysReplyQueue, TopicExchange sysDevTopicExchange) {
        return BindingBuilder.bind(devSysReplyQueue).to(sysDevTopicExchange).with("reply.dev.#");
    }

    @Bean
    Binding remSysBinding(Queue remSysQueue, TopicExchange remSysTopicExchange) {
        return BindingBuilder.bind(remSysQueue).to(remSysTopicExchange).with("sys.#");
    }

    @Bean
    Binding sysRemReplyBinding(Queue sysRemReplyQueue, TopicExchange remSysTopicExchange) {
        return BindingBuilder.bind(sysRemReplyQueue).to(remSysTopicExchange).with("reply.sys.#");
    }

    @Bean
    Binding sysRemBinding(Queue sysRemQueue, TopicExchange sysRemTopicExchange) {
        return BindingBuilder.bind(sysRemQueue).to(sysRemTopicExchange).with("rem.#");
    }

    @Bean
    Binding remSysReplyBinding(Queue remSysReplyQueue, TopicExchange sysRemTopicExchange) {
        return BindingBuilder.bind(remSysReplyQueue).to(sysRemTopicExchange).with("reply.rem.#");
    }

    @Bean
    Binding manSysBinding(Queue manSysQueue, TopicExchange manSysTopicExchange) {
        return BindingBuilder.bind(manSysQueue).to(manSysTopicExchange).with("sys.#");
    }

    @Bean
    Binding sysManReplyBinding(Queue sysManReplyQueue, TopicExchange manSysTopicExchange) {
        return BindingBuilder.bind(sysManReplyQueue).to(manSysTopicExchange).with("reply.sys.#");
    }

    @Bean
    Binding sysManBinding(Queue sysManQueue, TopicExchange sysManTopicExchange) {
        return BindingBuilder.bind(sysManQueue).to(sysManTopicExchange).with("man.#");
    }

    @Bean
    Binding manSysReplyBinding(Queue manSysReplyQueue, TopicExchange sysManTopicExchange) {
        return BindingBuilder.bind(manSysReplyQueue).to(sysManTopicExchange).with("reply.man.#");
    }

    @Bean
    Binding devSysDataBinding(Queue devSysDataQueue, TopicExchange devSysDataTopicExchange) {
        return BindingBuilder.bind(devSysDataQueue).to(devSysDataTopicExchange).with("sys.#");
    }

    @Bean
    Binding devSysStatusBinding(Queue devSysStatusQueue, TopicExchange devSysStatusTopicExchange) {
        return BindingBuilder.bind(devSysStatusQueue).to(devSysStatusTopicExchange).with("sys.#");
    }

    @Bean
    Binding manSysDataBinding(Queue manSysDataQueue, TopicExchange manSysDataTopicExchange) {
        return BindingBuilder.bind(manSysDataQueue).to(manSysDataTopicExchange).with("sys.#");
    }

    @Bean
    Binding manSysStatusBinding(Queue manSysStatusQueue, TopicExchange manSysStatusTopicExchange) {
        return BindingBuilder.bind(manSysStatusQueue).to(manSysStatusTopicExchange).with("sys.#");
    }

    @Bean
    Binding remSysDataBinding(Queue remSysDataQueue, TopicExchange remSysDataTopicExchange) {
        return BindingBuilder.bind(remSysDataQueue).to(remSysDataTopicExchange).with("sys.#");
    }

    @Bean
    Binding remSysStatusBinding(Queue remSysStatusQueue, TopicExchange remSysStatusTopicExchange) {
        return BindingBuilder.bind(remSysStatusQueue).to(remSysStatusTopicExchange).with("sys.#");
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
