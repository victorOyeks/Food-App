package com.example.foodapp.service;

import com.example.foodapp.payloads.request.CategoryRequest;
import com.example.foodapp.payloads.request.SupplementRequest;
import com.example.foodapp.payloads.response.CategoryResponse;
import com.example.foodapp.payloads.response.ItemDetailsResponse;
import com.example.foodapp.payloads.response.ItemMenuResponse;
import com.example.foodapp.entities.ItemMenu;
import com.example.foodapp.payloads.response.SupplementResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ItemService {
    CategoryResponse addItemCategory(CategoryRequest request);
    ItemMenuResponse addItemMenu(String itemName, BigDecimal itemPrice, String categoryId, MultipartFile file) throws IOException;
    SupplementResponse addSupplement(SupplementRequest supplementRequest);
    ItemMenuResponse editItemMenu(String itemId, String itemName, BigDecimal itemPrice, String categoryId, Boolean availableStatus, MultipartFile file) throws IOException;
    CategoryResponse editItemCategory(String categoryId, CategoryRequest request);
    CategoryResponse getItemCategory(String categoryId);
    void deleteItemMenu(String itemId, String categoryId);
    List<CategoryResponse> getAllItemCategory();
    List<ItemDetailsResponse> getAllVendorItems();
    List<SupplementResponse> getAllSupplements();
    void deleteItemCategory(String categoryId);
    ItemMenu getFile(String id);
    }
