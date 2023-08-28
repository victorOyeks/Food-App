package com.example.foodapp.controller;

import com.example.foodapp.payloads.response.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    /*
    @PostMapping("/vendors/{vendorId}/select-item-individual")
    public ResponseEntity<ApiResponse<String>> selectItemsForIndividuals(@PathVariable String vendorId, @RequestParam String menuId) {
        ApiResponse<String> apiResponse = new ApiResponse<>(orderService.selectItemForIndividual(vendorId, menuId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }*/

    @PostMapping("/vendors/{vendorId}/select-item-individual")
    public ResponseEntity<ApiResponse<String>> selectItemsForIndividuals(@PathVariable String vendorId,
                                                                         @RequestParam String menuId,
                                                                         @RequestParam(required = false) List<String>  supplementIds) {

        ApiResponse<String> apiResponse;
        //user selects supplements
        if (supplementIds != null) {
            apiResponse = new ApiResponse<>(orderService.selectItemWithSupplementForIndividual(vendorId, menuId, supplementIds));
        } else {
            // User didn't select a supplement
            apiResponse = new ApiResponse<>(orderService.selectItemForIndividual(vendorId, menuId));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/vendors/{vendorId}/select-item-company")
    public ResponseEntity<ApiResponse<String>> selectItemsForCompanies(@PathVariable String vendorId, @RequestParam String menuId) {
        ApiResponse<String> apiResponse = new ApiResponse<>(orderService.selectItemForCompany(vendorId, menuId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("carts")
    public ResponseEntity<ApiResponse<OrderViewResponse>> viewAllOrders() {
        OrderViewResponse orders;
        try {
            orders = orderService.viewUserCart();
        } catch (CustomException userException) {
            orders = orderService.viewCompanyCart();
        }
        ApiResponse<OrderViewResponse> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<UserOrderViewResponse>> viewSimplifiedOrdersByUser() {
        ApiResponse<UserOrderViewResponse> orderViewResponse = new ApiResponse<>(orderService.viewSimplifiedOrdersByUser());
        return new ResponseEntity<>(orderViewResponse, HttpStatus.OK);
    }

    @DeleteMapping("{orderId}/itemMenu")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable("orderId") String orderId, @RequestParam String itemId) {
        String message = orderService.deleteItem(orderId, itemId);
        ApiResponse<String> apiResponse = new ApiResponse<>(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    @GetMapping("/{orderId}")
//    public ResponseEntity<UserOrderDetailsResponse> viewOrderDetails(@PathVariable String orderId) {
//        UserOrderDetailsResponse orderDetailsResponse = orderService.viewOrderById(orderId);
//        return new ResponseEntity<>(orderDetailsResponse, HttpStatus.OK);
//    }


    @GetMapping("order-details")
    public ResponseEntity<UserOrderDetailsResponse> viewOrderDetails(@RequestParam String orderId) {
        UserOrderDetailsResponse orderDetailsResponse;
        try {
            // Attempt to view the order as a user
            orderDetailsResponse = orderService.viewOrderByOrderIdForUser(orderId);
        } catch (CustomException userException) {
            try {
                // If not found for the user, attempt to view as a company
                orderDetailsResponse = orderService.viewOrderByOrderIdForCompany(orderId);
            } catch (CustomException companyException) {
                // If not found for both, return a not found response
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