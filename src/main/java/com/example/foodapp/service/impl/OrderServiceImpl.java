package com.example.foodapp.service.impl;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.SubmitStatus;
import com.example.foodapp.payloads.request.CartItemWithSupplements;
import com.example.foodapp.payloads.request.SupplementItem;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final SupplementRepository supplementRepository;
    private final ItemMenuRepository itemMenuRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemSupplementRepository orderItemSupplementRepository;

   /* public List<FoodDataResponse> viewAllItemMenus() {

        User user = getAuthenticatedUser();
        List<FoodDataResponse> foodDataResponse = new ArrayList<>();
        List<Vendor> vendors = user.getCompany().getVendors();

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
    } */

    public List<FoodDataResponse> viewFoodItemsByVendorAndCategory(String vendorId, String categoryId) {
        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if (userCompany == null) {
            throw new CustomException("User is not associated with this company");
        }
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));
        if (!userCompany.getVendors().contains(vendor)) {
            throw new CustomException("Vendor not associated with this user's company");
        }
        ItemCategory category = vendor.getItemCategory().stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Category not found for this vendor"));

        List<FoodDataResponse> foodDataResponses = category.getItemMenus().stream()
                .map(itemMenu -> FoodDataResponse.builder()
                        .itemId(itemMenu.getItemId())
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
                        .vendorName(vendor.getBusinessName())
                        .build())
                .collect(Collectors.toList());

        return foodDataResponses;
    }

    @Override
    public List<SupplementResponse> viewAllSupplements(String vendorId) {

        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if (userCompany == null){
            throw new CustomException("User is not associated with this company");
        }
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));
        if (!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor not associated with this user's company");
        }
        List<Supplement> supplements = supplementRepository.findByVendorId(vendorId);
        List<SupplementResponse> supplementResponses = supplements.stream()
                .map(supplement -> SupplementResponse.builder()
                        .supplementId(supplement.getSupplementId())
                        .supplementName(supplement.getSupplementName())
                        .supplementPrice(supplement.getSupplementPrice())
                        .supplementCategory(supplement.getSupplementCategory())
                        .build())
                .collect(Collectors.toList());

        return supplementResponses;
    }

    @Override
    public OrderViewResponse addToCart(String vendorId, List<CartItemWithSupplements> cartItemsWithSupplements) {
        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();

        if (userCompany == null) {
            throw new CustomException("User is not associated with this company");
        }

        // Fetch the vendor's supplements
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));
        List<Supplement> vendorSupplements = vendor.getSupplements();

        if (!userCompany.getVendors().contains(vendor)) {
            throw new CustomException("Vendor is not associated with the user's company");
        }

//        Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());

