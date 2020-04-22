package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.BaseController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.Reply;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/25/2020 2:19 PM
 */
@RestController
public class ReplyResourceController extends BaseController {
    @Autowired
    private NotificationService notificationService;

    @Override
    public void init() {
        this.logger = LoggerFactory.getLogger(ReplyResourceController.class);
    }

    @PostMapping("/replies")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Tạo câu trả lời tư vấn",
            notes = "Trả về 201 CREATED hoặc 400 BAD REQUEST hoặc 403 FORBIDDEN",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inquiry-id", value = "Mã ID của yêu cầu tư vấn", required = true, paramType = "int", example = "1"),
            @ApiImplicitParam(name = "reply", value = "Câu trả lời", required = true, paramType = "string", example = "thắc mắc")
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo câu trả lời thành công"),
            @ApiResponse(code = 400, message = "Mã ID của yêu cầu tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Bạn không có quyền tạo câu trả lời cho tư vấn này")
    })
    public ResponseEntity createReply(@ApiIgnore @RequestAttribute("User") User user,
                                      @RequestParam("inquiry-id") long inquiryId,
                                      @RequestParam("reply") String reply) {
        Optional<Inquiry> op = inquiryRepository.findById(inquiryId);
        if (op.isPresent()) {
            Inquiry inquiry = op.get();
            if (inquiry.of(user)) {
                Reply r = new Reply(inquiry, user, reply);
                notificationService.makeNotification(user, NotificationFactory.getMessages(inquiry));
                save(r);
                return new ResponseEntity(HttpStatus.CREATED);
            } else throw new ForbiddenException(user.getId(), "Bạn không có quyền tạo câu trả lời cho tư vấn này");
        } else throw new BadRequestException(user.getId(), "Mã ID của yêu cầu tư vấn không tồn tại");
    }

    @GetMapping("/replies")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách trả lời của 1 yêu cầu tư vấn",
            notes = "Trả về danh sách trả lời hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inquiry-id", value = "Mã ID của yêu cầu tư vấn", required = true, paramType = "long", example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách trả lời theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không có câu trả lời nào theo yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<List<Reply>> getReplyList(@ApiIgnore @RequestAttribute("User") User user,
                                                    @RequestParam("inquiry-id") long inquiryId) {
        Optional<List<Reply>> op = replyRepository.findByInquiryId(inquiryId);
        if (op.isPresent())
            return new ResponseEntity<>(op.get(), HttpStatus.OK);
        else throw new NotFoundException(user.getId());
    }
}
