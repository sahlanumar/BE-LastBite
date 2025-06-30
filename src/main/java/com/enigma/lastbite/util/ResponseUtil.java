package com.enigma.lastbite.util;



import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus httpStatus, String message, T data) {
        CommonResponse<T> response = CommonResponse.<T>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(data)
                .build();
                
        return ResponseEntity.status(httpStatus).body(response);
    }
    
    public static <T> ResponseEntity<CommonResponse<T>> buildResponse(HttpStatus httpStatus, String message, T data, Page<?> page) {
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page.getNumber() + 1) // Page dimulai dari 0, tapi untuk response kita tampilkan dari 1
                .totalPage(page.getTotalPages())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
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
