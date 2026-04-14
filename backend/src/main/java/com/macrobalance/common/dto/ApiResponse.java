package com.macrobalance.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic wrapper for all API responses in the MacroBalance backend.
 *
 * <p>Every endpoint returns this structure so the frontend has a
 * consistent envelope to work with regardless of the operation:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Order placed successfully",
 *   "data": { ... }
 * }
 * </pre>
 *
 * <p>On failure, {@code success} is {@code false}, {@code message}
 * contains the error description, and {@code data} is {@code null}:
 * <pre>
 * {
 *   "success": false,
 *   "message": "Insufficient stock for product: protein-bar-iron",
 *   "data": null
 * }
 * </pre>
 *
 * <p>The type parameter {@code T} represents the payload type.
 * Common values: a single entity response DTO, a {@code Page<T>},
 * a {@code List<T>}, a JWT token {@code String}, or {@code Void}/{@code null}
 * for operations that return no data.
 *
 * @param <T> type of the response payload
 */
@Schema(description = "Standard API response envelope used by all endpoints")
public record ApiResponse<T>(

        @Schema(
                description = "Whether the operation completed successfully",
                example = "true"
        )
        boolean success,

        @Schema(
                description = "Human-readable description of the outcome",
                example = "Order placed successfully"
        )
        String message,

        @Schema(
                description = "Response payload. Null for operations that return no data " +
                        "(e.g. delete, password change) or on error."
        )
        T data

) {}