# Gunakan image resmi OpenJDK
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy file proyek ke dalam container
COPY . .

# Build aplikasi menggunakan Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Buka port aplikasi
EXPOSE 8080

# Jalankan aplikasi
CMD ["java", "-jar", "target/lastbite-0.0.1-SNAPSHOT.jar"]
