package com.enigma.lastbite.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ORDER_NOT_PENDING_PAYMENT(HttpStatus.CONFLICT, "Cannot update status, order is not pending payment"),
    ORDER_NOT_PAID(HttpStatus.CONFLICT, "Order must be PAID to be accepted"),
    ORDER_NOT_PREPARING(HttpStatus.CONFLICT, "Order must be PREPARING to be marked as ready"),
    ORDER_NOT_READY_FOR_PICKUP(HttpStatus.CONFLICT, "Order is not ready for pickup"),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "Invalid verification code"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "Invalid order status"),
    INVALID_MENU_ITEM(HttpStatus.BAD_REQUEST, "Invalid menu item"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Review not found"),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "Invalid rating"),
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "Duplicate review"),

    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "Invalid quantity"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart item not found"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found"),

    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "Refresh token tidak valid"),
    NOT_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Token yang diberikan bukan refresh token"),

    MIDTRANS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Midtrans error"),

    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not found"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed"),

    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "Seller not found"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_USER_DATA(HttpStatus.BAD_REQUEST, "Invalid user data"),
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "Email already registered"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid password"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "Invalid role"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "Invalid user role"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Password not match"),

    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "Menu not found"),
    INVALID_MENU_DATA(HttpStatus.BAD_REQUEST, "Invalid menu data"),
    MENU_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Menu not available"),

    //Customer related errors
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
    INVALID_CUSTOMER_DATA(HttpStatus.BAD_REQUEST, "Invalid customer data"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username already exists"),
    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Phone already exists"),

    //Transaction related errors
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Transaction not found"),
    INVALID_TRANSACTION_DATA(HttpStatus.BAD_REQUEST, "Invalid transaction data"),

    //TRANSACTION DETAIL related errors
    TRANSACTION_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Transaction detail not found"),
    INVALID_TRANSACTION_DETAIL_DATA(HttpStatus.BAD_REQUEST, "Invalid transaction detail data"),
    
    // Generic errors
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
