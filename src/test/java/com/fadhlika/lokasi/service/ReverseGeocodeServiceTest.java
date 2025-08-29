package com.fadhlika.lokasi.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.dto.GeocodeProperties;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.model.Place;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;
import com.fadhlika.lokasi.repository.PlaceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ReverseGeocodeServiceTest {

    @InjectMocks
    private ReverseGeocodeService reverseGeocodeService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PhotonRepository photonRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private ObjectMapper mapper;

    @Test
    public void testReverseGeocodeLocation() throws IOException, InterruptedException {
        Location l = new Location(-6.1754, 106.8272) {
            {
                setId(1);
                setUserId(1);
                setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
            }
        };

        when(locationRepository.findLocation(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(false)))
                .thenReturn(Optional.of(l))
                .thenReturn(Optional.empty());

        when(photonRepository.reverseGeocode(-6.1754, 106.8272, 3)).thenReturn(new FeatureCollection(new ArrayList<>() {
            {
                add(new Feature(l.getGeometry(), new GeocodeProperties(
                        "W",
                        1159084257,
                        "historic",
                        "monument",
                        "house",
                        "10110",
                        "ID",
                        "Monumen Nasional",
                        "Indonesia",
                        "Jakarta",
                        "Gambir",
                        "RW 02",
                        "Jalan Medan Merdeka Utara",
                        "Jawa",
                        new ArrayList<>() {
                            {
                                add(106.8267455);
                                add(-6.1749898);
                                add(106.8275888);
                                add(-6.1758248);
                            }
                        })));
            }
        }));

        doNothing().when(locationRepository).updateLocationGeocode(eq(1),
                any(FeatureCollection.class));

        when(placeRepository.fetchPlace(l.getGeometry())).thenReturn(Optional.empty());

        doNothing().when(placeRepository).createPlace(any(Place.class));

        reverseGeocodeService.processReverseGeocode();

        verify(photonRepository).reverseGeocode(-6.1754, 106.8272, 3);

        verify(locationRepository, times(2)).findLocation(Optional.empty(),
                Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(false));

        verify(placeRepository, times(1)).fetchPlace(l.getGeometry());

        verify(placeRepository, times(1)).createPlace(any(Place.class));

    }
}
