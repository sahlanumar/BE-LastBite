package com.enigma.lastbite.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //====================================================================
    // KESALAHAN UMUM (GENERAL ERRORS)
    //====================================================================
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Permintaan tidak valid"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Anda tidak memiliki izin untuk melakukan operasi ini"),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "Data tidak ditemukan"),
    INVALID_STATUS_FILTER(HttpStatus.BAD_REQUEST, "Filter status tidak valid"),
    STATUS_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Status tidak diizinkan"),

    //====================================================================
    // PENGGUNA, PELANGGAN, & PENJUAL (USERS, CUSTOMERS, & SELLERS)
    //====================================================================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Pengguna tidak ditemukan"),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan"),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "Penjual tidak ditemukan"),
    EMAIL_ALREADY_REGISTERED(HttpStatus.CONFLICT, "Email sudah terdaftar"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email sudah digunakan"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username sudah digunakan"),
    PHONENUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Nomor telepon sudah digunakan"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Kata sandi tidak valid"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Konfirmasi kata sandi tidak cocok"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "Peran tidak valid"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "Peran pengguna tidak valid"),
    INVALID_USER_DATA(HttpStatus.BAD_REQUEST, "Data pengguna tidak valid"),
    INVALID_CUSTOMER_DATA(HttpStatus.BAD_REQUEST, "Data pelanggan tidak valid"),
    USER_CANT_SUSPEND(HttpStatus.BAD_REQUEST, "Anda tidak dapat menonaktifkan akun Anda"),
    INVALID_SUPERADMIN_KEY(HttpStatus.BAD_REQUEST, "Kunci superadmin tidak valid"),
    SELLER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "Penjual tidak aktif"),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "Kata sandi diperlukan"),
    USERNAME_OR_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "Username atau email harus diisi"),

    //====================================================================
    // TOKEN OTENTIKASI (AUTHENTICATION TOKEN)
    //====================================================================
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "Refresh token tidak valid"),
    NOT_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Token yang diberikan bukan refresh token"),

    //====================================================================
    // MENU & STOK (MENU & STOCK)
    //====================================================================
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "Menu tidak ditemukan"),
    INVALID_MENU_DATA(HttpStatus.BAD_REQUEST, "Data menu tidak valid"),
    MENU_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Menu sedang tidak tersedia"),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Stok produk habis"),
    INVALID_MENU_ITEM(HttpStatus.BAD_REQUEST, "Item menu tidak valid"),
    MENU_ITEM_NOT_IN_ORDER(HttpStatus.BAD_REQUEST, "Item menu tidak ada dalam pesanan"),
    MENU_ITEM_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Item menu sedang tidak tersedia"),

    //====================================================================
    // KERANJANG (CART)
    //====================================================================
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Keranjang tidak ditemukan"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Item di keranjang tidak ditemukan"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "Jumlah tidak valid"),
    CART_EMPTY_FOR_SELLER(HttpStatus.BAD_REQUEST, "Keranjang kosong untuk penjual ini"),

    //====================================================================
    // PESANAN & TRANSAKSI (ORDER & TRANSACTION)
    //====================================================================
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Pesanan tidak ditemukan"),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Transaksi tidak ditemukan"),
    TRANSACTION_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Detail transaksi tidak ditemukan"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "Status pesanan tidak valid"),
    INVALID_TRANSACTION_DATA(HttpStatus.BAD_REQUEST, "Data transaksi tidak valid"),
    INVALID_TRANSACTION_DETAIL_DATA(HttpStatus.BAD_REQUEST, "Data detail transaksi tidak valid"),
    ORDER_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "Pesanan belum selesai"),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "Kode verifikasi tidak valid"),
    NO_AVAILABLE_ITEMS_FOR_CHECKOUT(HttpStatus.BAD_REQUEST, "Tidak ada item yang tersedia untuk checkout"),
    // -- Alur Status Pesanan
    ORDER_NOT_PENDING_PAYMENT(HttpStatus.CONFLICT, "Gagal memperbarui status, pesanan tidak menunggu pembayaran"),
    ORDER_NOT_PAID(HttpStatus.CONFLICT, "Pesanan harus LUNAS untuk dapat diterima"),
    ORDER_NOT_PREPARING(HttpStatus.CONFLICT, "Pesanan harus dalam status DIPERSIAPKAN untuk ditandai sebagai siap"),
    ORDER_NOT_READY_FOR_PICKUP(HttpStatus.CONFLICT, "Pesanan belum siap untuk diambil"),

    //====================================================================
    // PEMBAYARAN & SALDO (PAYMENT & BALANCE)
    //====================================================================
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "Saldo tidak mencukupi"),
    MIDTRANS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada sistem pembayaran"),

    //====================================================================
    // ULASAN (REVIEW)
    //====================================================================
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Ulasan tidak ditemukan"),
    UNAUTHORIZED_REVIEW(HttpStatus.UNAUTHORIZED, "Anda tidak memiliki izin untuk melakukan operasi ini"),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "Rating tidak valid"),
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "Anda sudah memberikan ulasan untuk pesanan ini"),

    //====================================================================
    // FILE & GAMBAR (FILE & IMAGE)
    //====================================================================
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "Gagal mengunggah gambar"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal mengunggah file"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File tidak ditemukan");


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