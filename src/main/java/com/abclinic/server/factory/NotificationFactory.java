package com.abclinic.server.factory;

import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.model.entity.notification.NotificationMessage;
import com.abclinic.server.model.entity.payload.IPayload;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.IPayloadIpml;
import com.abclinic.server.model.entity.payload.Reply;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.factory
 * @created 2/11/2020 3:45 PM
 */

public class NotificationFactory {
    public static <T extends IPayload> NotificationMessage getMessage(MessageType messageType, User target, T payload) {
        return new NotificationMessage(messageType, target, payload);
    }

    public static List<NotificationMessage> getInquiryMessages(Inquiry payload) {
        List<NotificationMessage> list = new ArrayList<>();

        NotificationMessage message = getMessage(MessageType.INQUIRE, payload.getPatient().getPractitioner(), payload);
        list.add(message);
            list.addAll(payload.getPatient().getDietitians().stream().map(d -> {
                message.setTargetUser(d);
                return message;
            }).collect(Collectors.toList()));
            list.addAll(payload.getPatient().getSpecialists().stream().map(s -> {
                message.setTargetUser(s);
                return message;
            }).collect(Collectors.toList()));
        return list;
    }

    public static List<NotificationMessage> getReplyMessages(User sender, Reply reply) {
        List<NotificationMessage> list = new ArrayList<>();
        Inquiry inquiry = reply.getInquiry();
        Patient patient = inquiry.getPatient();
        if (sender.getRole().equals(Role.PATIENT)) {
            NotificationMessage message = getMessage(MessageType.REPLY, patient.getPractitioner(), inquiry);
            list.add(message);
            list.addAll(patient.getSubDoctors().stream().map(d -> {
                message.setTargetUser(d);
                return message;
            }).collect(Collectors.toList())) ;
        } else {
            list.add(getMessage(MessageType.REPLY, patient, inquiry));
        }
        return list;
    }

    public static List<NotificationMessage> getDeactivateMessages(Patient sender) {
        List<NotificationMessage> list = new ArrayList<>();
        if (sender.getPractitioner() != null)
            list.add(getMessage(MessageType.DEACTIVATED, sender.getPractitioner(), sender));
        sender.getSubDoctors().forEach(d -> list.add(getMessage(MessageType.DEACTIVATED, d, sender)));
        return list;
    }
}
