package com.rkchat.demo.repositories;

import com.rkchat.demo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findBySenderIdAndRecipientId(Long senderId, Long recipientId);


    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.recipient.id = :user1Id) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("user1Id") Long user1Id,@Param("user2Id") Long user2Id);
}
