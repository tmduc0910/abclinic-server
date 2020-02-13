package com.abclinic.server.service;

import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 2/10/2020 2:33 PM
 */

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
        return notificationRepository.save(notification);
    }
}
