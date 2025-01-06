FROM eclipse-temurin:17
RUN mkdir /opt/app

COPY target/premium-webclient-*.jar /opt/app/
RUN rm -f /opt/app/*-stubs.jar

ENV WEB_CLIENT_PORT=8080
ENV PROXY_SERVICE_HOST=localhost
ENV PROXY_SERVICE_PORT=8081

ENTRYPOINT [ \
  "sh", "-c", "java -jar /opt/app/premium-webclient-*.jar \
  --server.port=$WEB_CLIENT_PORT \
  --proxyservice.host=$PROXY_SERVICE_HOST \
  --proxyservice.port=$PROXY_SERVICE_PORT" \
]