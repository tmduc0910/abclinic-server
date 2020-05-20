package com.abclinic.server.controller;

import com.abclinic.server.annotation.authorized.Restricted;
import com.abclinic.server.common.base.CustomController;
import com.abclinic.server.common.base.Views;
import com.abclinic.server.exception.BadRequestException;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.request.post.RequestCreateReplyDto;
import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.payload.Reply;
import com.abclinic.server.model.entity.user.Coordinator;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.InquiryService;
import com.abclinic.server.service.entity.ReplyService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author tmduc
 * @package com.abclinic.server.controller
 * @created 2/25/2020 2:19 PM
 */
@RestController
public class ReplyResourceController extends CustomController {
    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private ReplyService replyService;

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
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tạo câu trả lời thành công"),
            @ApiResponse(code = 400, message = "Mã ID của yêu cầu tư vấn không tồn tại"),
            @ApiResponse(code = 403, message = "Bạn không có quyền tạo câu trả lời cho tư vấn này")
    })
    public ResponseEntity<Reply> createReply(@ApiIgnore @RequestAttribute("User") User user,
                                             @RequestBody RequestCreateReplyDto requestCreateReplyDto) {
        try {
            Inquiry inquiry = inquiryService.getById(requestCreateReplyDto.getInquiryId());
            if (inquiry.of(user)) {
                Reply r = new Reply(inquiry, user, requestCreateReplyDto.getReply());
                r = replyService.save(r);
                notificationService.makeNotification(user, NotificationFactory.getReplyMessages(user, r));
                return new ResponseEntity<>(r, HttpStatus.CREATED);
            } else throw new ForbiddenException(user.getId(), "Bạn không có quyền tạo câu trả lời cho tư vấn này");
        } catch (NotFoundException e) {
            throw new BadRequestException(user.getId(), "Mã ID của tư vấn không tồn tại");
        }
    }

    @GetMapping("/replies")
    @Restricted(excluded = Coordinator.class)
    @ApiOperation(
            value = "Lấy danh sách trả lời của 1 yêu cầu tư vấn",
            notes = "Trả về danh sách trả lời hoặc 404 NOT FOUND",
            tags = {"Đa khoa", "Chuyên khoa", "Dinh dưỡng", "Bệnh nhân"}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "inquiry-id", value = "Mã ID của yêu cầu tư vấn", required = true, dataType = "long", example = "1"),
            @ApiImplicitParam(name = "page", value = "Số thứ tự trang", required = true, paramType = "query", allowableValues = "range[1, infinity]", example = "1"),
            @ApiImplicitParam(name = "size", value = "Kích thước trang", required = true, paramType = "query", example = "4")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Danh sách trả lời theo yêu cầu"),
            @ApiResponse(code = 404, message = "Không có câu trả lời nào theo yêu cầu")
    })
    @JsonView(Views.Abridged.class)
    public ResponseEntity<Page<Reply>> getReplyList(@ApiIgnore @RequestAttribute("User") User user,
                                                    @RequestParam("inquiry-id") long inquiryId,
                                                    @RequestParam("page") int page,
                                                    @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return new ResponseEntity<>(replyService.getList(inquiryId, pageable), HttpStatus.OK);
    }
}
