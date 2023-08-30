package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.payloads.request.CategoryRequest;
import com.example.foodapp.payloads.request.SupplementRequest;
import com.example.foodapp.payloads.response.CategoryResponse;
import com.example.foodapp.payloads.response.ItemDetailsResponse;
import com.example.foodapp.payloads.response.ItemMenuResponse;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.payloads.response.SupplementResponse;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final VendorRepository vendorRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemMenuRepository itemMenuRepository;
    private final Cloudinary cloudinary;
    private final SupplementRepository supplementRepository;
    private final OrderRepository orderRepository;

    public CategoryResponse addItemCategory(CategoryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String vendorEmail = authentication.getName();
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        ItemCategory itemCategory = mapItemCategoryRequestToEntity(request);
        itemCategory.setVendor(vendor);
        vendor.getItemCategory().add(itemCategory);
        vendorRepository.save(vendor);
        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }

    public ItemMenuResponse addItemMenu(String itemName, BigDecimal itemPrice, String categoryId,
                                        MultipartFile file) throws IOException {

        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));
        ItemMenu itemMenu = new ItemMenu();

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", itemName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            String imageUrl = uploadResult.get("secure_url").toString();

            itemMenu.setItemName(itemName);
            itemMenu.setItemPrice(itemPrice);
            itemMenu.setImageUrl(imageUrl);
            itemMenu.setAvailableStatus(true);
            itemMenu.setItemCategory(itemCategory);

            itemCategory.getItemMenus().add(itemMenu);
            itemCategoryRepository.save(itemCategory);

            return ItemMenuResponse.builder()
                    .itemName(itemMenu.getItemName())
                    .itemPrice(itemMenu.getItemPrice())
                    .imageUrl(imageUrl)
                    .categoryName(itemCategory.getCategoryName())
                    .build();
        } else {
            throw new CustomException("Image file is required");
        }
    }

    public SupplementResponse addSupplement(SupplementRequest supplementRequest) {

        Vendor vendor = getAuthenticatedVendor();
        Supplement supplement = new Supplement();
        supplement.setSupplementName(supplementRequest.getSupplementName());
        supplement.setSupplementPrice(supplementRequest.getSupplementPrice());
        supplement.setSupplementCategory(supplementRequest.getSupplementCategory());
        supplement.setVendor(vendor);

        Supplement savedSupplement = supplementRepository.save(supplement);

        return SupplementResponse.builder()
                .supplementId(savedSupplement.getSupplementId())
                .supplementName(savedSupplement.getSupplementName())
                .supplementPrice(savedSupplement.getSupplementPrice())
                .supplementCategory(savedSupplement.getSupplementCategory())
                .build();
    }

    public ItemMenuResponse editItemMenu(String itemId, String itemName, BigDecimal itemPrice, String categoryId, Boolean availableStatus, MultipartFile file) throws IOException {

        String vendorId = getAuthenticatedVendor().getId();

        ItemMenu itemMenu = itemMenuRepository.findByItemIdAndVendorId(itemId, vendorId)
                .orElseThrow(() -> new CustomException("Item not found for the vendor"));

        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendorId, categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemMenu.setItemName(itemName);
        itemMenu.setItemPrice(itemPrice);
        itemMenu.setAvailableStatus(availableStatus);

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", itemName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            String imageUrl = uploadResult.get("secure_url").toString();
            itemMenu.setImageUrl(imageUrl);
        }

        itemMenu.setItemCategory(itemCategory);

        itemMenuRepository.save(itemMenu);

        return ItemMenuResponse.builder()
                .itemName(itemMenu.getItemName())
                .itemPrice(itemMenu.getItemPrice())
                .imageUrl(itemMenu.getImageUrl())
                .categoryName(itemCategory.getCategoryName())
                .build();
    }

    public CategoryResponse editItemCategory(String categoryId, CategoryRequest request) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemCategory.setCategoryName(request.getCategoryName());
        itemCategoryRepository.save(itemCategory);

        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }

    public CategoryResponse getItemCategory(String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        return CategoryResponse.builder()
                .categoryId(itemCategory.getCategoryId())
                .categoryName(itemCategory.getCategoryName())
                .itemMenus(itemCategory.getItemMenus())
                .build();
    }

    public void deleteItemMenu(String itemId, String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        ItemMenu itemMenu = itemCategory.getItemMenus().stream()
                .filter(menu -> menu.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Item not found in the category"));

        itemCategory.getItemMenus().remove(itemMenu);
        itemCategoryRepository.save(itemCategory);

        itemMenu.setItemCategory(null);
        itemMenuRepository.delete(itemMenu);
    }

    public List<CategoryResponse> getAllItemCategory() {
        Vendor vendor = getAuthenticatedVendor();
        List<ItemCategory> foodCategories = itemCategoryRepository.findByVendorId(vendor.getId());

        return foodCategories.stream()
                .map(foodCategory -> CategoryResponse.builder()
                        .categoryId(foodCategory.getCategoryId())
                        .categoryName(foodCategory.getCategoryName())
                        .itemMenus(foodCategory.getItemMenus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ItemDetailsResponse> getAllVendorItems() {
        Vendor vendor = getAuthenticatedVendor();

        List<ItemMenu> vendorItems = itemMenuRepository.findAllByVendorId(vendor.getId());

        Map<String, Long> itemMenuOrdersCountMap = getOrderCountByItemMenuName();

        return vendorItems.stream()
                .map(itemMenu -> {
                    String itemName = itemMenu.getItemName();
                    Long orderCount = itemMenuOrdersCountMap.getOrDefault(itemName, 0L);

                    return ItemDetailsResponse.builder()
                            .itemId(itemMenu.getItemId())
                            .itemName(itemMenu.getItemName())
                            .itemCategory(itemMenu.getItemCategory().getCategoryName())
                            .itemPrice(itemMenu.getItemPrice())
                            .availableStatus(itemMenu.getAvailableStatus())
                            .averageRating(itemMenu.getAverageRating())
                            .createdAt(itemMenu.getCreatedAt())
                            .updatedAt(itemMenu.getUpdatedAt())
                            .orderCount(orderCount)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public List<SupplementResponse> getAllSupplements() {
        Vendor vendor = getAuthenticatedVendor();

        List<Supplement> supplements = supplementRepository.findByVendorId(vendor.getId());

        return supplements.stream()
                .map(supplement -> SupplementResponse.builder()
                        .supplementId(supplement.getSupplementId())
                        .supplementName(supplement.getSupplementName())
                        .supplementPrice(supplement.getSupplementPrice())
                        .supplementCategory(supplement.getSupplementCategory())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteItemCategory(String categoryId) {
        Vendor vendor = getAuthenticatedVendor();
        ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendor.getId(), categoryId)
                .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

        itemCategoryRepository.delete(itemCategory);
    }

    public ItemMenu getFile(String id) {
        return itemMenuRepository.findByItemId(id);
    }

    /*************************** HELPER METHODS ***********************/

    private ItemCategory mapItemCategoryRequestToEntity(CategoryRequest categoryRequest) {
        ItemCategory itemCategory = new ItemCategory();
        itemCategory.setCategoryName(categoryRequest.getCategoryName());
        return itemCategory;
    }

    private Vendor getAuthenticatedVendor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String vendorEmail = authentication.getName();
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new CustomException("Vendor not found");
        }
        return vendor;
    }

    private Map<String, Long> getOrderCountByItemMenuName() {

        Vendor vendor = getAuthenticatedVendor();

        List<Order> allOrders = orderRepository.findOrdersByVendor(vendor);

        Map<String, Long> itemMenuOrdersCountMap = new HashMap<>();

        for (Order order : allOrders) {
            // Iterate through the cartItems map
            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the ItemMenu object from your data source using itemId
                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
                String itemName = itemMenu.getItemName();

                // Increment the count by the quantity
                itemMenuOrdersCountMap.put(itemName, itemMenuOrdersCountMap.getOrDefault(itemName, 0L) + quantity);
            }
        }
        return itemMenuOrdersCountMap;
    }
}
