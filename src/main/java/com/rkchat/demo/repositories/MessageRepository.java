package com.rkchat.demo.repositories;

import com.rkchat.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
List<Message> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
