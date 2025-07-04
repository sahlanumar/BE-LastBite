package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemReviewCreateRequest {

    /**
     * ID dari order (pesanan) di mana item ini dibeli.
     * Field ini WAJIB diisi karena dalam skema baru, sebuah review terikat pada
     * sebuah transaksi pembelian yang spesifik. Ini memungkinkan pengguna untuk
     * mereview item yang sama berulang kali jika mereka membelinya di pesanan yang berbeda.
     * Tanpa ID ini, kita tidak tahu pembelian mana yang sedang diulas.
     */
    @NotBlank(message = "Order ID tidak boleh kosong")
    private String orderId;

    /**
     * ID dari item menu yang akan diulas dari dalam pesanan tersebut.
     * Field ini WAJIB diisi untuk membedakan item mana yang sedang diulas jika dalam
     * satu pesanan terdapat beberapa item berbeda. Ini memastikan ulasan ditautkan
     * ke produk yang benar dalam konteks pesanan yang benar.
     */
    @NotBlank(message = "Menu Item ID tidak boleh kosong")
    private String menuItemId;

    /**
     * Peringkat numerik (misalnya, 1-5 bintang).
     * Field ini WAJIB diisi karena merupakan inti dari sebuah ulasan kuantitatif.
     * Validasi @Min dan @Max memastikan data yang masuk akal dan konsisten
     * sesuai dengan sistem rating yang Anda tentukan (misal: 1 sampai 5 bintang).
     */
    @NotNull(message = "Rating tidak boleh kosong")
    @Min(value = 1, message = "Rating minimal harus 1")
    @Max(value = 5, message = "Rating maksimal harus 5")
    private Integer rating;

    /**
     * Komentar atau ulasan dalam bentuk teks.
     * Field ini bersifat OPSIONAL, karena terkadang pengguna hanya ingin
     * memberikan rating bintang tanpa harus menulis penjelasan panjang.
     */
    private String comment;
}