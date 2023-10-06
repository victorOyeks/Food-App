package com.example.foodapp.controller;

import com.example.foodapp.payloads.request.CartRequest;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("item-menus")
    public ResponseEntity<ApiResponse<List<FoodDataResponse>>> viewFoodItemsByVendorAndCategory(@RequestParam String vendorId,
                                                                                                @RequestParam String categoryId) {
        List<FoodDataResponse> itemMenus = orderService.viewFoodItemsByVendorAndCategory(vendorId, categoryId);
        ApiResponse<List<FoodDataResponse>> apiResponse = new ApiResponse<>(itemMenus);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("supplements")
    public ResponseEntity<ApiResponse<List<SupplementResponse>>> viewAllSupplements(@RequestParam String vendorId) {
        List<SupplementResponse> supplementResponses = orderService.viewAllSupplements(vendorId);
        ApiResponse<List<SupplementResponse>> apiResponse = new ApiResponse<>(supplementResponses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ApiResponse<OrderViewResponse>> addToCart(@RequestParam String vendorId, @RequestBody CartRequest cartRequest) {
        OrderViewResponse message = orderService.addToCart(vendorId, cartRequest.getCartItems(), cartRequest.getSupplementItems());
        ApiResponse<OrderViewResponse> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/submit-cart")
    public ResponseEntity<ApiResponse<String>> submitCart(@RequestParam String orderId) {
        String message = orderService.submitCart(orderId);
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("carts")
    public ResponseEntity<ApiResponse<OrderViewResponse>> viewAllOrders() {
        OrderViewResponse orders = orderService.viewUserCart();
        ApiResponse<OrderViewResponse> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<UserOrderViewResponse>> viewSimplifiedOrdersByUser() {

        UserOrderViewResponse orderViewResponse;
        try{
            orderViewResponse = orderService.viewSimplifiedOrdersByUser();
        } catch (CustomException customException){
            orderViewResponse = orderService.viewSimplifiedOrdersByCompany();
        }
        ApiResponse<UserOrderViewResponse> apiResponse = new ApiResponse<>(orderViewResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("{orderId}/itemMenu")
    public ResponseEntity<ApiResponse<OrderViewResponse>> deleteItem(@PathVariable("orderId") String orderId, @RequestParam String itemId) {
        OrderViewResponse message;
        try {
            message = orderService.deleteItem(orderId, itemId);
        }catch (CustomException customException){
            message = orderService.deleteSupplement(orderId, itemId);
        }
        ApiResponse<OrderViewResponse> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("order-details")
    public ResponseEntity<UserOrderDetailsResponse> viewOrderDetails(@RequestParam String orderId) {
        UserOrderDetailsResponse orderDetailsResponse;
        try {
            orderDetailsResponse = orderService.viewOrderByOrderIdForUser(orderId);
        } catch (CustomException userException) {
            try {
                orderDetailsResponse = orderService.viewOrderByOrderIdForCompany(orderId);
            } catch (CustomException companyException) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(orderDetailsResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable("orderId") String orderId) {
        String message = orderService.deleteOrder(orderId);
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}