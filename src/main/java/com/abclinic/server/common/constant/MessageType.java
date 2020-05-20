package com.abclinic.server.common.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.constant
 * @created 2/8/2020 3:02 PM
 */
public enum MessageType {
    INQUIRE(0, "gửi yêu cầu tư vấn"),
    ADVICE(1, "tư vấn"),
    REPLY(2, "trả lời tư vấn"),
    ASSIGN(3, "gán quyền quản lý"),
    ACCEPT_ASSIGN(4, "chấp nhận quản lý bệnh nhân"),
    DENY_ASSIGN(5, "từ chối quản lý bệnh nhân"),
    REMOVE_ASSIGN(6, "hủy quyền phụ trách"),
    SCHEDULE(7, "đặt lịch gửi chỉ số sức khỏe"),
    SEND_INDEX(8, "gửi chỉ số sức khỏe"),
    SCHEDULE_REMINDER(9, "có lịch gửi chỉ số sức khỏe phải nộp"),
    DEACTIVATED(10, "đã được xóa");

    private int value;
    private String action;

    MessageType(int value, String action) {
        this.value = value;
        this.action = action;
    }

    public int getValue() {
        return value;
    }

    public String getAction() {
        return action;
    }
}