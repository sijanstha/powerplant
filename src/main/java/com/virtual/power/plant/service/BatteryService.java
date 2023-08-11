package com.virtual.power.plant.service;

import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.*;

import java.util.List;

public interface BatteryService {
    BatteryDto save(BatteryDto request);
    BulkLoadResponseDto saveAll(List<BatteryDto> dtos);
    SearchBatteryResponseDto findBatteryInRange(String fromPostCode, String toPostCode);
}
