package com.abclinic.server.service;

import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.NotificationRepository;
import com.abclinic.server.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 2/10/2020 2:33 PM
 */

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;
    private WebSocketService webSocketService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, WebSocketService webSocketService) {
        this.notificationRepository = notificationRepository;
        this.webSocketService = webSocketService;
    }

    public String pack(User sender, NotificationMessage message) {
        StringJoiner builder = new StringJoiner(" ");
        switch (sender.getRole()) {
            case PATIENT:
                builder.add("Bệnh nhân");
                break;
            case COORDINATOR:
                builder.add("Điều phối viên");
                break;
            default:
                builder.add("Bác sĩ");
                break;
        }
        builder.add(sender.getName());
        builder.add("đã");
        builder.add(message.getMessageType().getAction());
        builder.add("cho bạn");
//        if (message.getPayload() instanceof Inquiry)
//            builder.add("yêu cầu tư vấn này.");
//        else if (message.getPayload() instanceof Record)
//            builder.add("tư vấn này.");
//        else if (message.getPayload() instanceof Reply)
//            builder.add("trả lời tư vấn này.");
        return builder.toString();
    }

    public Notification makeNotification(User sender, NotificationMessage notificationMessage) {
        String message = pack(sender, notificationMessage);
        Notification notification = new Notification(sender, notificationMessage.getTargetUser(), message, notificationMessage.getMessageType().getValue());
        notification.setPayloadId(notificationMessage.getPayload().getId());
        webSocketService.broadcast(sender, message);
        return notificationRepository.save(notification);
    }

    public List<Notification> makeNotification(User sender, List<NotificationMessage> notificationMessages) {
        return notificationMessages.stream().map(m -> makeNotification(sender, m)).collect(Collectors.toList());
    }
}
