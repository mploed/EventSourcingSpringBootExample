package com.innoq.ploed.spring.eventsourcing.web.Event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.innoq.ploed.spring.eventsourcing.web.domain.User;

@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public class UserVerifiedEvent extends DomainEvent {
    private Long userId;

    private String adminName;

    public UserVerifiedEvent() {
        super();
    }

    public UserVerifiedEvent(long eventId, Long userId, String adminName) {
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
}
