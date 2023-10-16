package com.example.foodapp.controller;

import com.example.foodapp.payloads.request.CategoryRequest;
import com.example.foodapp.payloads.request.SupplementRequest;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.service.ItemService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("category/add-item-menu")
    public ResponseEntity<ApiResponse<ItemMenuResponse>> addFoodMenu(@RequestParam String itemName,
                                                                     @RequestParam BigDecimal itemPrice,
                                                                     @RequestParam (required = false) MultipartFile file,
                                                                     @RequestParam  String categoryId) throws IOException {
        ApiResponse<ItemMenuResponse> apiResponse = new ApiResponse<>(itemService.addItemMenu(itemName, itemPrice, categoryId, file));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("add-supplements")
        public ResponseEntity<ApiResponse<SupplementResponse>> addSupplement(@RequestBody SupplementRequest supplementRequest){
            ApiResponse<SupplementResponse> apiResponse = new ApiResponse<>(itemService.addSupplement(supplementRequest));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }

    @PutMapping("/edit-item-menu")
    public ResponseEntity<ApiResponse<ItemMenuResponse>> editFoodMenu(@RequestParam String itemId,
                                                                      @RequestParam String itemName,
                                                                      @RequestParam BigDecimal itemPrice,
                                                                      @RequestParam String categoryId,
                                                                      @RequestParam Boolean availableStatus,
                                                                      @RequestParam(required = false) MultipartFile file) throws IOException {
        ApiResponse<ItemMenuResponse> apiResponse = new ApiResponse<>(itemService.editItemMenu(itemId, itemName, itemPrice, categoryId, availableStatus, file));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("category/{categoryId}/item-menu/{itemId}")
    public ResponseEntity<ApiResponse<String>> deleteItemMenu(@PathVariable String categoryId, @PathVariable String itemId) {
        itemService.deleteItemMenu(itemId, categoryId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Item deleted successfully");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/edit-category")
    public ResponseEntity<ApiResponse<CategoryResponse>> editItemCategory(@RequestParam String categoryId, @RequestBody CategoryRequest request) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(itemService.editItemCategory(categoryId, request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<CategoryResponse>> getItemCategory(@RequestParam String categoryId) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(itemService.getItemCategory(categoryId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("item-category")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllItemsCategory() {
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>(itemService.getAllItemCategory());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("item-list")
    public ResponseEntity<ApiResponse<List<ItemDetailsResponse>>> getAllItemsForVendors() {
        ApiResponse<List<ItemDetailsResponse>> apiResponse = new ApiResponse<>(itemService.getAllVendorItems());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("supplements")
    public ResponseEntity<ApiResponse<List<SupplementResponse>>> getAllSupplements() {
        ApiResponse<List<SupplementResponse>> apiResponse = new ApiResponse<>(itemService.getAllSupplements());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("item-category/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteItemCategory(@PathVariable String categoryId) {
        itemService.deleteItemCategory(categoryId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Food category deleted successfully");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
