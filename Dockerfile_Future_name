FROM maven:3.8.5

WORKDIR /app

COPY . /app

RUN mvn install package

EXPOSE 8080

CMD ["java", "-jar", "/app/target/app.jar"]