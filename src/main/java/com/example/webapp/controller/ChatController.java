package com.example.webapp.controller;

import com.example.webapp.entity.*;
import com.example.webapp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Tạo hoặc lấy cuộc trò chuyện
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startChat(@RequestBody(required = false) Map<String, String> body) {
        String conversationId = body != null ? body.get("conversationId") : null;

        ChatConversation conversation = chatService.getOrCreateConversation(conversationId);

        // Gửi AI greeting ban đầu
        String greeting = "👋 Chào bạn! Tôi là AI hỗ trợ của TT Care+. Tôi có thể giúp bạn về:\n" +
                "✅ Giờ làm việc\n" +
                "✅ Địa chỉ phòng khám\n" +
                "✅ Giá dịch vụ\n" +
                "✅ Đặt lịch khám\n\n" +
                "Hoặc bạn có thể nói '(nói chuyện với admin)' để được hỗ trợ trực tiếp! 😊";

        chatService.saveAIMessage(conversation, greeting);

        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", conversation.getConversationId());
        response.put("status", conversation.getStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * Nhận tin nhắn từ khách hàng và trả lời
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> body) {
        String conversationId = body.get("conversationId");
        String message = body.get("message");
        String customerName = body.get("customerName");
        String customerPhone = body.get("customerPhone");

        ChatConversation conversation = chatService.getOrCreateConversation(conversationId);

        // Lưu tin nhắn khách hàng
        chatService.saveCustomerMessage(conversation, message, customerName, customerPhone);

        // Kiểm tra nếu khách hàng yêu cầu admin
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("gặp admin") || lowerMessage.contains("chuyển admin") ||
                lowerMessage.contains("nói chuyện admin") || lowerMessage.contains("admin trực tiếp")) {
            chatService.transferToAdmin(conversation);

            Map<String, Object> response = new HashMap<>();
            response.put("conversationId", conversation.getConversationId());
            response.put("status", ChatStatus.WAITING_ADMIN);
            response.put("aiMessage", "Bạn sẽ được chuyển tiếp đến admin!");

            return ResponseEntity.ok(response);
        }

        // AI tự động trả lời
        String aiResponse = chatService.generateAIResponse(message);

        if ("TRANSFER_TO_ADMIN".equals(aiResponse)) {
            chatService.transferToAdmin(conversation);

            Map<String, Object> response = new HashMap<>();
            response.put("conversationId", conversation.getConversationId());
            response.put("status", ChatStatus.WAITING_ADMIN);
            response.put("aiMessage", "Bạn sẽ được chuyển tiếp đến admin!");

            return ResponseEntity.ok(response);
        }

        chatService.saveAIMessage(conversation, aiResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", conversation.getConversationId());
        response.put("status", conversation.getStatus());
        response.put("aiMessage", aiResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy lịch sử tin nhắn
     */
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String conversationId) {
        ChatConversation conversation = chatService.getOrCreateConversation(conversationId);
        List<ChatMessage> messages = chatService.getConversationHistory(conversation);

        return ResponseEntity.ok(messages);
    }

    /**
     * Lấy trạng thái cuộc trò chuyện
     */
    @GetMapping("/status/{conversationId}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String conversationId) {
        ChatConversation conversation = chatService.getOrCreateConversation(conversationId);

        Map<String, Object> response = new HashMap<>();
        response.put("conversationId", conversation.getConversationId());
        response.put("status", conversation.getStatus());
        response.put("customerName", conversation.getCustomerName());
        response.put("customerPhone", conversation.getCustomerPhone());

        return ResponseEntity.ok(response);
    }
}
