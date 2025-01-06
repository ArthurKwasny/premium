FROM eclipse-temurin:17
RUN mkdir /opt/app

COPY target/premium-restproxy-*.jar /opt/app/
RUN rm -f /opt/app/*-stubs.jar

ENV PROXY_SERVICE_PORT=8081
ENV QUOTE_SERVICE_HOST=localhost
ENV QUOTE_SERVICE_PORT=8082
ENV DATA_PROVIDER_HOST=localhost
ENV DATA_PROVIDER_PORT=8083

ENTRYPOINT [ \
  "sh", "-c", "java -jar /opt/app/premium-restproxy-*.jar \
  --server.port=$PROXY_SERVICE_PORT \
  --quoteservice.host=$QUOTE_SERVICE_HOST \
  --quoteservice.port=$QUOTE_SERVICE_PORT \
  --dataprovider.host=$DATA_PROVIDER_HOST \
  --dataprovider.port=$DATA_PROVIDER_PORT" \
]