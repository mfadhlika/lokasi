# The deployment Image
FROM scratch

WORKDIR /app

EXPOSE 8080

COPY target /build/target/lokasi lokasi

ENTRYPOINT ["/app/lokasi"]
