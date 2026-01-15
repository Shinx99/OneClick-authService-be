# Development Dockerfile với hot reload
FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app

# Copy pom.xml và cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline
#COPY . .

# Expose app port và debug port
EXPOSE 8080 5005

# Chạy với Maven spring-boot:run để có hot reload
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]


CMD ["mvn", "spring-boot:run", \
     "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
     "-Dspring.devtools.restart.enabled=true", \
     "-Dspring.devtools.livereload.enabled=true"]