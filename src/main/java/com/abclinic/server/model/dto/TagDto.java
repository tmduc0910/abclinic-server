package com.abclinic.server.model.dto;

import java.time.LocalDateTime;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/27/2020 11:26 AM
 */
public class TagDto {
    private long tagId;
    private LocalDateTime createdAt;

    public TagDto(long tagId, LocalDateTime createdAt) {
        this.tagId = tagId;
        this.createdAt = createdAt;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TagDto)
            return this.tagId == ((TagDto) obj).tagId;
        return false;
    }
}
