package com.fadhlika.lokasi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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

import com.fadhlika.lokasi.dto.Feature;
import com.fadhlika.lokasi.dto.FeatureCollection;
import com.fadhlika.lokasi.model.Location;
import com.fadhlika.lokasi.repository.LocationRepository;
import com.fadhlika.lokasi.repository.PhotonRepository;

@ExtendWith(MockitoExtension.class)
public class ReverseGeocodeServiceTest {

    @InjectMocks
    private ReverseGeocodeService reverseGeocodeService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PhotonRepository photonRepository;

    @Test
    public void testReverseGeocodeLocation() throws IOException, InterruptedException {
        when(locationRepository.findLocation(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(false)))
                .thenReturn(Optional.of(new Location(-6.1754, 106.8272) {
                    {
                        setId(1);
                        setUserId(1);
                        setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
                    }
                }))
                .thenReturn(Optional.empty());

        when(photonRepository.reverseGeocode(-6.1754, 106., 3)).thenReturn(new FeatureCollection(new ArrayList<>() {
            {
                add(new Feature());
            }
        }));

        doNothing().when(locationRepository).updateLocationGeocode(eq(1),
                any(FeatureCollection.class));

        reverseGeocodeService.processReverseGeocode();

        verify(photonRepository).reverseGeocode(-6.1754, 106.8272, 3);

        verify(locationRepository, times(2)).findLocation(Optional.empty(),
                Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(false));

    }
}
