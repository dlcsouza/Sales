package com.sales.api.dto;

import com.sales.api.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
