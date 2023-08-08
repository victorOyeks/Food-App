package com.example.foodapp.service;

import com.example.foodapp.dto.request.CategoryRequest;
import com.example.foodapp.dto.response.CategoryResponse;
import com.example.foodapp.dto.response.ItemMenuResponse;
import com.example.foodapp.entities.ItemMenu;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ItemService {
    CategoryResponse addItemCategory(CategoryRequest request);
    ItemMenuResponse addItemMenu(String itemName, BigDecimal itemPrice, String categoryId, MultipartFile file) throws IOException;
    ItemMenuResponse editItemMenu(String itemId, String itemName, BigDecimal itemPrice, Boolean breakfast, Boolean lunch, Boolean dinner, String categoryId, MultipartFile file) throws IOException;
    CategoryResponse editItemCategory(String categoryId, CategoryRequest request);
    CategoryResponse getItemCategory(String categoryId);
    void deleteItemMenu(String itemId, String categoryId);
    List<CategoryResponse> getAllItemCategory();
    void deleteItemCategory(String categoryId);
    ItemMenu getFile(String id);
    }
