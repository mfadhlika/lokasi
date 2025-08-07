package com.fadhlika.lokasi.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class PhotonRepository {

    private static final Logger logger = LoggerFactory.getLogger(PhotonRepository.class);

    @Value("${photon.base_url}")
    private String baseUrl;

    private final HttpClient client;

    private final ObjectMapper mapper;

    public PhotonRepository() throws URISyntaxException {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
    }

    public FeatureCollection reverseGeocode(double lat, double lon) throws IOException, InterruptedException {
        URI uri = URI
                .create(String.format("%s/reverse?lat=%s&lon=%s", baseUrl, Double.toString(lat), Double.toString(lon)));
        HttpRequest req = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<InputStream> res = client.send(req, BodyHandlers.ofInputStream());

        if (res.statusCode() != HttpStatus.OK.value()) {
            String message = new String(res.body().readAllBytes(), StandardCharsets.UTF_8);
            throw new InternalErrorException(message);
        }

        return mapper.readValue(res.body(), FeatureCollection.class);
    }
}
