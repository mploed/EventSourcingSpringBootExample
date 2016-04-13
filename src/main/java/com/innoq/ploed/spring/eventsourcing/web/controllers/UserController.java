package com.innoq.ploed.spring.eventsourcing.web.controllers;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.HazelcastClientInstanceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.innoq.ploed.spring.eventsourcing.web.Event.EventStoreContainer;
import com.innoq.ploed.spring.eventsourcing.web.Event.UserCreatedEvent;
import com.innoq.ploed.spring.eventsourcing.web.Event.UserDeactivatedEvent;
import com.innoq.ploed.spring.eventsourcing.web.Event.UserVerifiedEvent;
import com.innoq.ploed.spring.eventsourcing.web.domain.User;
import com.innoq.ploed.spring.eventsourcing.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.List;


@RequestMapping(path = "/user")
@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @Autowired
    RedisTemplate<String, EventStoreContainer> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HazelcastInstance hazelcastInstance;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        ClientConfig cc = new ClientConfig();

        Collection userList = hazelcastInstance.getMap("userList").values();
        Collection deactivatedUsers = hazelcastInstance.getMap("deactivatedUsers").values();
        Collection verifiedUsers = hazelcastInstance.getMap("verifiedUsers").values();

        model.addAttribute("verifiedUsers", verifiedUsers);
        model.addAttribute("deactivatedUsers", deactivatedUsers);
        model.addAttribute("users", userList);
        model.addAttribute("user", new User());
        return "index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public String create(@ModelAttribute User user, Model model) {
        Long eventId = redisTemplate.opsForValue().increment("event_ids", 1);
        Long aggregateId = redisTemplate.opsForValue().increment("user_ids", 1);
        user.setId(aggregateId);
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(eventId, user);

        EventStoreContainer eventStoreContainer = new EventStoreContainer(userCreatedEvent);
        Long nextIndex = redisTemplate.opsForList().rightPush("events", eventStoreContainer);

        stringRedisTemplate.convertAndSend("new_events", Long.toString(nextIndex - 1));
        return "redirect:/user";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @Transactional
    public String verify(@ModelAttribute User user, Model model) {
        Long eventId = redisTemplate.opsForValue().increment("event_ids", 1);
        UserVerifiedEvent userVerifiedEvent = new UserVerifiedEvent(eventId, user.getId(), "mploed");
        Long nextIndex = redisTemplate.opsForList().rightPush("events", new EventStoreContainer(userVerifiedEvent));

        stringRedisTemplate.convertAndSend("new_events", Long.toString(nextIndex - 1));
        return "redirect:/user";
    }

    @RequestMapping(value = "/deactivate", method = RequestMethod.POST)
    @Transactional
    public String deactivate(@ModelAttribute User user, Model model) {
        Long eventId = redisTemplate.opsForValue().increment("event_ids", 1);
        UserDeactivatedEvent userDeactivatedEvent = new UserDeactivatedEvent(eventId, user.getId(), "mploed");
        Long nextIndex = redisTemplate.opsForList().rightPush("events", new EventStoreContainer(userDeactivatedEvent));

        stringRedisTemplate.convertAndSend("new_events", Long.toString(nextIndex - 1));
        return "redirect:/user";
    }

    @RequestMapping(value = "/replay", method = RequestMethod.POST)
    public String replay() {
        hazelcastInstance.getMap("userList").clear();
        Long size = redisTemplate.opsForList().size("events");
        for (int i = 0; i < size; i++) {
            stringRedisTemplate.convertAndSend("new_events", Integer.toString(i));
        }
        return "redirect:/user";
    }
}