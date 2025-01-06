FROM eclipse-temurin:17
RUN mkdir /opt/app

COPY target/premium-data-provider-*.jar /opt/app/
RUN rm -f /opt/app/*-stubs.jar

ENV DATA_PROVIDER_PORT=8083

ENTRYPOINT ["sh", "-c", "java -jar /opt/app/premium-data-provider-*.jar --server.port=${DATA_PROVIDER_PORT}"]