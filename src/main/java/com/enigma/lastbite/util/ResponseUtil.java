package com.enigma.lastbite.util;



import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.PagingResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class ResponseUtil {
    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus httpStatus, String message, T data) {
        CommonResponse<T> response = CommonResponse.<T>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(data)
                .build();
                
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(
            HttpStatus httpStatus,
            String message,
            T data,
            Page<?> page,
            String baseUrl,
            Object filter,
            String sortField,
            String sortDir
    ) {
        int currentPage = page.getNumber();
        int size = page.getSize();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(baseUrl);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> filterParams = mapper.convertValue(filter, new TypeReference<Map<String, String>>() {});

        filterParams.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                uriBuilder.queryParam(key, value);
            }
        });

        uriBuilder.queryParam("sortField", sortField);
        uriBuilder.queryParam("sortDir", sortDir);
        uriBuilder.queryParam("size", size);

        String next = null;
        if (page.hasNext()) {
            next = uriBuilder.cloneBuilder()
                    .queryParam("page", currentPage + 1)
                    .toUriString();
        }

        String prev = null;
        if (page.hasPrevious()) {
            prev = uriBuilder.cloneBuilder()
                    .queryParam("page", currentPage - 1)
                    .toUriString();
        }

        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(currentPage + 1)
                .totalPage(page.getTotalPages())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .nextPage(next)
                .previousPage(prev)
                .build();

        CommonResponse<T> response = CommonResponse.<T>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(data)
                .paging(pagingResponse)
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }
}
