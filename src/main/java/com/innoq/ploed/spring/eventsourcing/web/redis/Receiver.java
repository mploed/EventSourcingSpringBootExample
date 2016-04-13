package com.innoq.ploed.spring.eventsourcing.web.redis;

import com.hazelcast.core.HazelcastInstance;
import com.innoq.ploed.spring.eventsourcing.web.Event.*;
import com.innoq.ploed.spring.eventsourcing.web.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);


    RedisTemplate<String, EventStoreContainer> redisTemplate;

    HazelcastInstance hazelcastInstance;

    @Autowired
    public Receiver(RedisTemplate<String, EventStoreContainer> redisTemplate, HazelcastInstance hazelcastInstance) {
        this.redisTemplate = redisTemplate;
        this.hazelcastInstance = hazelcastInstance;
    }

    public void receiveMessage(String eventIndex) {

        Long size = redisTemplate.opsForList().size("events");
        LOGGER.info("notified of event " + eventIndex);
        LOGGER.info("we have " + size + " events");

        EventStoreContainer eventStoreContainer = redisTemplate.opsForList().index("events", Long.parseLong(eventIndex));
        DomainEvent event = eventStoreContainer.getEvent();
        LOGGER.error(event.toString());

        if (event instanceof UserCreatedEvent) {

            UserCreatedEvent userCreatedEvent = (UserCreatedEvent) event;
            User userFromHazelcast = (User) hazelcastInstance.getMap("userList").get(userCreatedEvent.getUser().getId());

            if (userFromHazelcast == null) {
                hazelcastInstance.getMap("userList").put(userCreatedEvent.getUser().getId(), userCreatedEvent.getUser());
            }
        }

        if (event instanceof UserVerifiedEvent) {

            UserVerifiedEvent userVerifiedEvent = (UserVerifiedEvent) event;
            if (userVerifiedEvent.getUserId() != null) {
                User userFromHazelcast = (User) hazelcastInstance.getMap("userList").get(userVerifiedEvent.getUserId());
                userFromHazelcast.setStatus("verified");
                hazelcastInstance.getMap("deactivatedUsers").remove(userVerifiedEvent.getUserId());
                hazelcastInstance.getMap("userList").put(userVerifiedEvent.getUserId(), userFromHazelcast);
                hazelcastInstance.getMap("verifiedUsers").put(userVerifiedEvent.getUserId(), userFromHazelcast);
            }

        }

        if (event instanceof UserDeactivatedEvent) {

            UserDeactivatedEvent userDeactivatedEvent = (UserDeactivatedEvent) event;
            if (userDeactivatedEvent.getUserId() != null) {
                User userFromHazelcast = (User) hazelcastInstance.getMap("userList").get(userDeactivatedEvent.getUserId());
                userFromHazelcast.setStatus("deactivated");
                hazelcastInstance.getMap("verifiedUsers").remove(userDeactivatedEvent.getUserId());
                hazelcastInstance.getMap("userList").put(userDeactivatedEvent.getUserId(), userFromHazelcast);
                hazelcastInstance.getMap("deactivatedUsers").put(userDeactivatedEvent.getUserId(), userFromHazelcast);
            }
        }

    }

}