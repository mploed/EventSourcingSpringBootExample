package com.innoq.ploed.spring.eventsourcing.web.Event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public class UserDeactivatedEvent extends DomainEvent {
    private Long userId;

    private String adminName;

    public UserDeactivatedEvent() {
        super();
    }

    public UserDeactivatedEvent(long eventId, Long userId, String adminName) {
        super(eventId, userId);
        this.userId = userId;
        this.adminName = adminName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public String toString() {
        return "UserDeactivatedEvent{" +
                "userId=" + userId +
                ", adminName='" + adminName + '\'' +
                ", eventId='" + getEventId() + '\'' +
                '}';
    }
}
