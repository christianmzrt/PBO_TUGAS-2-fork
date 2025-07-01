# API Pemesanan Vila Sederhana (PBO_TUGAS2)

## 📋 Deskripsi Proyek
API RESTful untuk sistem pemesanan vila yang dibangun menggunakan Java. API ini menyediakan layanan untuk mengelola data vila, customer, booking, dan voucher dengan database SQLite.

## 👥 Informasi Kelompok
- **Anggota 1**: [Richard Christian Mozart Diazoni] - [2405551019] - [PBO (B)]
- **Anggota 2**: [I Komang Cahya Kertha Yasa] - [2405551034] - [PBO (B)]  
- **Anggota 3**: [I Made Nanda Prasetya Dwipaya] - [2405551043] - [PBO (B)]
- **Anggota 4**: [I Kadek Bintang Adi Bimantara] - [2405551049] - [PBO (B)]

## 🚀 Cara Menjalankan Program

### Persyaratan Sistem
- Java JDK 11 atau lebih tinggi
- SQLite
- Postman (untuk testing)

### Langkah Instalasi
1. Clone repository ini:
   ```bash
   git clone [URL_REPOSITORY]
   cd [NAMA_FOLDER]
   ```

2. Compile program:
   ```bash
   javac -cp "lib/*" src/*.java
   ```

3. Jalankan server:
   ```bash
   java -cp "lib/*:src" Httpserver
   ```

4. Server akan berjalan di `http://localhost:8080`

### Autentikasi
API menggunakan API Key untuk autentikasi. Sertakan header berikut dalam setiap request:
```
X-API-Key: [rahasia123]
```

## 📚 API Endpoints

### Villa Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/villas` | Daftar semua vila |
| GET | `/villas/{id}` | Detail vila berdasarkan ID |
| GET | `/villas/{id}/rooms` | Daftar kamar pada vila |
| GET | `/villas/{id}/bookings` | Daftar booking pada vila |
| GET | `/villas/{id}/reviews` | Daftar review pada vila |
| GET | `/villas?ci_date={date}&co_date={date}` | Pencarian vila berdasarkan tanggal |
| POST | `/villas` | Menambah villa baru |
| POST | `/villas/{id}/rooms` | Menambah kamar pada vila |
| PUT | `/villas/{id}` | Update data vila |
| PUT | `/villas/{id}/rooms/{id}` | Update data kamar |
| DELETE | `/villas/{id}` | Hapus vila |
| DELETE | `/villas/{id}/rooms/{id}` | Hapus kamar |

### Customer Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/customers` | Daftar semua customer |
| GET | `/customers/{id}` | Detail customer berdasarkan ID |
| GET | `/customers/{id}/bookings` | Daftar booking customer |
| GET | `/customers/{id}/reviews` | Daftar review customer |
| POST | `/customers` | Registrasi customer baru |
| POST | `/customers/{id}/bookings` | Buat booking baru |
| POST | `/customers/{id}/bookings/{id}/reviews` | Buat review |
| PUT | `/customers/{id}` | Update data customer |
| DELETE | `/customers/{id}` | Delete data customer |

### Voucher Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/vouchers` | Daftar semua voucher |
| GET | `/vouchers/{id}` | Detail voucher berdasarkan ID |
| POST | `/vouchers` | Buat voucher baru |
| PUT | `/vouchers/{id}` | Update voucher |
| DELETE | `/vouchers/{id}` | Hapus voucher |

## 🧪 Testing dengan Postman

### Villa Endpoints

