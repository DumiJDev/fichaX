FROM maven:3.8.5

WORKDIR /app

COPY . /app

RUN mvn install package

CMD ["java", "-jar", "/app/app.jar"]