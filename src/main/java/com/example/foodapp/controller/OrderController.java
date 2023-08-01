package com.example.foodapp.controller;

import com.example.foodapp.dto.request.BulkItemOrderRequest;
import com.example.foodapp.dto.request.ItemOrderRequest;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.service.CompanyService;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("item-menus")
    public ResponseEntity<ApiResponse<List<FoodDataResponse>>> viewAllItemMenus() {
        List<FoodDataResponse> itemMenus = orderService.viewAllItemMenus();
        ApiResponse<List<FoodDataResponse>> apiResponse = new ApiResponse<>(itemMenus);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/select-item")
    public ResponseEntity<ApiResponse<String>> selectItems(@RequestBody ItemOrderRequest orderRequest, @RequestParam String vendorId) {
        ApiResponse<String> apiResponse = new ApiResponse<>(orderService.selectItem(vendorId, orderRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/make-bulk-order")
    public ResponseEntity<ApiResponse<String>> makeBulkOrderForStaff(@RequestParam String vendorId, @RequestBody List<BulkItemOrderRequest> staffOrders) {
        String response = orderService.createBulkOrder(vendorId, staffOrders);
        ApiResponse<String> apiResponse = new ApiResponse<>(response);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<OrderViewResponse>> viewAllOrders() {
        OrderViewResponse orders;
        try {
            orders = orderService.viewAllOrdersByUser();
        } catch (CustomException userException) {
            orders = orderService.viewAllOrdersByCompany();
        }
        ApiResponse<OrderViewResponse> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("{orderId}/itemMenu")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable("orderId") String orderId, @RequestParam String itemId) {
        String message = orderService.deleteItem(orderId, itemId);
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable("orderId") String orderId) {
        String message = orderService.deleteOrder(orderId);
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
