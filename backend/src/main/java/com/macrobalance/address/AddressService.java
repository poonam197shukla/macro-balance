package com.macrobalance.address.service;

import com.macrobalance.address.dto.AddressRequest;
import com.macrobalance.address.dto.AddressResponse;
import com.macrobalance.address.entity.Address;
import com.macrobalance.address.repository.AddressRepository;
import com.macrobalance.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;

    private static final int MAX_ADDRESSES = 5;

    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {

        // Limit addresses per user
        long count = addressRepository.findByUserId(userId).size();
        if (count >= MAX_ADDRESSES) {
            throw new BadRequestException(
                    "Maximum " + MAX_ADDRESSES + " addresses allowed");
        }

        // If this is being set as default, clear existing default first
        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
        }

        // If this is the user's first address, make it default automatically
        boolean shouldBeDefault = request.isDefault() || count == 0;

        Address address = new Address();
        address.setUserId(userId);
        applyRequest(address, request);
        address.setDefault(shouldBeDefault);

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId,
                                         AddressRequest request) {

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BadRequestException(
                        "Address not found"));

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
        }

        applyRequest(address, request);
        address.setDefault(request.isDefault());

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BadRequestException(
                        "Address not found"));

        // If deleting the default address, promote another one
        if (address.isDefault()) {
            addressRepository.findByUserId(userId)
                    .stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        addressRepository.save(next);
                    });
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {

        addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BadRequestException(
                        "Address not found"));

        addressRepository.clearDefaultForUser(userId);

        Address address = addressRepository.findById(addressId).get();
        address.setDefault(true);

        return toResponse(addressRepository.save(address));
    }

    // ── Mapper ─────────────────────────────────────────────────

    private void applyRequest(Address address, AddressRequest request) {
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setLabel(request.label());
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getLine1(),
                a.getLine2(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountry(),
                a.getLabel(),
                a.isDefault()
        );
    }
}