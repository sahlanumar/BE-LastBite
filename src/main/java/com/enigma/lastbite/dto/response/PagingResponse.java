package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {
    private int currentPage;
    private int totalPage;
    private int size;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
    private String nextPage;
    private String previousPage;
}
