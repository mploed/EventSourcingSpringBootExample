package com.innoq.ploed.spring.eventsourcing.web.Event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.CLASS)
public class EventStoreContainer implements Serializable{
    public DomainEvent event;

    private EventStoreContainer() {
    }

    public EventStoreContainer(DomainEvent event) {
        this.event = event;
    }

    public DomainEvent getEvent() {
        return event;
    }

    public void setEvent(DomainEvent event) {
        this.event = event;
    }
}
