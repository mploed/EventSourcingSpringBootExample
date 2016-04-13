package com.innoq.ploed.spring.eventsourcing.web.repository;

import com.innoq.ploed.spring.eventsourcing.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByStatus(String status);
}
