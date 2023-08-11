package com.virtual.power.plant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private T body;
    private String message;
    private String timestamp;
}