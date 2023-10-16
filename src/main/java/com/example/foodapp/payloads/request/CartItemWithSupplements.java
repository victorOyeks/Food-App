package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemWithSupplements {
    private String itemId;
    private int quantity;
    private List<SupplementItem> supplementItems;
    private BigDecimal totalAmount;
}

/*
i need you to help me refactor this code above so that in a cart, each item can have their own supplement. In this case,
a supplement must fall under an item, below is an example of how I want it to look like

An example of how the request will look like;
  [
    {
        "itemId": "123",
        "quantity": 2,
        "supplementItems": [
            {
                "supplementId": "927",
                "quantity": 1
            },
            {
                "supplementId": "736",
                "quantity": 3
            },
            {
                "supplementId": "837",
                "quantity": 4
            }
        }
    },
    {
        "itemId": "789",
        "quantity": 1,
        "supplementItems": [
            {
                "supplementId": "883",
                "quantity": 1
            },
            {
                "supplementId": "736",
                "quantity": 2
            }
        }
    }
]

And the Response can then come in this format
{
    "responseTime": "2023-10-13T11:30:15.123456",
    "status": "Success",
    "message": "Request Processed Successfully",
    "data": [
        {
            "itemId": "123",
            "itemName": "Rice",
            "itemPrice": 300,
            "quantity": 2,
            "supplementItems": [
                {
                    "supplementId": "927",
                    "supplementName": "Chicken",
                    "supplementPrice": 200,
                    "quantity": 1
                },
                {
                    "supplementId": "736",
                    "supplementName": "Soup",
                    "supplementPrice": 100,
                    "quantity": 3
                },
                {
                    "supplementId": "837",
                    "supplementName": "Plantain",
                    "supplementPrice": 200,
                    "quantity": 4
                }
            ],
            "totalAmount": 2100
        },
        {
            "itemId": "789",
            "itemName": "Eba",
            "itemPrice": 500,
            "quantity": 1,
            "supplementItems": [
                {
                    "supplementId": "883",
                    "supplementName": "Fish",
                    "supplementPrice": 1000,
                    "quantity": 1
                },
                {
                    "supplementId": "736",
                    "supplementName": "Soup",
                    "supplementPrice": 100,
                    "quantity": 2
                }
            ],
            "totalAmount": 1700
        }
    ],
    "orderSummary": {
            "totalItems": 2,
            "totalSum": 3800.00
        }
}
*/