# LastBite Backend

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue.svg)](https://www.postgresql.org)
[![Maven](https://img.shields.io/badge/Maven-4.0-red.svg)](https://maven.apache.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Layanan backend yang menggerakkan ekosistem LastBite, termasuk API untuk platform pelanggan, mitra, dan admin. LastBite adalah platform pemesanan makanan yang menghubungkan pengguna dengan berbagai restoran dan penjual makanan.

## 🚀 Fitur

*   **Otentikasi & Otorisasi:** Sistem login dan pendaftaran yang aman untuk pengguna dan penjual menggunakan JWT.
*   **Manajemen Pengguna:** Operasi CRUD untuk mengelola data pengguna.
*   **Manajemen Penjual:** Penjual dapat mengelola profil mereka, daftar menu, dan melacak pesanan.
*   **Pencarian & Daftar Menu:** Pengguna dapat mencari item menu dan melihat daftar menu dari berbagai penjual.
*   **Manajemen Keranjang:** Pengguna dapat menambahkan, memperbarui, dan menghapus item dari keranjang belanja mereka.
*   **Pemesanan & Pembayaran:** Proses pemesanan yang lancar dengan integrasi gateway pembayaran Midtrans.
*   **Ulasan & Peringkat:** Pengguna dapat memberikan ulasan dan peringkat untuk item menu.
*   **Manajemen Penarikan:** Penjual dapat meminta penarikan dana yang diperoleh.
*   **Penyimpanan File:** Menggunakan Cloudinary untuk mengunggah dan mengelola gambar untuk item menu dan foto profil.

## 🔧 Tumpukan Teknologi

*   **Java Spring Boot:** Kerangka kerja utama untuk membangun aplikasi.
*   **Spring Web:** Untuk membangun layanan RESTful.
*   **Spring Data JPA:** Untuk berinteraksi dengan database.
*   **Spring Security:** Untuk otentikasi dan otorisasi.
*   **Spring WebSocket:** Untuk komunikasi dua arah real-time.
*   **PostgreSQL:** Sistem manajemen database relasional.
*   **Maven:** Alat manajemen proyek dan build.
*   **Lombok:** Untuk mengurangi kode boilerplate.
*   **JWT (Java Web Token):** Untuk otentikasi berbasis token yang aman.
*   **Midtrans:** Gateway pembayaran untuk memproses transaksi.
*   **Cloudinary:** Untuk layanan penyimpanan dan manajemen file berbasis cloud.
*   **OpenAPI (Swagger):** Untuk dokumentasi dan pengujian API.

## ⚙️ Pengaturan Proyek

### Prasyarat
- Java 17
- Maven
- PostgreSQL

### Instalasi

1.  **Kloning Repositori**
    ```bash
    git clone https://github.com/USERNAME/lastbite-backend.git
    cd lastbite-backend
    ```

2.  **Konfigurasi Database**
    - Buat database baru di PostgreSQL.
    - Salin file `.env.example` ke `.env` dan perbarui variabel lingkungan berikut dengan kredensial database Anda:
      ```env
      DB_URL=jdbc:postgresql://localhost:5432/your_database_name
      DB_USERNAME=your_username
      DB_PASSWORD=your_password
      ```

3.  **Konfigurasi Variabel Lingkungan**
    - Perbarui variabel lingkungan yang tersisa di file `.env` Anda:
      ```env
      APP_JWT_SECRET=your_jwt_secret
      APP_JWT_EXPIRATION_MS=your_jwt_expiration_in_ms
      CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
      CLOUDINARY_API_KEY=your_cloudinary_api_key
      CLOUDINARY_API_SECRET=your_cloudinary_api_secret
      MIDTRANS_SERVER_KEY=your_midtrans_server_key
      MIDTRANS_CLIENT_KEY=your_midtrans_client_key
      ```

4.  **Jalankan Aplikasi**
    ```bash
    ./mvnw spring-boot:run
    ```
    Aplikasi akan berjalan di `http://localhost:8080`.

## 📄 Dokumentasi API

Setelah aplikasi berjalan, Anda dapat mengakses dokumentasi API Swagger di browser Anda di:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🤝 Berkontribusi

Kontribusi dipersilakan! Silakan fork repositori ini dan buat pull request untuk menambahkan fitur baru atau memperbaiki bug.

## 📝 Lisensi

Proyek ini dilisensikan di bawah Lisensi MIT. Lihat file [LICENSE](LICENSE) untuk detailnya.