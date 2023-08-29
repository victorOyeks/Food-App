package com.example.foodapp.service.impl;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.PaymentStatus;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.entities.Order;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SupplementRepository supplementRepository;

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

    public String selectItemForIndividual(String vendorId, String menuId) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedItemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder != null) {
                List<ItemMenu> selectedItemMenus = existingOpenOrder.getItemMenu();
                selectedItemMenus.add(selectedItemMenu);
                existingOpenOrder.setItemMenu(selectedItemMenus);

                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedItemMenu.getItemPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setUser(user);

                List<ItemMenu> selectedItemMenus = new ArrayList<>();
                selectedItemMenus.add(selectedItemMenu);
                newOrder.setItemMenu(selectedItemMenus);

                BigDecimal totalAmount = selectedItemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }

            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }


    /*public String selectSupplementsForItemForIndividual(String vendorId, String menuId, List<String> supplementIds) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        ItemMenu selectedMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedMenu != null) {
            // Check if there is an open order for the user
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder == null) {
                // If there is no open order, create a new one
                existingOpenOrder = new Order();
                existingOpenOrder.setUser(user);
                existingOpenOrder.setTotalAmount(BigDecimal.ZERO); // Initialize total amount
                existingOpenOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                existingOpenOrder.setPaymentStatus(PaymentStatus.PENDING);
                existingOpenOrder.setItemMenu(new ArrayList<>()); // Initialize item menu list
            }

            // Calculate the total amount with item price and supplements
            BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedMenu.getItemPrice());

            // Calculate temporary total price with supplements
            BigDecimal tempTotalAmount = totalAmount;
            for (String supplementId : supplementIds) {
                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));

                // Check if the supplement belongs to the selected menu
                if (!selectedSupplement.getItemMenu().equals(selectedMenu)) {
                    throw new CustomException("Supplement does not belong to the selected menu!!!");
                }

                tempTotalAmount = tempTotalAmount.add(selectedSupplement.getSupplementPrice());
            }

            existingOpenOrder.getItemMenu().add(selectedMenu);
            existingOpenOrder.setTotalAmount(tempTotalAmount);
            orderRepository.save(existingOpenOrder);

            // If user's order list is null, initialize it
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            return "Supplements selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }

     */

    public OrderResponse selectSupplementsForItemForIndividual(String vendorId, String menuId, List<String> supplementIds) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        User user = getAuthenticatedUser();

        ItemMenu selectedMenu = findFoodMenuById(vendor.getId(), menuId);

        if (selectedMenu != null) {
            // Check if there is an open order for the user
            Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

            if (existingOpenOrder == null) {
                // If there is no open order, create a new one
                existingOpenOrder = new Order();
                existingOpenOrder.setUser(user);
                existingOpenOrder.setTotalAmount(BigDecimal.ZERO); // Initialize total amount
                existingOpenOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                existingOpenOrder.setPaymentStatus(PaymentStatus.PENDING);
                existingOpenOrder.setItemMenu(new ArrayList<>()); // Initialize item menu list
            }

            // Calculate the total amount with item price and supplements
            BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(selectedMenu.getItemPrice());

            // Calculate temporary total price with supplements
            BigDecimal tempTotalAmount = totalAmount;
            BigDecimal updatedItemPrice = selectedMenu.getItemPrice(); // Initialize updated item price

            for (String supplementId : supplementIds) {
                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));

                // Check if the supplement belongs to the selected menu
                if (!selectedSupplement.getItemMenu().equals(selectedMenu)) {
                    throw new CustomException("Supplement does not belong to the selected menu!!!");
                }

                tempTotalAmount = tempTotalAmount.add(selectedSupplement.getSupplementPrice());
                updatedItemPrice = updatedItemPrice.add(selectedSupplement.getSupplementPrice());
            }

            existingOpenOrder.getItemMenu().add(selectedMenu);

            existingOpenOrder.setTotalAmount(tempTotalAmount);

            // If user's order list is null, initialize it
            if (user.getOrderList() == null) {
                user.setOrderList(new ArrayList<>());
            }

            // Build and return the response
            OrderResponse orderResponse = buildOrderResponse(existingOpenOrder, updatedItemPrice);
            return orderResponse;
        } else {
            throw new CustomException("Item menu not found!!!");
        }
    }


    public String selectItemForCompany(String vendorId, String menuId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found!!!"));
        Company company = getAuthenticatedCompany();
        ItemMenu itemMenu = findFoodMenuById(vendor.getId(), menuId);

        if (itemMenu != null) {
            Order existingOpenOrder = orderRepository.findOpenOrderByCompany(company.getId());

            if (existingOpenOrder != null) {
                List<ItemMenu> selectedItemMenus = existingOpenOrder.getItemMenu();
                selectedItemMenus.add(itemMenu);
                existingOpenOrder.setItemMenu(selectedItemMenus);

                BigDecimal totalAmount = existingOpenOrder.getTotalAmount().add(itemMenu.getItemPrice());
                existingOpenOrder.setTotalAmount(totalAmount);

                orderRepository.save(existingOpenOrder);
            } else {
                Order newOrder = new Order();
                newOrder.setCompany(company);

                List<ItemMenu> selectedItemMenus = new ArrayList<>();
                selectedItemMenus.add(itemMenu);
                newOrder.setItemMenu(selectedItemMenus);

                BigDecimal totalAmount = itemMenu.getItemPrice();
                newOrder.setTotalAmount(totalAmount);
                newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
                newOrder.setPaymentStatus(PaymentStatus.PENDING);

                orderRepository.save(newOrder);
            }

            if (company.getOrderList() == null) {
                company.setOrderList(new ArrayList<>());
            }

            return "Food selected successfully!!!";
        } else {
            throw new CustomException("Item menu not found!!!");
        }
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


    public OrderResponse viewOrderByOrderIdForUser(String orderId) {
        String userId = getAuthenticatedUser().getId();
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndUserId(orderId, userId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return buildOrderResponse(order);
        } else {
            throw new CustomException("Order not found with orderId: " + orderId);
        }
    }

    public OrderResponse viewOrderByOrderIdForCompany(String orderId) {
        String companyId = getAuthenticatedCompany().getId();
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndCompanyId(orderId, companyId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return buildOrderResponse(order);
        } else {
            throw new CustomException("Order not found with orderId: " + orderId);
        }
    }

    /*
    public OrderViewResponse viewAllOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        return viewAllOrdersInternal(orderRepository.findOrdersByUserId(userId));
    }
     */

    public UserOrderViewResponse viewSimplifiedOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        List<Order> userOrders = orderRepository.findOrdersByUserId(userId);
        List<SimplifiedOrderResponse> simplifiedOrders = viewSimplifiedOrdersInternal(userOrders);
        return new UserOrderViewResponse(simplifiedOrders, null);
    }

    public OrderViewResponse viewUserCart() {
        String userId = getAuthenticatedUser().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByUserId(userId);
        return viewAllOrdersInternal(pendingOrders);
    }


    public OrderViewResponse viewCompanyCart() {
        String companyId = getAuthenticatedCompany().getId();
        List<Order> pendingOrders = orderRepository.findPendingOrdersByCompanyId(companyId);
        return viewAllOrdersInternal(pendingOrders);
    }

    public String deleteItem(String orderId, String foodItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));
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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));
        orderRepository.delete(order);
        return "Order deleted successfully!!!";
    }


    /*********************** HELPER METHODS ************************/

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

    /*
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
                        .supplementResponses(itemMenu.getSelectedSupplements().subList())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
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

     */

    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalFoodItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            for (ItemMenu itemMenu : order.getItemMenu()) {
                Vendor vendor = itemMenu.getItemCategory().getVendor();
                List<SupplementResponse> supplementResponses = new ArrayList<>();

                for (Supplement supplement : itemMenu.getSelectedSupplements()) {
                    supplementResponses.add(SupplementResponse.builder()
                            .supplementName(supplement.getSupplementName())
                            .supplementPrice(supplement.getSupplementPrice())
                            .build());
                }

                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemMenu.getItemId())
                        .itemName(itemMenu.getItemName())
                        .supplementResponses(supplementResponses)
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
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


    private List<SimplifiedOrderResponse> viewSimplifiedOrdersInternal(List<Order> orderList) {
        List<SimplifiedOrderResponse> simplifiedOrderResponses = new ArrayList<>();

        for (Order order : orderList) {
            simplifiedOrderResponses.add(new SimplifiedOrderResponse(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getDeliveryStatus(),
                    order.getPaymentStatus()
            ));
        }
        return simplifiedOrderResponses;
    }

    /*private UserOrderDetailsResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        for (ItemMenu itemMenu : order.getItemMenu()) {
            Vendor vendor = itemMenu.getItemCategory().getVendor();
            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(itemMenu.getItemId())
                    .itemName(itemMenu.getItemName())
                    .price(itemMenu.getItemPrice())
                    .imageUri(itemMenu.getImageUrl())
                    .vendorName(vendor.getBusinessName())
                    .build());
        }

        return UserOrderDetailsResponse.builder()
                .orderId(order.getOrderId())
                .itemMenu(order.getItemMenu())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .build();
    }

     */

    private OrderResponse buildOrderResponse(Order order, BigDecimal updatedItemPrice) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        for (ItemMenu itemMenu : order.getItemMenu()) {
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(itemMenu.getItemId())
                    .itemName(itemMenu.getItemName())
                    .price(updatedItemPrice) // Display updated item price
                    .imageUri(itemMenu.getImageUrl())
                    .vendorName(vendor.getBusinessName())
                    .supplementResponses(buildSupplementResponses(itemMenu.getSelectedSupplements()))
                    .build());
        }

        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(order.getItemMenu().size())
                .totalSum(order.getTotalAmount())
                .build();

        return new OrderResponse(order.getOrderId(), foodDataResponses, orderSummary.getTotalSum());
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();
        BigDecimal updatedItemPrice = BigDecimal.ZERO; // Initialize updated item price

        for (ItemMenu itemMenu : order.getItemMenu()) {
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            // Calculate updated item price with selected supplements
            for (Supplement supplement : itemMenu.getSelectedSupplements()) {
                updatedItemPrice = updatedItemPrice.add(supplement.getSupplementPrice());
            }
            BigDecimal displayedItemPrice = itemMenu.getItemPrice().add(updatedItemPrice);

            foodDataResponses.add(FoodDataResponse.builder()
                    .itemId(itemMenu.getItemId())
                    .itemName(itemMenu.getItemName())
                    .price(displayedItemPrice) // Display updated item price
                    .imageUri(itemMenu.getImageUrl())
                    .vendorName(vendor.getBusinessName())
                    .supplementResponses(buildSupplementResponses(itemMenu.getSelectedSupplements()))
                    .build());
        }

        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(order.getItemMenu().size())
                .totalSum(order.getTotalAmount())
                .build();

        return new OrderResponse(order.getOrderId(), foodDataResponses, orderSummary.getTotalSum());
    }

    // Add this method to your OrderServiceImpl class
    private List<SupplementResponse> buildSupplementResponses(List<Supplement> selectedSupplements) {
        List<SupplementResponse> supplementResponses = new ArrayList<>();

        for (Supplement supplement : selectedSupplements) {
            supplementResponses.add(SupplementResponse.builder()
                    .supplementId(supplement.getSupplementId())
                    .supplementName(supplement.getSupplementName())
                    .supplementPrice(supplement.getSupplementPrice())
                    .itemMenuName(supplement.getItemMenu().getItemName())
                    .build());
        }
        return supplementResponses;
    }

    private List<SupplementResponse> extractSupplementInfo(Order order) {
        List<SupplementResponse> supplementResponses = new ArrayList<>();
        for (ItemMenu itemMenu : order.getItemMenu()) {
            for (Supplement supplement : itemMenu.getSelectedSupplements()) {
                SupplementResponse supplementResponse = new SupplementResponse();
                supplementResponse.setSupplementId(supplement.getSupplementId());
                supplementResponse.setSupplementName(supplement.getSupplementName());
                supplementResponse.setSupplementPrice(supplement.getSupplementPrice());
                supplementResponses.add(supplementResponse);
            }
        }
        return supplementResponses;
    }

}