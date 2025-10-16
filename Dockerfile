FROM openjdk:17-jdk-alpine

# Install timezone and netcat
RUN apk add --no-cache tzdata netcat-openbsd

# Set timezone
RUN cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime && echo "Asia/Jakarta" > /etc/timezone

# Set working directory
WORKDIR /usr/local/mail-service-oob

# Copy JAR file
COPY target/mail-service-oob.jar .

# Jalankan JAR saat container start
CMD ["java", "-jar", "mail-service-oob.jar"]
