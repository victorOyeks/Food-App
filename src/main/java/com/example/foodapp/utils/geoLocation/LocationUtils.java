package com.example.foodapp.utils.geoLocation;

import com.example.foodapp.dto.request.VendorRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class LocationUtils {
    private static final String API_KEY = "AIzaSyA22GBhIK3LwSHcYDlB8UYJ4x1IoGeuqvM";
    public static GeoResponse getGeoDetails(@RequestParam VendorRegistrationRequest vendor) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/geocode/json")
                .queryParam("key", API_KEY)
                .queryParam("address", vendor.getBusinessAddress())
                .build();
        ResponseEntity<GeoResponse> response = new RestTemplate().getForEntity(uri.toUriString(), GeoResponse.class);

        return response.getBody();
    }
    public static GeoResponse getGeoDetails(@RequestParam String businessAddress) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/geocode/json")
                .queryParam("key", API_KEY)
                .queryParam("address", businessAddress)
                .build();
        ResponseEntity<GeoResponse> response = new RestTemplate().getForEntity(uri.toUriString(), GeoResponse.class);

        return response.getBody();
    }

    public static String extractActualLocation(GeoResponse geoDetails) {
        if (geoDetails != null && geoDetails.getResult() != null && geoDetails.getResult().length > 0) {
            Result firstResult = geoDetails.getResult()[0];
            if (firstResult.getAddress() != null) {
                return firstResult.getAddress();
            }
        }
        return "Unknown Location";
    }

    public static GeoLocation extractGeoLocation(GeoResponse geoDetails) {
        if (geoDetails != null && geoDetails.getResult() != null && geoDetails.getResult().length > 0) {
            Result firstResult = geoDetails.getResult()[0];
            if (firstResult.getGeometry() != null && firstResult.getGeometry().getLocation() != null) {
                return firstResult.getGeometry().getLocation();
            }
        }
        return null;
    }
}
