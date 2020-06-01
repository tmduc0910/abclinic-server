package com.abclinic.server.service;

import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.dto.NotificationDto;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.NotificationRepository;
import com.abclinic.server.service.entity.IDataMapperService;
import com.abclinic.server.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 2/10/2020 2:33 PM
 */

@Service
public class NotificationService implements IDataMapperService<Notification> {
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

        if (message.getMessageType() == MessageType.SCHEDULE_REMINDER) {
            LocalDateTime now = DateTimeUtils.getCurrent();
            long time = DateTimeUtils.getDistanceByHour(now, ((HealthIndexSchedule) message.getPayload()).getEndedAt());
            return new StringJoiner(" ")
                    .add("Bạn")
                    .add(message.getMessageType().getAction())
                    .add("vào")
                    .add(String.valueOf(time))
                    .add("tiếng nữa")
                    .toString();
        }
        return builder.toString();
    }

    public Notification makeNotification(User sender, NotificationMessage notificationMessage) {
        String message = pack(sender, notificationMessage);
        Notification notification = new Notification(sender, notificationMessage.getTargetUser(), message, notificationMessage.getMessageType().getValue());
        if (notificationMessage.getPayload() instanceof PatientHealthIndexField)
            notification.setPayloadId(((PatientHealthIndexField) notificationMessage.getPayload()).getTagId());
        else notification.setPayloadId(notificationMessage.getPayload().getId());
        notification = notificationRepository.save(notification);
        webSocketService.broadcast(notificationMessage.getTargetUser(), new NotificationDto(notification.getId(), notification.getReceiver().getId(), message, notification.getType()));
        return notification;
    }

    public List<Notification> makeNotification(User sender, List<NotificationMessage> notificationMessages) {
        return notificationMessages.stream().map(m -> makeNotification(sender, m)).collect(Collectors.toList());
    }

    @Override
    public Notification getById(long id) throws NotFoundException {
        return notificationRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Notification save(Notification obj) {
        return notificationRepository.save(obj);
    }

    @Override
    public Page<Notification> getList(User user, Pageable pageable) {
        return notificationRepository.findByReceiver(user, pageable).orElseThrow(NotFoundException::new);
    }
}
