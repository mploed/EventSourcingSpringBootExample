package com.innoq.ploed.spring.eventsourcing.web.Event;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.innoq.ploed.spring.eventsourcing.web.domain.User;


@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public class UserCreatedEvent extends DomainEvent {
    private User user;

    public UserCreatedEvent() {
        super();
    }

    public UserCreatedEvent(long eventId, User user) {
        super(eventId, user.getId());
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "aggregateId=" + getAggregateId() +
                "eventId=" + getEventId() +
                "timestamp=" + getTimestamp() +
                "user=" + user +
                '}';
    }
}
