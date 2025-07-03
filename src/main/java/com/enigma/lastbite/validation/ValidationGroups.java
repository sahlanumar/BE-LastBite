package com.enigma.lastbite.validation;

/**
 * Interface penampung untuk grup validasi.
 * Marker interfaces ini digunakan untuk membedakan konteks validasi,
 * seperti membedakan antara registrasi Customer, Seller, atau Admin.
 */
public interface ValidationGroups {
    interface Create {}
    interface Update {}
}