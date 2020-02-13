package com.abclinic.server.constant;

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
    SCHEDULE(6, "đặt lịch gửi chỉ số sức khỏe"),
    SEND_INDEX(7, "gửi chỉ số sức khỏe");

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