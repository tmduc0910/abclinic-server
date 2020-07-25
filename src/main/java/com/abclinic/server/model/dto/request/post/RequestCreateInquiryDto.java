package com.abclinic.server.model.dto.request.post;

import com.abclinic.server.common.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto.request.post
 * @created 5/14/2020 9:27 PM
 */
public class RequestCreateInquiryDto {
    @JsonProperty("album_id")
    private String albumId = null;
    private int type;

    @JsonProperty("chain_id")
    private long chainId = 0;

    private String content;
    private String date;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        try {
            return DateTimeUtils.parseDateTime(date);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
