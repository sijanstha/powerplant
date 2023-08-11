package com.virtual.power.plant.dto;

import java.util.List;

public class ResponseRecordsDto {
    public record BulkLoadResponseDto(List<BatteryDto> savedList, List<FailedEntryWithReason> failedEntries) {
    }

    public record FailedEntryWithReason(String reason, BatteryDto entry) {
    }

    public record SearchBatteryResponseDto(List<BatteryDto> data, int totalCapacity, double avgCapacity, int totalCount) {
    }
}
