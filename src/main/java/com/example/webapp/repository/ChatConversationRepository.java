package com.example.webapp.repository;

import com.example.webapp.entity.ChatConversation;
import com.example.webapp.entity.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    Optional<ChatConversation> findByConversationId(String conversationId);

    List<ChatConversation> findByStatusOrderByLastMessageAtDesc(ChatStatus status);

    List<ChatConversation> findByStatusAndAssignedAdminIdOrderByLastMessageAtDesc(ChatStatus status, Long adminId);
}
