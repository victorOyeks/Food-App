package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DetailsResponse {
    private String id;
    private String businessName;
    private String address;
    private String contactNumber;
//    private List<MenuResponse> menus;
}
