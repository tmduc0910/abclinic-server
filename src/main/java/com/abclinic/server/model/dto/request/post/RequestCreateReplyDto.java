package com.abclinic.server.model.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/15/2020 9:16 AM
 */
public class RequestCreateReplyDto {
    @JsonProperty("inquiry_id")
    private long inquiryId;
    private String reply;

    public long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(long inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
