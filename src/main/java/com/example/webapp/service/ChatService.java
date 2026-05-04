package com.example.webapp.service;

import com.example.webapp.entity.*;
import com.example.webapp.repository.ChatConversationRepository;
import com.example.webapp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    /**
     * Tạo cuộc trò chuyện mới với ID ngẫu nhiên
     */
    public ChatConversation createConversation() {
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationId(UUID.randomUUID().toString());
        conversation.setStatus(ChatStatus.WAITING_AI);
        return conversationRepository.save(conversation);
    }

    /**
     * Lấy hoặc tạo cuộc trò chuyện dựa trên conversationId
     */
    public ChatConversation getOrCreateConversation(String conversationId) {
        if (conversationId != null && !conversationId.isEmpty()) {
            Optional<ChatConversation> existing = conversationRepository.findByConversationId(conversationId);
            if (existing.isPresent()) {
                return existing.get();
            }
        }
        return createConversation();
    }

    /**
     * Lưu tin nhắn của khách hàng
     */
    public void saveCustomerMessage(ChatConversation conversation, String content, String customerName,
            String customerPhone) {
        // Cập nhật thông tin khách hàng nếu có
        if (customerName != null && !customerName.isEmpty()) {
            conversation.setCustomerName(customerName);
        }
        if (customerPhone != null && !customerPhone.isEmpty()) {
            conversation.setCustomerPhone(customerPhone);
        }

        ChatMessage message = new ChatMessage(conversation, ChatSender.CUSTOMER, content, customerName);
        messageRepository.save(message);

        // Cập nhật thời gian tin nhắn cuối cùng
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * AI tự động trả lời dựa trên nội dung tin nhắn
     */
    public String generateAIResponse(String customerMessage) {
        String lowerMessage = customerMessage.toLowerCase();

        // Kiểm tra yêu cầu chuyển admin
        if (containsAny(lowerMessage, "gặp admin", "chuyển admin", "nói chuyện admin", "admin trực tiếp",
                "muốn nói chuyện với admin")) {
            return "TRANSFER_TO_ADMIN"; // Signal để backend xử lý
        }

        // Hỏi giờ làm việc
        if (containsAny(lowerMessage, "giờ làm việc", "mấy giờ", "giờ mở cửa", "giờ hoạt động")) {
            return "Phòng khám TT Care+ hoạt động từ 8:00 - 17:00 hôm nay. Thứ 2 đến Thứ 7. Chúng tôi đóng cửa Chủ nhật. 📅";
        }

        // Hỏi địa chỉ
        if (containsAny(lowerMessage, "địa chỉ", "ở đâu", "vị trí", "tìm đường")) {
            return "Địa chỉ của chúng tôi:\n📍 TT Care+ - Phòng khám đa khoa\n🏥 Yên Nghĩa, Hà Đông, Hà Nội\n☎️ 0985081624\n\nBạn có thể sử dụng Google Maps để tìm đường đến phòng khám! 🗺️";
        }

        // Hỏi giá dịch vụ
        if (containsAny(lowerMessage, "giá", "phí", "bao nhiêu", "chi phí", "dịch vụ")) {
            return "Bảng giá dịch vụ của chúng tôi:\n💰 Khám lâm sàng: 200.000 - 300.000 VNĐ\n💰 Siêu âm: 300.000 - 500.000 VNĐ\n💰 Xét nghiệm máu: 150.000 - 400.000 VNĐ\n\nGiá có thể thay đổi tùy theo dịch vụ cụ thể. Hãy ghi danh để được tư vấn chi tiết! 📞";
        }

        // Hỏi đặt lịch khám
        if (containsAny(lowerMessage, "đặt lịch", "book", "lịch khám", "lịch hẹn", "muốn khám")) {
            return "Tuyệt vời! 🎉 Để đặt lịch khám, chúng tôi cần:\n1️⃣ Tên của bạn\n2️⃣ Số điện thoại\n3️⃣ Ngày/giờ mong muốn khám\n4️⃣ Chuyên khoa (Tim mạch, Nhi, Sản phụ khoa, v.v)\n\nVui lòng cung cấp thông tin này để tiếp tục! 👨‍⚕️";
        }

        // Hỏi về bác sĩ
        if (containsAny(lowerMessage, "bác sĩ", "BS", "thầy thuốc", "medical staff")) {
            return "Phòng khám chúng tôi có đội ngũ bác sĩ giàu kinh nghiệm từ 10-20 năm, chuyên môn hóa trong các lĩnh vực khác nhau. 👨‍⚕️👩‍⚕️\n\nBạn muốn tìm hiểu về bác sĩ nào hoặc chuyên khoa cụ thể không? 🔍";
        }

        // Hỏi về phòng khám
        if (containsAny(lowerMessage, "phòng khám", "bệnh viện", "cơ sở y tế", "công ty", "về chúng tôi")) {
            return "TT Care+ là phòng khám đa khoa hiện đại, cung cấp dịch vụ khám chữa bệnh toàn diện với trang thiết bị tiên tiến và đội ngũ y tế chuyên nghiệp. 🏥\n\nChúng tôi cam kết mang đến dịch vụ chất lượng cao với chi phí hợp lý! ✨";
        }

        // Câu hỏi về bảo hiểm
        if (containsAny(lowerMessage, "bảo hiểm", "bhyt", "thanh toán", "thẻ bảo hiểm")) {
            return "Phòng khám chúng tôi chấp nhận thanh toán bằng:\n💳 Thẻ BHYT (Bảo Hiểm Y Tế)\n💳 Thẻ Tín dụng\n💵 Tiền mặt\n\nHãy mang theo thẻ BHYT để được thanh toán tối ưu nhất! 🆔";
        }

        // Câu hỏi chung chung - AI không biết trả lời
        return "Cảm ơn bạn! 😊 Câu hỏi của bạn rất hay, nhưng tôi cần sự giúp đỡ của admin để trả lời chi tiết hơn. Vui lòng đợi một lát hoặc yêu cầu '(nói chuyện với admin)' để được trợ giúp ngay lập tức! 👨‍💼";
    }

    /**
     * Lưu AI response
     */
    public void saveAIMessage(ChatConversation conversation, String aiResponse) {
        ChatMessage message = new ChatMessage(conversation, ChatSender.AI, aiResponse, "AI Hỗ Trợ");
        messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    /**
     * Chuyển cuộc trò chuyện sang chế độ admin
     */
    public void transferToAdmin(ChatConversation conversation) {
        conversation.setStatus(ChatStatus.WAITING_ADMIN);
        conversationRepository.save(conversation);

        // Lưu thông báo trong cuộc trò chuyện
        ChatMessage notification = new ChatMessage(
                conversation,
                ChatSender.AI,
                "✨ Bạn vừa được chuyển sang chế độ hỗ trợ từ admin. Admin sẽ trả lời bạn trong vòng 1-2 phút. Cảm ơn bạn đã chờ đợi!",
                "Hệ Thống");
        messageRepository.save(notification);
    }

    /**
     * Gán admin cho cuộc trò chuyện
     */
    public void assignAdminToConversation(ChatConversation conversation, NguoiDung admin) {
        conversation.setAssignedAdmin(admin);
        conversation.setStatus(ChatStatus.WAITING_ADMIN);
        conversationRepository.save(conversation);
    }

    /**
     * Lưu tin nhắn từ admin
     */
    public void saveAdminMessage(ChatConversation conversation, String content, String adminName) {
        ChatMessage message = new ChatMessage(conversation, ChatSender.ADMIN, content, adminName);
        messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setStatus(ChatStatus.WAITING_ADMIN);
        conversationRepository.save(conversation);
    }

    /**
     * Lấy tất cả tin nhắn của một cuộc trò chuyện
     */
    public List<ChatMessage> getConversationHistory(ChatConversation conversation) {
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }

    /**
     * Lấy danh sách cuộc trò chuyện chờ AI
     */
    public List<ChatConversation> getPendingAIConversations() {
        return conversationRepository.findByStatusOrderByLastMessageAtDesc(ChatStatus.WAITING_AI);
    }

    /**
     * Lấy danh sách cuộc trò chuyện chờ admin
     */
    public List<ChatConversation> getPendingAdminConversations() {
        return conversationRepository.findByStatusOrderByLastMessageAtDesc(ChatStatus.WAITING_ADMIN);
    }

    /**
     * Lấy danh sách cuộc trò chuyện của một admin cụ thể
     */
    public List<ChatConversation> getAdminConversations(Long adminId) {
        return conversationRepository.findByStatusAndAssignedAdminIdOrderByLastMessageAtDesc(ChatStatus.WAITING_ADMIN,
                adminId);
    }

    /**
     * Hỗ trợ: kiểm tra nếu message chứa bất kỳ từ khóa nào
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Đóng cuộc trò chuyện
     */
    public void closeConversation(ChatConversation conversation) {
        conversation.setStatus(ChatStatus.CLOSED);
        conversationRepository.save(conversation);

        ChatMessage closureMessage = new ChatMessage(
                conversation,
                ChatSender.AI,
                "Cảm ơn bạn đã liên hệ với TT Care+! Nếu bạn có thêm câu hỏi, vui lòng liên hệ lại bất kỳ lúc nào. 👋",
                "Hệ Thống");
        messageRepository.save(closureMessage);
    }
}
