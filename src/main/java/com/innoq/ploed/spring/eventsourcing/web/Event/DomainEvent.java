package com.innoq.ploed.spring.eventsourcing.web.Event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.Date;

@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.CLASS)
public abstract class DomainEvent implements Serializable {
    private long eventId;
    private Long aggregateId;
    private Date timestamp;

    public DomainEvent() {
    }

    public DomainEvent(long eventId, Long aggregateId) {
        this.eventId = eventId;
        this.timestamp = new Date();
        setAggregateId(aggregateId);
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    private void setAggregateId(Long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public long getEventId() {
        return eventId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
