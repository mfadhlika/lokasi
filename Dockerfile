FROM scratch

WORKDIR /app

COPY target/lokasi lokasi

ENTRYPOINT ["/app/lokasi"]
