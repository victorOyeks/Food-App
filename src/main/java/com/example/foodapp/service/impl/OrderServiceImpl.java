package com.example.foodapp.service.impl;

import com.example.foodapp.dto.request.BulkItemOrderRequest;
import com.example.foodapp.dto.request.ItemOrderRequest;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.entities.Order;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.OrderRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public List<FoodDataResponse> viewAllItemMenus() {
        List<FoodDataResponse> foodDataResponse = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();

        for (Vendor vendor : vendors) {
            for (ItemCategory itemCategory : vendor.getItemCategory()) {
                for (ItemMenu itemMenu : itemCategory.getItemMenus()) {
                    foodDataResponse.add(FoodDataResponse.builder()
                            .itemId(itemMenu.getItemId())
                            .itemName(itemMenu.getItemName())
                            .price(itemMenu.getItemPrice())
                            .imageUri(itemMenu.getImageUrl())
                            .vendorName(vendor.getBusinessName())
                            .build());
                }
            }
        }
        return foodDataResponse;
    }

    public String selectItem(String vendorId, ItemOrderRequest orderRequest) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();
        List<ItemMenu> selectedItemMenus = new ArrayList<>();

        for (String itemMenuId : orderRequest.getItemMenuId()) {
            ItemMenu itemMenu = findFoodMenuById(vendor.getId(), itemMenuId);
            if (itemMenu != null) {
                selectedItemMenus.add(itemMenu);
            }
        }

        Order order = new Order();
        order.setUser(user);

        order.setItemMenu(selectedItemMenus);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemMenu itemMenu : selectedItemMenus) {
            totalAmount = totalAmount.add(itemMenu.getItemPrice());
        }
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);

        if (user.getOrderList() == null) {
            user.setOrderList(new ArrayList<>());
        }
        user.getOrderList().add(order);
        userRepository.save(user);

        return "Food selected successfully!!!";
    }

    public String createBulkOrder(String vendorId, List<BulkItemOrderRequest> bulkOrders) {
        Company company = getAuthenticatedCompany();
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));

        for (BulkItemOrderRequest bulkOrder : bulkOrders) {
            String userEmail = bulkOrder.getUserEmail();
            User user = userRepository.findByEmail(userEmail);

            if (user == null) {
                throw new CustomException("User with email " + userEmail + " not found.");
            }

            List<ItemMenu> selectedItemMenus = new ArrayList<>();

            for (String itemMenuId : bulkOrder.getItemMenuIds()) {
                ItemMenu itemMenu = findFoodMenuById(vendor.getId(), itemMenuId);
                if (itemMenu != null) {
                    selectedItemMenus.add(itemMenu);
                }
            }

            Order order = new Order();
            order.setCompany(company);

            order.setItemMenu(selectedItemMenus);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (ItemMenu itemMenu : selectedItemMenus) {
                totalAmount = totalAmount.add(itemMenu.getItemPrice());
            }
            order.setTotalAmount(totalAmount);

            orderRepository.save(order);

            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }
            user.getOrderList().add(order);
            userRepository.save(user);
        }

        return "Bulk order created successfully.";
    }


    private ItemMenu findFoodMenuById(String vendorId, String foodMenuId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor nor found!!!"));
        for (ItemCategory itemCategory : vendor.getItemCategory()) {
            for (ItemMenu itemMenu : itemCategory.getItemMenus()) {
                if (itemMenu.getItemId().equals(foodMenuId)) {
                    return itemMenu;
                }
            }
        }
        return null;
    }

    public OrderViewResponse viewAllOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        return viewAllOrdersInternal(orderRepository.findOrdersByUserId(userId));
    }

    public OrderViewResponse viewAllOrdersByCompany() {
        String companyId = getAuthenticatedCompany().getId();
        return viewAllOrdersInternal(orderRepository.findOrdersByCompanyId(companyId));
    }

    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalFoodItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            for (ItemMenu itemMenu : order.getItemMenu()) {
                Vendor vendor = itemMenu.getItemCategory().getVendor();
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemMenu.getItemId())
//                        .recipient(order.getUser().getFirstName())
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .vendorName(vendor.getBusinessName())
                        .build());
                totalFoodItems++;
            }
            orderResponses.add(OrderResponse.builder()
                    .orderId(order.getOrderId())
                    .items(foodDataResponses)
                    .totalAmount(order.getTotalAmount())
                    .build());

            totalSum = totalSum.add(order.getTotalAmount());
        }

        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(totalFoodItems)
                .totalSum(totalSum)
                .build();

        return new OrderViewResponse(orderResponses, orderSummary);
    }

    public String deleteItem(String orderId, String foodItemId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("Order not found!!!"));
        List<ItemMenu> foodItems = order.getItemMenu();
        for (ItemMenu itemMenu : foodItems) {
            if (itemMenu.getItemId().equals(foodItemId)) {
                foodItems.remove(itemMenu);
                order.setItemMenu(foodItems);
                order.setTotalAmount(calculateTotalAmount(order));
                orderRepository.save(order);
                return itemMenu.getItemName() + " deleted successfully!!!";
            }
        }
        throw new CustomException("Food item not found in the order!!!");
    }

    public String deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("Order not found!!!"));
        orderRepository.delete(order);
        return "Order deleted successfully!!!";
    }


    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemMenu itemMenu : order.getItemMenu()) {
            totalAmount = totalAmount.add(itemMenu.getItemPrice());
        }
        return totalAmount;
    }
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
    }

    private Company getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Company company = companyRepository.findByCompanyEmail(userEmail);
        if (company == null) {
            throw new CustomException("Company not found");
        }
        return company;
    }
}
