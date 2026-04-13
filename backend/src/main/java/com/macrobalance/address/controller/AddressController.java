package com.macrobalance.address.controller;

import com.macrobalance.address.dto.AddressRequest;
import com.macrobalance.address.dto.AddressResponse;
import com.macrobalance.address.service.AddressService;
import com.macrobalance.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing delivery addresses.
 *
 * <p>All endpoints require authentication. A user may have a maximum
 * of 5 saved addresses. One address can be marked as the default,
 * which is pre-selected during checkout.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Manage saved delivery addresses for the authenticated user")
@SecurityRequirement(name = "Bearer Auth")
public class AddressController {

    private final AddressService addressService;

    /**
     * Retrieves all saved addresses for the currently authenticated user.
     *
     * @param authentication the current user's authentication context
     * @return list of saved addresses
     */
    @Operation(
            summary = "Get all addresses",
            description = "Returns all delivery addresses saved by the authenticated user"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Addresses fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized — JWT token missing or invalid")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Addresses fetched",
                addressService.getAddresses(userId)
        ));
    }

    /**
     * Adds a new delivery address for the authenticated user.
     *
     * <p>A maximum of 5 addresses are allowed per user. If this is the
     * user's first address, it will automatically be marked as default.
     * If {@code isDefault} is true, any existing default address is cleared.
     *
     * @param request        the address details
     * @param authentication the current user's authentication context
     * @return the newly created address
     */
    @Operation(
            summary = "Add a new address",
            description = "Saves a new delivery address. Maximum 5 addresses allowed per user. " +
                    "First address is automatically set as default."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Address added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or address limit reached"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
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

    /**
     * Updates an existing address by ID.
     *
     * <p>The address must belong to the authenticated user.
     * If {@code isDefault} is set to true, the previous default is cleared.
     *
     * @param id             the ID of the address to update
     * @param request        the updated address details
     * @param authentication the current user's authentication context
     * @return the updated address
     */
    @Operation(
            summary = "Update an address",
            description = "Updates an existing address. The address must belong to the authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @Parameter(description = "ID of the address to update", required = true)
            @PathVariable Long id,
            @RequestBody @Valid AddressRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Address updated",
                addressService.updateAddress(userId, id, request)
        ));
    }

    /**
     * Deletes an address by ID.
     *
     * <p>The address must belong to the authenticated user. If the deleted
     * address was the default, another address is automatically promoted
     * to default.
     *
     * @param id             the ID of the address to delete
     * @param authentication the current user's authentication context
     * @return empty success response
     */
    @Operation(
            summary = "Delete an address",
            description = "Deletes a saved address. If it was the default, another address is promoted automatically."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(description = "ID of the address to delete", required = true)
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(userId, id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Address deleted", null));
    }

    /**
     * Sets an address as the default delivery address.
     *
     * <p>Clears the default flag on any previously default address and
     * applies it to the specified address.
     *
     * @param id             the ID of the address to set as default
     * @param authentication the current user's authentication context
     * @return the updated address with {@code isDefault = true}
     */
    @Operation(
            summary = "Set default address",
            description = "Marks the specified address as the default. Clears the default flag on any previous default address."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Default address updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Address not found")
    })
    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @Parameter(description = "ID of the address to set as default", required = true)
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Default address updated",
                addressService.setDefault(userId, id)
        ));
    }
}