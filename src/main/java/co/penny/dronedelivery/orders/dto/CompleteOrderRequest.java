package co.penny.dronedelivery.orders.dto;

import jakarta.validation.constraints.NotNull;

public record CompleteOrderRequest(@NotNull CompletionResult result) {
}