#### 1. GET All Villas
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET All Villas]
![image](https://github.com/user-attachments/assets/f0dea349-2ab9-4fa7-acad-a75e73d0a7bb)

#### 2. GET Villa by ID
**Request:**
- Method: GET  
- URL: `http://localhost:8080/villas/1`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Villa by ID]
![image](https://github.com/user-attachments/assets/c22e345f-1c0e-4c41-828c-8abc1aa4e82b)

#### 3. GET Villa Rooms by Villa ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas/1/rooms`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Villa Rooms]
![image](https://github.com/user-attachments/assets/044e39f9-ec1b-4fe9-8def-5aad0ef9a70d)

#### 4. GET Villa Bookings by Villa ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas/1/bookings`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Villa Bookings]
![image](https://github.com/user-attachments/assets/6e3b86b7-b560-4caf-ab8d-4f4f42c8d92a)

#### 5. GET Villa Reviews by Villa ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas/1/reviews`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Villa Reviews]
![image](https://github.com/user-attachments/assets/5b9051cc-4c91-424d-9b40-a270390a87bf)

#### 6. GET Villa Availability
**Request:**
- Method: GET
- URL: `[http://localhost:8080/villas?ci_date=2025-07-10&co_date=2025-07-12]`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Villa Availability]
![image](https://github.com/user-attachments/assets/05d59fd4-23e9-4ddf-9fe2-125b86d7138b)

#### 7. POST Create Villa
**Request:**
- Method: POST
- URL: `http://localhost:8080/villas`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "name": "Villa Kumbang",
  "description": "Villa dengan pemandangan pegunungan dan kolam renang pribadi.",
  "address": "Jl. Batu Bulan No.123, Gianyar, Bali"
}
```

**Screenshot:**
![POST Villas]
![image](https://github.com/user-attachments/assets/0a16916a-43ae-4893-9a5a-5174cd24c61e)

#### 8. POST Create Room by Villa ID
**Request:**
- Method: POST
- URL: `http://localhost:8080/villas/2/rooms`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "name": "Suite Family",
  "quantity": 5,
  "capacity": 4,
  "price": 1200000,
  "bedSize": "King",
  "hasDesk": true,
  "hasAc": true,
  "hasTv": true,
  "hasWifi": true,
  "hasShower": true,
  "hasHotwater": true,
  "hasFridge": true
}
```

**Screenshot:**
![POST Rooms by Villa ID]
![image](https://github.com/user-attachments/assets/ad09e1de-7f37-4337-bdac-7a413805bee3)

#### 9. PUT Update Villa by ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/villas/6`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "id": 6,
  "name": "Villa Sakura",
  "description": "Villa dengan pemandangan pegunungan dan kolam renang pribadi.",
  "address": "Jl. Bunga No. 123, Bandung"
}
```

**Screenshot:**
![PUT Villas by ID]
![image](https://github.com/user-attachments/assets/a3045649-9041-419f-8222-e4321df829e9)

#### 10. PUT Update Room by Villa and Room ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/villas/1/rooms/3`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "id": 3,
  "villa": 1,
  "name": "Deluxe Suite",
  "quantity": 5,
  "capacity": 4,
  "price": 1500000,
  "bedSize": "King",
  "hasDesk": true,
  "hasAc": true,
  "hasTv": true,
  "hasWifi": true,
  "hasShower": true,
  "hasHotwater": true,
  "hasFridge": false
}
```

**Screenshot:**
![PUT Rooms by Villa and Room ID]
![image](https://github.com/user-attachments/assets/392ec4a6-c957-4cc9-a435-159d8d306817)

#### 11. DELETE Room by Villa and Room ID
**Request:**
- Method: DELETE
- URL: `http://localhost:8080/villas/1/rooms/3`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![DELETE Rooms by Villa and Room ID]
![image](https://github.com/user-attachments/assets/bdc6e758-c7f7-4950-b7dd-acebe16eb72a)

#### 12. DELETE Villa by ID
**Request:**
- Method: DELETE
- URL: `http://localhost:8080/villas/6`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![DELETE Villas by ID]
![image](https://github.com/user-attachments/assets/b7d18849-2c2b-4e2a-b58c-7ae5af59c3c7)

### Customer Endpoints

#### 13. GET All Customers
**Request:**
- Method: GET
- URL: `http://localhost:8080/customers`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET All Customers]
![image](https://github.com/user-attachments/assets/3ea2af33-5433-4fe5-a64b-a2e48cf514fb)

#### 14. GET Customer by ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/customers/1`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Customers by ID]
![image](https://github.com/user-attachments/assets/369e0f09-07b3-4b72-a130-4ffe01bf5424)

#### 15. GET Customer Bookings by Customer ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/customers/2/bookings`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Customer Bookings]
![image](https://github.com/user-attachments/assets/03136294-45e3-4983-9d81-6678a07a73fe)

#### 16. GET Customer Reviews by Customer ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/customers/2/reviews`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Customer Reviews]
![image](https://github.com/user-attachments/assets/8e2a78e9-01ad-4595-b292-97b314c31527)

#### 17. POST Create Customer
**Request:**
- Method: POST
- URL: `http://localhost:8080/customers`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
    "name": "Mei Mei",
    "email": "meimeicantik@example.com",
    "phone": "081234567890"
}
```

**Screenshot:**
![POST Customers]
![image](https://github.com/user-attachments/assets/d8acbc64-93a9-4388-93e1-91405e7a1878)

#### 18. POST Customer Booking
**Request:**
- Method: POST
- URL: `http://localhost:8080/customers/2/bookings`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
    "room_type": 2,
    "checkin_date": "2025-06-05 14:00:00",
    "checkout_date": "2025-06-07 12:00:00",
    "voucher": null
}
```

**Screenshot:**
![POST Customer Booking]
![image](https://github.com/user-attachments/assets/e15b3512-8743-49d8-9f9f-1a04bed8ed23)

#### 19. POST Customer Review
**Request:**
- Method: POST
- URL: `http://localhost:8080/customers/2/bookings/4/reviews`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "booking": 4,
  "star": 5,
  "title": "Sangat memuaskan!",
  "content": "Pelayanan sangat ramah, fasilitas lengkap, pemandangan indah. Akan datang lagi!"
}
```

**Screenshot:**
![POST Review Customers]
![image](https://github.com/user-attachments/assets/1abe9d89-cc46-4d61-b7fa-a6e862ef0c34)

#### 20. PUT Check-in by Booking ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/bookings/4/checkin`
- Headers: `X-API-Key: [rahasia123]`
- Body:
```json
{
  "hasCheckedIn": true
}
```

**Screenshot:**
![PUT Checkin by Bookings ID]
![image](https://github.com/user-attachments/assets/0fbb37fd-1171-4bc8-8d32-44bb90968be3)

#### 21. PUT Check-out by Booking ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/bookings/4/checkout`
- Headers: `X-API-Key: [rahasia123]`
- Body:
```json
{
  "hasCheckedOut": true
}
```

**Screenshot:**
![PUT Checkout by Bookings ID]
![image](https://github.com/user-attachments/assets/49577cb8-5565-47a2-9352-a2df3d9676e6)

#### 22. PUT Update Customer by ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/customers/2`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
    "id": 2,
    "name": "Cahya",
    "email": "test1@gmail.com",
    "phone": "6281234567890"
}
```

**Screenshot:**
![PUT Customers by ID]
![image](https://github.com/user-attachments/assets/96918ea1-3e83-4736-839b-6d3c91f236c2)

#### 23. DELETE Customer by ID
**Request:**
- Method: DELETE
- URL: `http://localhost:8080/customers/3`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![DELETE Customers by ID]
![image](https://github.com/user-attachments/assets/ec148239-8cee-4fa5-b241-f1596befbb93)

### Voucher Endpoints

#### 24. GET All Vouchers
**Request:**
- Method: GET
- URL: `http://localhost:8080/vouchers`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET All Vouchers]
![image](https://github.com/user-attachments/assets/08779f6b-5644-4b84-9652-7eee0d912dc9)

#### 25. GET Voucher by ID
**Request:**
- Method: GET
- URL: `http://localhost:8080/vouchers/2`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![GET Vouchers by ID]
![image](https://github.com/user-attachments/assets/a2ee7a6b-ac80-40a6-a986-aaa48ba3c44f)

#### 26. POST Create Voucher
**Request:**
- Method: POST
- URL: `http://localhost:8080/vouchers`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
    "code": "PROMO7.7",
    "description": "Promo khusus bulan 7 tanggal 7",
    "discount": 30,
    "start_date": "2025-07-07 00:00:00",
    "end_date": "2025-07-07 23:59:59"
}
```

**Screenshot:**
![POST Vouchers]
![image](https://github.com/user-attachments/assets/619b907d-09e0-4d6a-9d8d-f137328b6373)

#### 27. PUT Update Voucher by ID
**Request:**
- Method: PUT
- URL: `http://localhost:8080/vouchers/2`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
    "code": "PROMO7.7",
    "description": "Promo khusus bulan 7 tanggal 7",
    "discount": 15,
    "start_date": "2025-07-07 00:00:00",
    "end_date": "2025-07-07 23:59:59"
}
```

**Screenshot:**
![PUT Vouchers by ID]
![image](https://github.com/user-attachments/assets/3c8e8e12-5992-4e1a-b3ff-6a7a82a08a3c)

#### 28. DELETE Voucher by ID
**Request:**
- Method: DELETE
- URL: `http://localhost:8080/vouchers/1`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![DELETE Vouchers by ID]
![image](https://github.com/user-attachments/assets/3417b447-fa6a-4b95-9b92-ad7bbac7dd47)

## ❌ Error Handling Testing

### 1. Error 404 - Villa Not Found
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas/999`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![Error 404]
![image](https://github.com/user-attachments/assets/7829aba8-9439-418d-b909-a777841300a9)

### 2. Error 400 - Bad Request (Invalid Email)
**Request:**
- Method: POST
- URL: `http://localhost:8080/customers`
- Headers: `X-API-Key: [rahasia123]`, `Content-Type: application/json`
- Body:
```json
{
  "name": "Invalid User",
  "email": "invalid-email",
  "phone": "081234567890"
}
```

**Screenshot:**
![Error 400]
![image](https://github.com/user-attachments/assets/0f577257-52e6-488d-b14a-7dd94cc7efc5)

### 3. Error 401 - Unauthorized (Missing API Key)
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas`
- Headers: (No API Key)

**Screenshot:**
![Error 401]
![image](https://github.com/user-attachments/assets/10c1729a-3bc3-45a8-b96d-e5630ae30f72)

### 4. Error 500 - Internal Server Error (Simulasi Kesalahan Server)
**Request:**
- Method: GET
- URL: `http://localhost:8080/villas`
- Headers: `X-API-Key: [rahasia123]`

**Screenshot:**
![Error 500]
![image](![image](https://github.com/user-attachments/assets/a4a4ae09-ac7a-4082-9166-e8311799ac0f))

## 🗄️ Database Structure

API menggunakan SQLite database dengan entitas:
- **villaa**: Menyimpan informasi vila
- **room_types**: Menyimpan informasi kamar pada vila
- **customers**: Menyimpan informasi pelanggan
- **bookings**: Menyimpan informasi pemesanan
- **reviews**: Menyimpan ulasan pelanggan
- **vouchers**: Menyimpan informasi voucher diskon

## 🔧 Teknologi yang Digunakan

- **Java 11+**: Bahasa pemrograman utama
- **SQLite**: Database
- **JSON**: Format data exchange
- **HTTP Server**: Built-in Java HTTP server
- **Postman**: API testing tool

## 🚨 HTTP Status Codes

- `200 OK`: Request berhasil
- `201 Created`: Resource berhasil dibuat
- `400 Bad Request`: Data tidak valid
- `401 Unauthorized`: API key tidak valid
- `404 Not Found`: Resource tidak ditemukan
- `500 Internal Server Error`: Error server


**Mata Kuliah**: Pemrograman Berorientasi Objek  
**Dosen**: Wayan Oger Vihikan, S.T.I, M.I.T.  
**Semester**: 2  
**Tahun**: 2025
