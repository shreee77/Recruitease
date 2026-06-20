package com.recruitease.service;

import com.recruitease.model.ChatMessage;
import com.recruitease.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired private ChatMessageRepository chatMessageRepository;

    public ChatMessage send(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getConversation(Long userId1, Long userId2) {
        return chatMessageRepository.findConversation(userId1, userId2);
    }

    public long countUnread(Long userId) {
        return chatMessageRepository.countUnreadMessages(userId);
    }

    public void markRead(Long receiverId) {
        List<ChatMessage> unread = chatMessageRepository.findByReceiverIdAndIsReadFalse(receiverId);
        unread.forEach(m -> m.setRead(true));
        chatMessageRepository.saveAll(unread);
    }
}
