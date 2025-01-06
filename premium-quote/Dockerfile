FROM eclipse-temurin:17
RUN mkdir /opt/app

COPY target/premium-quote-*.jar /opt/app/
RUN rm -f /opt/app/*-stubs.jar

ENV QUOTE_SERVICE_PORT=8082
ENV DATA_PROVIDER_HOST=localhost
ENV DATA_PROVIDER_PORT=8083

ENV DS_URL=jdbc:h2:file:./database;AUTO_SERVER=TRUE;AUTO_RECONNECT=TRUE
ENV DS_USER=sa
ENV DS_PW=
ENV DS_DRIVER=org.h2.Driver

ENTRYPOINT [ \
  "sh", "-c", "java -jar /opt/app/premium-quote-*.jar \
  --server.port=$QUOTE_SERVICE_PORT \
  --dataprovider.host=$DATA_PROVIDER_HOST \
  --dataprovider.port=$DATA_PROVIDER_PORT \
  --spring.datasource.url=$DS_URL \
  --spring.datasource.username=$DS_USER \
  --spring.datasource.password=$DS_PW \
  --spring.datasource.driverClassName=$DS_DRIVER" \
]