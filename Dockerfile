FROM fedora:41
WORKDIR /app
COPY target/oda-news-service /app

CMD ["./oda-news-service"]
