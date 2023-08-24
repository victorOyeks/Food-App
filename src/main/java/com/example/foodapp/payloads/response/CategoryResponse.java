package com.example.foodapp.payloads.response;

import com.example.foodapp.entities.ItemMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryResponse {
    private String categoryId;
    private String categoryName;
    private List<ItemMenu> itemMenus = new ArrayList<>();
}
