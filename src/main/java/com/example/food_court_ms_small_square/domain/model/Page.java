package com.example.food_court_ms_small_square.domain.model;

import java.util.List;

public class Page<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;

    public Page(List<T> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
