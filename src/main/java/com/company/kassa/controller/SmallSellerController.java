package com.company.kassa.controller;

import com.company.kassa.service.small_seller.SmallSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/small-seller")
public class SmallSellerController {
    private final SmallSellerService smallSellerService;


}
