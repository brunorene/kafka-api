FROM openjdk:alpine

COPY application*.yml /
COPY build/libs/kafka-api-*.jar /kafka-api.jar

CMD ["java", "-jar", "/kafka-api.jar", "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "-Xmx1500M", "-Xms1500M"]