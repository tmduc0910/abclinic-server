package com.abclinic.server.controller;

import com.abclinic.server.base.BaseController;
import com.abclinic.server.base.Views;
import com.abclinic.server.constant.MessageType;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import org.aspectj.weaver.ast.Not;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/13/2020 2:00 PM
 */
@RestController
public class NotificationResourceController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(NotificationResourceController.class);
    }

/*    @PostMapping("/notifications")
    public ResponseEntity createNotification(@ApiIgnore @RequestAttribute("user") User user,
                                             @RequestParam("notification_id") long notificationId,
                                             @RequestParam("is_accepted") boolean isAccepted) {
        Notification notification = notificationRepository.findById(notificationId);
        if (notification != null) {
            if (notification.getType() == MessageType.ASSIGN.getValue()) {
                Patient patient = patientRepository.findById(notification.getSender().getId());
                if (isAccepted) {
                    Doctor doctor;
                    switch (user.getRole()) {
                        case PRACTITIONER:
                            doctor = practitionerRepository.findById(user.getId());
                            patient.setPractitioner((Practitioner) doctor);
                            break;
                        case DIETITIAN:
                            doctor = dietitianRepository.findById(user.getId());
                            patient.
                            break;
                        case SPECIALIST:

                    }
                }
            }
        }
    }*/

    @GetMapping("/notifications")
    @JsonView(Views.Abridged.class)
    public ResponseEntity<List<Notification>> getNotifications(@ApiIgnore @RequestAttribute("user") User user,
                                                               @RequestParam("page") int page,
                                                               @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Optional<List<Notification>> op = notificationRepository.findByReceiver(user, pageable);
        if (op.isPresent())
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }

    @GetMapping("/notifications/{id}")
    @JsonView(Views.Private.class)
    public ResponseEntity<Notification> getNotification(@ApiIgnore @RequestAttribute("user") User user,
                                                        @PathVariable("id") long id) {
        Notification notification = notificationRepository.findById(id);
        if (notification != null) {
            if (notification.getReceiver().equals(user))
                return new ResponseEntity<>(notification, HttpStatus.OK);
            else throw new ForbiddenException(user.getId(), "Không được phép truy cập thông báo của người dùng khác");
        } else throw new NotFoundException(user.getId());
    }
}
