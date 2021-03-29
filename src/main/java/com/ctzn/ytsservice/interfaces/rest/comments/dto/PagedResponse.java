package com.ctzn.ytsservice.interfaces.rest.comments.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {
    List<T> content;
    int totalPages;
    long totalElements;
    int number;
    int size;
    int numberOfElements;
}
