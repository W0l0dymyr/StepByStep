FROM openjdk:17-jdk AS build
COPY . .
#RUN mvn clean package

#
# Package stage
#
FROM openjdk:17-jdk
COPY --from=build /target/StepByStep-0.0.1-SNAPSHOT.jar cities.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","cities.jar"]