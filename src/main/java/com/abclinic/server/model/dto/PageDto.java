package com.abclinic.server.model.dto;

import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 5/25/2020 7:51 PM
 */
public class PageDto<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int page;
    private int size;
    private Sort sort;
    private boolean isLast;

    public PageDto(List<T> content, long totalElements, int page, int size, Sort sort) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.isLast = page >= totalPages - 1;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
