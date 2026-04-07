package com.macrobalance.address.controller;

import com.macrobalance.address.dto.AddressRequest;
import com.macrobalance.address.dto.AddressResponse;
import com.macrobalance.address.service.AddressService;
import com.macrobalance.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Addresses fetched",
                addressService.getAddresses(userId)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @RequestBody @Valid AddressRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Address added",
                addressService.addAddress(userId, request)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @RequestBody @Valid AddressRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Address updated",
                addressService.updateAddress(userId, id, request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Address deleted", null));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Default address updated",
                addressService.setDefault(userId, id)
        ));
    }
}