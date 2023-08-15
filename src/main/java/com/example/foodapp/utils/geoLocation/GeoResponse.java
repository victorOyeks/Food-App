package com.example.foodapp.utils.geoLocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeoResponse {
    @JsonProperty("results")
    private Result[] result;
}
