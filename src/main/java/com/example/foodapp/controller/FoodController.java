package com.example.foodapp.controller;

import com.example.foodapp.dto.request.CategoryRequest;
import com.example.foodapp.dto.response.ApiResponse;
import com.example.foodapp.dto.response.CategoryResponse;
import com.example.foodapp.dto.response.ItemMenuResponse;
import com.example.foodapp.entities.ItemMenu;
import com.example.foodapp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/items/")
@RequiredArgsConstructor
public class FoodController {

    private final ItemService itemService;

    @PostMapping("add-category")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@RequestBody CategoryRequest request) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(itemService.addItemCategory(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("category/{categoryId}/add-item-menu")
    public ResponseEntity<ApiResponse<ItemMenuResponse>> addFoodMenu(@RequestParam String itemName,
                                                                     @RequestParam BigDecimal itemPrice,
                                                                     @RequestParam MultipartFile file,
                                                                     @PathVariable  String categoryId) throws IOException {
        ApiResponse<ItemMenuResponse> apiResponse = new ApiResponse<>(itemService.addItemMenu(itemName, itemPrice, categoryId, file));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/category/{categoryId}/edit-item-menu/{itemId}")
    public ResponseEntity<ApiResponse<ItemMenuResponse>> editFoodMenu(@RequestParam String itemId,
                                                                      @RequestParam String itemName,
                                                                      @RequestParam BigDecimal itemPrice,
                                                                      @RequestParam Boolean breakfast,
                                                                      @RequestParam Boolean lunch,
                                                                      @RequestParam Boolean dinner,
                                                                      @RequestParam String categoryId,
                                                                      @RequestParam(required = false) MultipartFile file) throws IOException {
        ApiResponse<ItemMenuResponse> apiResponse = new ApiResponse<>(itemService.editItemMenu(itemId, itemName, itemPrice, breakfast, lunch, dinner, categoryId, file));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("category/{categoryId}/item-menu/{itemId}")
    public ResponseEntity<ApiResponse<String>> deleteItemMenu(@PathVariable String categoryId, @PathVariable String itemId) {
        itemService.deleteItemMenu(itemId, categoryId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Item deleted successfully");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/item-category/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> editItemCategory(@PathVariable String categoryId, @RequestBody CategoryRequest request) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(itemService.editItemCategory(categoryId, request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getItemCategory(@PathVariable String categoryId) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(itemService.getItemCategory(categoryId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("item-category")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllItemsCategory() {
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>(itemService.getAllItemCategory());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("item-category/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteItemCategory(@PathVariable String categoryId) {
        itemService.deleteItemCategory(categoryId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Food category deleted successfully");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