//        if (existingOpenOrder != null) {
//            for (CartItemWithSupplements cartItemWithSupplements : cartItemsWithSupplements) {
//                String itemId = cartItemWithSupplements.getItemId();
//                int quantity = cartItemWithSupplements.getQuantity();
//                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
//
//                if (selectedItemMenu != null) {
//                    // Create an OrderItem for the current item and add it to the existing order
//                    OrderItem orderItem = new OrderItem();
//                    orderItem.setItemMenu(selectedItemMenu);
//                    orderItem.setQuantity(quantity);
//
//                    // Fetch supplement items from the cart
//                    List<OrderItemSupplement> orderItemSupplements = cartItemWithSupplements.getSupplementItems()
//                            .stream()
//                            .map(supplementItem -> {
//                                String supplementId = supplementItem.getSupplementId();
//                                int supplementQuantity = supplementItem.getQuantity();
//
//                                // Find the corresponding supplement in the vendor's supplements
//                                Supplement selectedSupplement = vendorSupplements.stream()
//                                        .filter(supplement -> supplement.getSupplementId().equals(supplementId))
//                                        .findFirst()
//                                        .orElseThrow(() -> new CustomException("Supplement not found"));
//
//                                OrderItemSupplement itemSupplement = new OrderItemSupplement();
//                                itemSupplement.setSupplement(selectedSupplement);
//                                itemSupplement.setQuantity(supplementQuantity);
//
//                                // Save the OrderItemSupplement
//                                orderItemSupplementRepository.save(itemSupplement);
//
//                                return itemSupplement;
//                            })
//                            .collect(Collectors.toList());
//
//                    // Add the supplements to the order item
//                    orderItem.setOrderItemSupplements(orderItemSupplements);
//
//                    // Calculate the total amount for the order item, including supplements
//                    BigDecimal itemTotalAmount = selectedItemMenu.getItemPrice().multiply(BigDecimal.valueOf(quantity));
//
//                    BigDecimal supplementsTotalPrice = orderItemSupplements.stream()
//                            .map(itemSupplement -> itemSupplement.getSupplement().getSupplementPrice()
//                                    .multiply(BigDecimal.valueOf(itemSupplement.getQuantity())))
//                            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//                    itemTotalAmount = itemTotalAmount.add(supplementsTotalPrice);
//
//                    orderItem.setItemTotalAmount(itemTotalAmount);
//
//                    // Save the OrderItem
//                    orderItemRepository.save(orderItem);
//
//                    // Add the saved order item to the existing order
//                    existingOpenOrder.getOrderItems().add(orderItem);
//                } else {
//                    throw new CustomException("Item menu not found!!!");
//                }
//            }
//
//            // Update total amount based on order items
//            BigDecimal totalAmount = calculateTotalAmount(existingOpenOrder);
//            existingOpenOrder.setTotalAmount(totalAmount);
//            orderRepository.save(existingOpenOrder);
//
//            // Prepare the OrderViewResponse
//            List<OrderResponse> orderResponses = new ArrayList<>();
//            OrderResponse orderResponse = new OrderResponse();
//            orderResponse.setOrderId(existingOpenOrder.getOrderId());
//            List<FoodDataResponse> items = existingOpenOrder.getOrderItems().stream()
//                    .map(orderItem -> {
//                        ItemMenu selectedItemMenu = orderItem.getItemMenu();
//                        List<SupplementResponse> itemSupplements = orderItem.getOrderItemSupplements()
//                                .stream()
//                                .map(orderItemSupplement -> {
//                                    Supplement selectedSupplement = orderItemSupplement.getSupplement();
//                                    int supplementQuantity = orderItemSupplement.getQuantity();
//                                    return new SupplementResponse(
//                                            selectedSupplement.getSupplementId(),
//                                            selectedSupplement.getSupplementName(),
//                                            selectedSupplement.getSupplementPrice(),
//                                            supplementQuantity,
//                                            selectedSupplement.getSupplementCategory()
//                                    );
//                                })
//                                .collect(Collectors.toList());
//
//                        FoodDataResponse foodDataResponse = new FoodDataResponse(
//                                selectedItemMenu.getItemId(),
//                                selectedItemMenu.getItemName(),
//                                selectedItemMenu.getItemPrice(),
//                                selectedItemMenu.getImageUrl(),
//                                orderItem.getQuantity(),
//                                itemSupplements,
//                                orderItem.getItemTotalAmount(),
//                                vendor.getBusinessName()
//                        );
//                        return foodDataResponse;
//                    })
//                    .collect(Collectors.toList());
//
//            BigDecimal orderTotalAmount = calculateTotalAmount(existingOpenOrder);
//            orderResponse.setItems(items);
//            orderResponse.setTotalAmount(orderTotalAmount);
//            orderResponses.add(orderResponse);
//
//            OrderSummary orderSummary = new OrderSummary();
//            orderSummary.setTotalItems(items.size());
//            orderSummary.setTotalSum(totalAmount);
//
//            OrderViewResponse orderViewResponse = new OrderViewResponse();
//            orderViewResponse.setOrderResponses(orderResponses);
//            orderViewResponse.setOrderSummary(orderSummary);
//
//            return orderViewResponse;
//        } else {
            // Create a new order with cart items and supplements

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setVendor(vendor);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemWithSupplements cartItemWithSupplements : cartItemsWithSupplements) {
            String itemId = cartItemWithSupplements.getItemId();
            int quantity = cartItemWithSupplements.getQuantity();
            ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);

            if (selectedItemMenu != null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setItemMenu(selectedItemMenu);
                orderItem.setQuantity(quantity);

                List<OrderItemSupplement> orderItemSupplements = cartItemWithSupplements.getSupplementItems()
                        .stream()
                        .map(supplementItem -> {
                            String supplementId = supplementItem.getSupplementId();
                            int supplementQuantity = supplementItem.getQuantity();

                            Supplement selectedSupplement = vendorSupplements.stream()
                                    .filter(supplement -> supplement.getSupplementId().equals(supplementId))
                                    .findFirst()
                                    .orElse(null);

                            if (selectedSupplement != null) {
                                OrderItemSupplement itemSupplement = new OrderItemSupplement();
                                itemSupplement.setSupplement(selectedSupplement);
                                itemSupplement.setQuantity(supplementQuantity);
                                itemSupplement.setOrderItem(orderItem);

                                return itemSupplement;
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                orderItem.setOrderItemSupplements(orderItemSupplements);

                BigDecimal itemTotalAmount = selectedItemMenu.getItemPrice().multiply(BigDecimal.valueOf(quantity));

                BigDecimal supplementsTotalPrice = orderItemSupplements.stream()
                        .map(itemSupplement -> itemSupplement.getSupplement().getSupplementPrice()
                                .multiply(BigDecimal.valueOf(itemSupplement.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                itemTotalAmount = itemTotalAmount.add(supplementsTotalPrice);

                orderItem.setItemTotalAmount(itemTotalAmount);
                orderItem.setOrder(newOrder);

                orderItems.add(orderItem);
            } else {
                throw new CustomException("Item menu not found!!!");
            }
        }
            newOrder.setOrderItems(orderItems);

            // Set total amount based on order items
            BigDecimal totalAmount = calculateTotalAmount(newOrder);
            newOrder.setTotalAmount(totalAmount);
            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
            newOrder.setSubmitStatus(SubmitStatus.SUBMITTED);

            if (userCompany.getPriceLimit() == null) {
                throw new CustomException("Your company has not set a price limit. You cannot make an order!!!");
            }

            if (totalAmount.compareTo(userCompany.getPriceLimit()) > 0) {
                throw new CustomException("You cannot add items worth more than " + userCompany.getPriceLimit() + " to the cart");
            }

            orderRepository.save(newOrder);

            // Prepare the OrderViewResponse for the new order
            List<OrderResponse> orderResponses = new ArrayList<>();
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(newOrder.getOrderId());
            List<FoodDataResponse> items = newOrder.getOrderItems().stream()
                    .map(orderItem -> {
                        ItemMenu selectedItemMenu = orderItem.getItemMenu();
                        List<SupplementResponse> itemSupplements = orderItem.getOrderItemSupplements()
                                .stream()
                                .map(orderItemSupplement -> {
                                    Supplement selectedSupplement = orderItemSupplement.getSupplement();
                                    int supplementQuantity = orderItemSupplement.getQuantity();
                                    return new SupplementResponse(
                                            selectedSupplement.getSupplementId(),
                                            selectedSupplement.getSupplementName(),
                                            selectedSupplement.getSupplementPrice(),
                                            supplementQuantity,
                                            selectedSupplement.getSupplementCategory()
                                    );
                                })
                                .collect(Collectors.toList());

                        FoodDataResponse foodDataResponse = new FoodDataResponse(
                                selectedItemMenu.getItemId(),
                                selectedItemMenu.getItemName(),
                                selectedItemMenu.getItemPrice(),
                                selectedItemMenu.getImageUrl(),
                                orderItem.getQuantity(),
                                itemSupplements,
                                orderItem.getItemTotalAmount(),
                                vendor.getBusinessName()
                        );
                        return foodDataResponse;
                    })
                    .collect(Collectors.toList());

            BigDecimal orderTotalAmount = calculateTotalAmount(newOrder);
            orderResponse.setItems(items);
            orderResponse.setTotalAmount(orderTotalAmount);
            orderResponses.add(orderResponse);

            OrderSummary orderSummary = new OrderSummary();
            orderSummary.setTotalItems(items.size());
            orderSummary.setTotalSum(totalAmount);

            OrderViewResponse orderViewResponse = new OrderViewResponse();
            orderViewResponse.setOrderResponses(orderResponses);
            orderViewResponse.setOrderSummary(orderSummary);

            return orderViewResponse;
        }


    private OrderViewResponse createOrderViewResponse(Order order, List<Supplement> vendorSupplements) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getOrderId());
        List<FoodDataResponse> items = new ArrayList<>();

        for (OrderItem orderItem : order.getOrderItems()) {
            ItemMenu selectedItemMenu = orderItem.getItemMenu();
            int quantity = orderItem.getQuantity();
            List<SupplementResponse> itemSupplements = new ArrayList<>();

            for (OrderItemSupplement orderItemSupplement : orderItem.getOrderItemSupplements()) {
                Supplement selectedSupplement = orderItemSupplement.getSupplement();
                int supplementQuantity = orderItemSupplement.getQuantity();

                // Create and add a SupplementResponse
                SupplementResponse supplementResponse = new SupplementResponse(
                        selectedSupplement.getSupplementId(),
                        selectedSupplement.getSupplementName(),
                        selectedSupplement.getSupplementPrice(),
                        supplementQuantity,
                        selectedSupplement.getSupplementCategory()
                );
                itemSupplements.add(supplementResponse);
            }

            BigDecimal itemTotalAmount = selectedItemMenu.getItemPrice().multiply(BigDecimal.valueOf(quantity));

            // Add the total price of supplements to itemTotalAmount
            BigDecimal supplementsTotalPrice = itemSupplements.stream()
                    .map(supplementResponse -> supplementResponse.getSupplementPrice()
                            .multiply(BigDecimal.valueOf(supplementResponse.getSupplementQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            itemTotalAmount = itemTotalAmount.add(supplementsTotalPrice);

            FoodDataResponse foodDataResponse = new FoodDataResponse(
                    selectedItemMenu.getItemId(),
                    selectedItemMenu.getItemName(),
                    selectedItemMenu.getItemPrice(),
                    selectedItemMenu.getImageUrl(),
                    quantity,
                    itemSupplements,
                    itemTotalAmount,
                    order.getVendor().getBusinessName()
            );

            items.add(foodDataResponse);
        }

        BigDecimal orderTotalAmount = calculateTotalAmount(order);
        orderResponse.setItems(items);
        orderResponse.setTotalAmount(orderTotalAmount);
        orderResponses.add(orderResponse);

        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setTotalItems(items.size());
        orderSummary.setTotalSum(orderTotalAmount);

        OrderViewResponse orderViewResponse = new OrderViewResponse();
        orderViewResponse.setOrderResponses(orderResponses);
        orderViewResponse.setOrderSummary(orderSummary);

        return orderViewResponse;
    }



//    @Override
//    public OrderViewResponse addToCart(String vendorId, List<CartItemWithSupplements> cartItemsWithSupplements) {
//        User user = getAuthenticatedUser();
//        Company userCompany = user.getCompany();
//
//        if (userCompany == null) {
//            throw new CustomException("User is not associated with this company");
//        }
//
//        // Fetch the vendor's supplements
//        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException("Vendor not found"));
//        List<Supplement> vendorSupplements = vendor.getSupplements();
//
//        if (!userCompany.getVendors().contains(vendor)) {
//            throw new CustomException("Vendor is not associated with the user's company");
//        }
//
//        Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());
//
//        if (existingOpenOrder != null) {
//            // Update existing open order with cart items and supplements
//            Map<String, Integer> orderItems = existingOpenOrder.getItemMenus();
//            Map<String, Integer> orderSupplements = existingOpenOrder.getSupplements();
//
//            for (CartItemWithSupplements cartItemWithSupplements : cartItemsWithSupplements) {
//                String itemId = cartItemWithSupplements.getItemId();
//                int quantity = cartItemWithSupplements.getQuantity();
//                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
//
//                if (selectedItemMenu != null) {
//                    // Check if the item is already in the cart
//                    if (orderItems.containsKey(itemId)) {
//                        int existingQuantity = orderItems.get(itemId);
//                        orderItems.put(itemId, existingQuantity + quantity);
//                    } else {
//                        orderItems.put(itemId, quantity);
//                    }
//
//                    // Fetch supplement items from the vendor's supplements
//                    List<SupplementItem> supplementItems = cartItemWithSupplements.getSupplementItems();
//                    for (SupplementItem supplementItem : supplementItems) {
//                        String supplementId = supplementItem.getSupplementId();
//                        int supplementQuantity = supplementItem.getQuantity();
//
//                        // Find the corresponding supplement in the vendor's supplements
//                        Supplement selectedSupplement = vendorSupplements.stream()
//                                .filter(supplement -> supplement.getSupplementId().equals(supplementId))
//                                .findFirst()
//                                .orElseThrow(() -> new CustomException("Supplement not found"));
//
//                        // Check if the supplement is already in the cart
//                        if (orderSupplements.containsKey(selectedSupplement)) {
//                            int existingSupplementQuantity = orderSupplements.get(supplementId);
//                            orderSupplements.put(supplementId, existingSupplementQuantity + supplementQuantity);
//                        } else {
//                            orderSupplements.put(supplementId, supplementQuantity);
//                        }
//                    }
//                } else {
//                    throw new CustomException("Item menu not found!!!");
//                }
//            }
//
//            // Update total amount based on cart items and supplements
//            BigDecimal totalAmount = calculateTotalAmount(existingOpenOrder);
//            existingOpenOrder.setTotalAmount(totalAmount);
//            orderRepository.save(existingOpenOrder);
//        } else {
//            // Create a new order with cart items and supplements
//            Order newOrder = new Order();
//            newOrder.setUser(user);
//
//            Map<String, Integer> orderItems = new HashMap<>();
//            Map<String, Integer> orderSupplements = new HashMap<>();
//
//            for (CartItemWithSupplements cartItemWithSupplements : cartItemsWithSupplements) {
//                String itemId = cartItemWithSupplements.getItemId();
//                int quantity = cartItemWithSupplements.getQuantity();
//                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
//
//                if (selectedItemMenu != null) {
//                    orderItems.put(itemId, quantity);
//
//                    // Fetch supplement items from the vendor's supplements
//                    List<SupplementItem> supplementItems = cartItemWithSupplements.getSupplementItems();
//                    for (SupplementItem supplementItem : supplementItems) {
//                        String supplementId = supplementItem.getSupplementId();
//                        int supplementQuantity = supplementItem.getQuantity();
//
//                        // Find the corresponding supplement in the vendor's supplements
//                        Supplement selectedSupplement = vendorSupplements.stream()
//                                .filter(supplement -> supplement.getSupplementId().equals(supplementId))
//                                .findFirst()
//                                .orElseThrow(() -> new CustomException("Supplement not found"));
//
//                        orderSupplements.put(selectedSupplement.getSupplementId(), supplementQuantity);
//                    }
//                } else {
//                    throw new CustomException("Item menu not found!!!");
//                }
//            }
//
//            newOrder.setItemMenus(orderItems);
//            newOrder.setSupplements(orderSupplements);
//
//            // Set total amount based on cart items and supplements
//            BigDecimal totalAmount = calculateTotalAmount(newOrder);
//            newOrder.setTotalAmount(totalAmount);
//            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
//            newOrder.setSubmitStatus(SubmitStatus.PENDING);
//            newOrder.setVendor(vendor);
//
//            if (userCompany.getPriceLimit() == null) {
//                throw new CustomException("Your company has not set a price limit. You cannot make an order!!!");
//            }
//
//            if (totalAmount.compareTo(userCompany.getPriceLimit()) > 0) {
//                throw new CustomException("You cannot add items worth more than " + userCompany.getPriceLimit() + " to the cart");
//            }
//
//            orderRepository.save(newOrder);
//        }
//
//        if (user.getOrderList() == null) {
//            user.setOrderList(new ArrayList<>());
//        }
//
//        return viewUserCart();
//    }



    /* public OrderViewResponse addToCart(String vendorId, List<CartItem> cartItems, List<SupplementItem> supplementItems) {
        User user = getAuthenticatedUser();
        Company userCompany = user.getCompany();
        if (userCompany == null) {
            throw new CustomException("User is not associated with this company");
        }
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor not found"));
        if(!userCompany.getVendors().contains(vendor)){
            throw new CustomException("Vendor is not associated to user's company");
        }
        Order existingOpenOrder = orderRepository.findOpenOrderByUser(user.getId());
        if (existingOpenOrder != null) {

            // Update existing open order with cart items and supplements
            Map<String, Integer> orderItems = existingOpenOrder.getItemMenus();
            Map<String, Integer> orderSupplements = existingOpenOrder.getSupplements();
            for (CartItem cartItem : cartItems) {
                String itemId = cartItem.getItemId();
                int quantity = cartItem.getQuantity();
                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
                if (selectedItemMenu != null) {
                    // Check if the item is already in the cart
                    if (orderItems.containsKey(itemId)) {
                        int existingQuantity = orderItems.get(itemId);
                        orderItems.put(itemId, existingQuantity + quantity);
                    } else {
                        orderItems.put(itemId, quantity);
                    }
                } else {
                    throw new CustomException("Item menu not found!!!");
                }
            }

            for (SupplementItem supplementItem : supplementItems) {
                String supplementId = supplementItem.getSupplementId();
                int quantity = supplementItem.getQuantity();
                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));
                if (selectedSupplement != null) {
                    // Check if the supplement is already in the cart
                    if (orderSupplements.containsKey(supplementId)) {
                        int existingQuantity = orderSupplements.get(supplementId);
                        orderSupplements.put(supplementId, existingQuantity + quantity);
                    } else {
                        orderSupplements.put(supplementId, quantity);
                    }
                } else {
                    throw new CustomException("Supplement not found");
                }
            }

            // Update total amount based on cart items and supplements
            BigDecimal totalAmount = calculateTotalAmount(existingOpenOrder);
            existingOpenOrder.setTotalAmount(totalAmount);
//            existingOpenOrder.setVendor(vendor);
            orderRepository.save(existingOpenOrder);
        } else {
            // Create a new order with cart items and supplements
            Order newOrder = new Order();
            newOrder.setUser(user);

            Map<String, Integer> orderItems = new HashMap<>();
            Map<String, Integer> orderSupplements = new HashMap<>();

            for (CartItem cartItem : cartItems) {
                String itemId = cartItem.getItemId();
                int quantity = cartItem.getQuantity();
                ItemMenu selectedItemMenu = findFoodMenuById(vendor.getId(), itemId);
                if (selectedItemMenu != null) {
                    orderItems.put(itemId, quantity);
                } else {
                    throw new CustomException("Item menu not found!!!");
                }
            }
            for (SupplementItem supplementItem : supplementItems) {
                String supplementId = supplementItem.getSupplementId();
                int quantity = supplementItem.getQuantity();

                Supplement selectedSupplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));
                orderSupplements.put(selectedSupplement.getSupplementId(), quantity);
            }
            newOrder.setItemMenus(orderItems);
            newOrder.setSupplements(orderSupplements);

            // Set total amount based on cart items and supplements
            BigDecimal totalAmount = calculateTotalAmount(newOrder);
            newOrder.setTotalAmount(totalAmount);
            newOrder.setDeliveryStatus(DeliveryStatus.PENDING);
            newOrder.setSubmitStatus(SubmitStatus.PENDING);
            newOrder.setVendor(vendor);

            if(userCompany.getPriceLimit() == null){
                throw new CustomException("Your company has not set a price limit. You cannot make an order!!!");
            }
            if (totalAmount.compareTo(userCompany.getPriceLimit()) > 0) {
                throw new CustomException("You cannot add items worth more than " + userCompany.getPriceLimit() + " to cart");
            }
            orderRepository.save(newOrder);
        }
        if (user.getOrderList() == null) {
            user.setOrderList(new ArrayList<>());
        }
        return viewUserCart();
    }
     */

    @Override
    public String submitCart (String orderId){
        User user = getAuthenticatedUser();
        Order order = orderRepository.findOrderByOrderIdAndUserId(orderId, user.getId());

        if(order.getSubmitStatus().equals(SubmitStatus.SUBMITTED)){
            throw new CustomException("Order already submitted!!!");
        }
        order.setSubmitStatus(SubmitStatus.SUBMITTED);
        orderRepository.save(order);

        return "Order sent to vendor successfully";
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

    public UserOrderDetailsResponse viewOrderByOrderIdForUser(String orderId) {
        String userId = getAuthenticatedUser().getId();
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndUserId(orderId, userId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return buildOrderResponse(order);
        } else {
            throw new CustomException("Order not found with orderId: " + orderId);
        }
    }

    public UserOrderViewResponse viewSimplifiedOrdersByUser() {
        String userId = getAuthenticatedUser().getId();
        List<Order> userOrders = orderRepository.findOrdersByUserId(userId);
        List<SimplifiedOrderResponse> simplifiedOrders = viewSimplifiedOrdersInternal(userOrders);
        return new UserOrderViewResponse(simplifiedOrders, null);
    }

//    public OrderViewResponse deleteItem(String orderId, String foodItemId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new CustomException("Order not found!!!"));
//
//        List<OrderItem> orderItems = order.getOrderItems();
//        Optional<OrderItem> orderItemOptional = orderItems.stream()
//                .filter(orderItem -> orderItem.getItemMenu().getItemId().equals(foodItemId))
//                .findFirst();
//
//        if (orderItemOptional.isPresent()) {
//            OrderItem orderItem = orderItemOptional.get();
//
//            if (orderItem.getQuantity() > 1) {
//                orderItem.setQuantity(orderItem.getQuantity() - 1);
//            } else {
//                orderItems.remove(orderItem);
//            }
//            order.setTotalAmount(calculateTotalAmount(order));
//            orderRepository.save(order);
//
//            return viewUserCart();
//        } else {
//            throw new CustomException("Food item not found in the cart!!!");
//        }
//    }


    public String deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found!!!"));
        orderRepository.delete(order);
        return "Order deleted successfully!!!";
    }

    /*********************** HELPER METHODS ************************/

    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            // Calculate the total amount for the item including supplements
            BigDecimal itemTotal = orderItem.getItemTotalAmount();
            totalAmount = totalAmount.add(itemTotal);
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

//    public OrderViewResponse viewUserCart() {
//        String userId = getAuthenticatedUser().getId();
//        List<Order> pendingOrders = orderRepository.findPendingOrdersByUserId(userId);
//        return viewAllOrdersInternal(pendingOrders);
//    }
//
//    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
//        List<OrderResponse> orderResponses = new ArrayList<>();
//        int totalItems = 0;
//        BigDecimal totalSum = BigDecimal.ZERO;
//
//        for (Order order : orderList) {
//            List<FoodDataResponse> foodDataResponses = new ArrayList();
//
//            for (OrderItem orderItem : order.getOrderItems()) {
//                ItemMenu itemMenu = orderItem.getItemMenu();
//                Vendor vendor = itemMenu.getItemCategory().getVendor();
//                List<SupplementResponse> supplementResponses = new ArrayList<>();
//
//                // Fetch and iterate through the supplements associated with the order item
//                for (OrderItemSupplement orderItemSupplement : orderItem.getOrderItemSupplements()) {
//                    Supplement supplement = orderItemSupplement.getSupplement();
//                    int supplementQuantity = orderItemSupplement.getQuantity();
//
//                    supplementResponses.add(new SupplementResponse(
//                            supplement.getSupplementId(),
//                            supplement.getSupplementName(),
//                            supplement.getSupplementPrice(),
//                            supplementQuantity,
//                            supplement.getSupplementCategory()
//                    ));
//                }
//
//                BigDecimal itemTotalAmount = orderItem.getItemTotalAmount();
//
//                // Construct the FoodDataResponse for the item
//                FoodDataResponse foodDataResponse = new FoodDataResponse(
//                        itemMenu.getItemId(),
//                        itemMenu.getItemName(),
//                        itemMenu.getItemPrice(),
//                        itemMenu.getImageUrl(),
//                        orderItem.getQuantity(),
//                        supplementResponses,
//                        itemTotalAmount,
//                        vendor.getBusinessName()
//                );
//
//                foodDataResponses.add(foodDataResponse);
//            }
//
//            BigDecimal orderTotalAmount = order.getTotalAmount();
//
//            // Add the constructed order response to the list
//            orderResponses.add(new OrderResponse(
//                    order.getOrderId(),
//                    foodDataResponses,
//                    orderTotalAmount
//            ));
//
//            // Update the total items and total sum
//            totalItems += foodDataResponses.size();
//            totalSum = totalSum.add(orderTotalAmount);
//        }
//
//        // Prepare the OrderSummary for the response
//        OrderSummary orderSummary = new OrderSummary(totalItems, totalSum);
//
//        // Create the OrderViewResponse
//        OrderViewResponse orderViewResponse = new OrderViewResponse(orderResponses, orderSummary);
//
//        return orderViewResponse;
//    }


    private List<SimplifiedOrderResponse> viewSimplifiedOrdersInternal(List<Order> orderList) {
        List<SimplifiedOrderResponse> simplifiedOrderResponses = new ArrayList<>();

        for (Order order : orderList) {
            simplifiedOrderResponses.add(new SimplifiedOrderResponse(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getDeliveryStatus(),
                    order.getSubmitStatus()
            ));
        }
        return simplifiedOrderResponses;
    }

    private UserOrderDetailsResponse buildOrderResponse(Order order) {
        List<FoodDataResponse> foodDataResponses = new ArrayList();

        for (OrderItem orderItem : order.getOrderItems()) {
            ItemMenu itemMenu = orderItem.getItemMenu();
            Vendor vendor = itemMenu.getItemCategory().getVendor();
            String itemId = itemMenu.getItemId();
            int quantity = orderItem.getQuantity();

            // Create a FoodDataResponse for each item menu
            FoodDataResponse itemMenuResponse = new FoodDataResponse();
            itemMenuResponse.setItemId(itemId);
            itemMenuResponse.setItemName(itemMenu.getItemName());
            itemMenuResponse.setPrice(itemMenu.getItemPrice());
            itemMenuResponse.setQuantity(quantity);
            itemMenuResponse.setImageUri(itemMenu.getImageUrl());
            itemMenuResponse.setVendorName(vendor.getBusinessName());

            // Create a list of supplement responses for this item menu
            List<SupplementResponse> supplementResponses = new ArrayList();
            for (OrderItemSupplement orderItemSupplement : orderItem.getOrderItemSupplements()) {
                Supplement supplement = orderItemSupplement.getSupplement();
                String supplementId = supplement.getSupplementId();
                int supplementQuantity = orderItemSupplement.getQuantity();

                // Create a SupplementResponse for each supplement
                SupplementResponse supplementResponse = new SupplementResponse();
                supplementResponse.setSupplementId(supplementId);
                supplementResponse.setSupplementName(supplement.getSupplementName());
                supplementResponse.setSupplementPrice(supplement.getSupplementPrice());
                supplementResponse.setSupplementQuantity(supplementQuantity);
                supplementResponse.setSupplementCategory(supplement.getSupplementCategory());

                supplementResponses.add(supplementResponse);
            }
            itemMenuResponse.setSupplementResponses(supplementResponses);

            foodDataResponses.add(itemMenuResponse);
        }

        return UserOrderDetailsResponse.builder()
                .orderId(order.getOrderId())
                .itemMenu(foodDataResponses)
                .totalAmount(order.getTotalAmount())
                .submitStatus(order.getSubmitStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .build();
    }
}