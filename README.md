# Quarkus Membership REST API

![Project Overview](img/1.png)

Project ini adalah REST API untuk manajemen membership yang dibangun dengan Quarkus, framework Java yang supersonik dan subatomik. API ini menyediakan fitur registrasi, login, dan manajemen profil pengguna dengan autentikasi JWT.

## 🚀 Fitur Utama

- **Registrasi Pengguna**: Pendaftaran pengguna baru dengan validasi email
- **Autentikasi JWT**: Login dengan token JWT yang aman
- **Manajemen Profil**: Lihat dan update profil pengguna
- **Validasi Input**: Validasi data input dengan Jakarta Bean Validation
- **Dokumentasi API**: OpenAPI 3.0 dengan Swagger UI
- **Database PostgreSQL**: Persistensi data dengan Hibernate ORM Panache
- **Keamanan**: Konfigurasi keamanan berbasis peran dan path

## 📋 Prasyarat

- Java 17 atau versi lebih tinggi
- Apache Maven 3.8.1+
- PostgreSQL 12+
- Docker (opsional, untuk menjalankan database)

## 🛠️ Teknologi yang Digunakan

- **Quarkus 3.30.1**: Framework Java modern
- **Jakarta REST**: Implementasi REST API
- **Hibernate ORM dengan Panache**: ORM yang disederhanakan
- **PostgreSQL**: Database relasional
- **JWT (JSON Web Token)**: Autentikasi stateless
- **SmallRye OpenAPI**: Dokumentasi API
- **Lombok**: Pengurangan boilerplate code
- **Bean Validation**: Validasi data input

## 🏗️ Struktur Project

```
src/main/java/org/quarkus/rest/
├── QuarkusRestApplication.java     # Main application class
├── controller/
│   └── MembershipResource.java     # REST API endpoints
├── dto/
│   ├── ApiResponse.java             # Response wrapper
│   ├── LoginRequest.java            # Login DTO
│   ├── LoginResponse.java           # Login response DTO
│   ├── ProfileResponse.java         # Profile response DTO
│   ├── ProfileUpdateRequest.java   # Profile update DTO
│   └── RegistrationRequest.java     # Registration DTO
├── entity/
│   └── User.java                    # User entity
├── repository/
│   └── UserRepository.java          # Data access layer
└── service/
    └── TokenService.java            # JWT token generation
```

## 🚀 Menjalankan Aplikasi

### Mode Development

Jalankan aplikasi dalam mode development yang mendukung live coding:

```bash
./mvnw quarkus:dev
```

Aplikasi akan berjalan di `http://localhost:8080`

> **Catatan**: Quarkus menyediakan Dev UI yang dapat diakses di `http://localhost:8080/q/dev/`

### Menjalankan dengan Docker

1. Jalankan PostgreSQL dengan Docker:

```bash
mvn clean install -DskipTests
```

```bash
docker-compose up -d postgres
```

2. Jalankan aplikasi:

```bash
./mvnw quarkus:dev
```

## 📦 Packaging dan Deployment

### Packaging Aplikasi

```bash
./mvnw package
```

Ini akan menghasilkan file `quarkus-run.jar` di direktori `target/quarkus-app/`.

### Membuat Uber-JAR

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

### Native Executable

```bash
./mvnw package -Dnative
```

Atau jika tidak memiliki GraalVM:

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## 📚 API Documentation

### Swagger UI

Akses dokumentasi API interaktif di:
- **Swagger UI**: `http://localhost:8080/swagger-ui`
- **OpenAPI Spec**: `http://localhost:8080/openapi`

### API Endpoints

#### 1. Registrasi Pengguna

```http
POST /api/v1/registration
Content-Type: application/json

{
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "Password123!"
}
```

#### 2. Login

```http
POST /api/v1/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!"
}
```

#### 3. Get Profile (Membutuhkan JWT)

```http
GET /api/v1/profile
Authorization: Bearer <JWT_TOKEN>
```

#### 4. Update Profile (Membutuhkan JWT)

```http
PUT /api/v1/profile/update
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "firstName": "John Updated",
  "lastName": "Doe Updated"
}
```

## 🔧 Konfigurasi

### Database

Konfigurasi database ada di `src/main/resources/application.properties`:

```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkus_db
quarkus.datasource.username=quarkus_user
quarkus.datasource.password=quarkus_password
```

### JWT

Konfigurasi JWT:

```properties
smallrye.jwt.sign.key.location=/deployments/jwt-secret-key
mp.jwt.verify.issuer=https://yourdomain.com
smallrye.jwt.token.header=Authorization
smallrye.jwt.token.scheme=Bearer
```

### Keamanan

Konfigurasi keamanan path:

```properties
# Path yang membutuhkan autentikasi
quarkus.http.auth.permission.authenticated.paths=/api/v1/profile,/api/v1/profile/update
quarkus.http.auth.permission.authenticated.policy=authenticated

# Path publik
quarkus.http.auth.permission.public.paths=/api/v1/registration,/api/v1/login,/openapi,/swagger-ui
quarkus.http.auth.permission.public.policy=permit
```

## 🧪 Testing

### HTTP Request File

Project menyertakan file `test.http` untuk testing API dengan VS Code REST Client extension.

### Menjalankan Tests

```bash
./mvnw test
```

## 🔐 Keamanan

- **Password**: Saat ini disimpan sebagai plain text (untuk development)
- **JWT Token**: Berlaku selama 12 jam
- **Input Validation**: Menggunakan Jakarta Bean Validation
- **Path Security**: Konfigurasi keamanan berbasis path

## 🚨 Catatan Penting

1. **Password Hashing**: Untuk production, implementasikan password hashing dengan BCrypt atau Argon2
2. **Environment Variables**: Gunakan environment variables untuk konfigurasi sensitif
3. **HTTPS**: Aktifkan HTTPS untuk production
4. **Rate Limiting**: Pertimbangkan untuk menambahkan rate limiting pada endpoint login
5. **CORS**: Konfigurasi CORS jika frontend dan backend terpisah

## 📈 Monitoring

Quarkus menyediakan berbagai endpoint untuk monitoring:

- Health Check: `http://localhost:8080/q/health`
- Metrics: `http://localhost:8080/q/metrics`
- Info: `http://localhost:8080/q/info`


Project ini dilisensikan under MIT License - lihat file [LICENSE](LICENSE) untuk detailnya.

## 🔗 Link Penting

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Quarkus Getting Started](https://quarkus.io/guides/getting-started)
- [Jakarta REST](https://jakarta.ee/specifications/restful-ws/)
- [JWT Specification](https://tools.ietf.org/html/rfc7519)
- [OpenAPI Specification](https://swagger.io/specification/)
