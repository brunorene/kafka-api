FROM openjdk:alpine

COPY application*.yml /
COPY build/libs/kafka-api-*.jar /kafka-api.jar

#CMD ["java", "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:ParallelGCThreads=6", "-XX:ConcGCThreads=4", "-XX:InitiatingHeapOccupancyPercent=75", "-Xmx3000M", "-Xms3000M", "-jar", "/kafka-api.jar"]
CMD ["java", "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector", "-XX:+UseConcMarkSweepGC", "-XX:+UseCompressedOops", "-Xmx3000M", "-Xms3000M", "-jar", "/kafka-api.jar"]