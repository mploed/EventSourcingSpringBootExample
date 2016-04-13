package com.innoq.ploed.spring.eventsourcing.web;

import com.innoq.ploed.spring.eventsourcing.web.Event.EventStoreContainer;
import com.innoq.ploed.spring.eventsourcing.web.redis.Receiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean(name = "userCreatedEventRedisTemplate")
    RedisTemplate<String, EventStoreContainer> basicRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate t = new RedisTemplate();
        t.setConnectionFactory(redisConnectionFactory);
        t.setValueSerializer(new Jackson2JsonRedisSerializer<EventStoreContainer>(EventStoreContainer.class));
        return t;
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("new_events"));

        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
