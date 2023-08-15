package com.virtual.power.plant.controller;

import com.virtual.power.plant.dto.ApiResponse;
import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.BulkLoadResponseDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.SearchBatteryResponseDto;
import com.virtual.power.plant.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/battery")
@RequiredArgsConstructor
public class BatteryController {
    private final BatteryService batteryService;

    @PostMapping("/bulk/load")
    public ApiResponse<BulkLoadResponseDto> load(@RequestBody List<BatteryDto> batteryDtos) {
        BulkLoadResponseDto bulkLoadResponseDto = batteryService.saveAll(batteryDtos);
        String message = CollectionUtils.isEmpty(bulkLoadResponseDto.failedEntries()) ? "Data loaded successfully" : "Partial data loaded";
        return ApiResponse.<BulkLoadResponseDto>builder()
                .body(bulkLoadResponseDto)
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }

    @GetMapping
    public ApiResponse<SearchBatteryResponseDto> find(@RequestParam("from_post_code") String fromPostCode, @RequestParam("to_post_code") String toPostCode) {
        SearchBatteryResponseDto batteryInRange = batteryService.findBatteryInRange(fromPostCode, toPostCode);
        return ApiResponse.<SearchBatteryResponseDto>builder()
                .message("Fetched successfully")
                .body(batteryInRange)
                .timestamp(Instant.now().toString())
                .build();
    }
}
