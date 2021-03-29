package com.ctzn.ytsservice.interfaces.rest.dto;

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
