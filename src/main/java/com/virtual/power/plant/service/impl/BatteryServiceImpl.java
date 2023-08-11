package com.virtual.power.plant.service.impl;

import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.BulkLoadResponseDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.FailedEntryWithReason;
import com.virtual.power.plant.dto.ResponseRecordsDto.SearchBatteryResponseDto;
import com.virtual.power.plant.entity.Battery;
import com.virtual.power.plant.mapper.BatteryEntityMapper;
import com.virtual.power.plant.repository.BatteryRepository;
import com.virtual.power.plant.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryServiceImpl implements BatteryService {
    private final BatteryRepository repository;
    private final BatteryEntityMapper entityMapper;

    @Override
    public BatteryDto save(BatteryDto request) {
        request.validate();
        Battery entity = entityMapper.toEntity(request);
        Battery savedEntity = repository.saveAndFlush(entity);
        return entityMapper.toDto(savedEntity);
    }

    @Override
    public BulkLoadResponseDto saveAll(List<BatteryDto> dtos) {
        if (dtos.isEmpty()) {
            throw new IllegalArgumentException("Cannot accept empty list");
        }
        List<FailedEntryWithReason> failedList = new ArrayList<>();
        List<Battery> batteryList = dtos.stream()
                .map(dto -> {
                    try {
                        dto.validate();
                        return entityMapper.toEntity(dto);
                    } catch (IllegalArgumentException e) {
                        failedList.add(new FailedEntryWithReason(e.getMessage(), dto));
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
        List<Battery> savedBatteries = repository.saveAllAndFlush(batteryList);
        return new BulkLoadResponseDto(entityMapper.toDtoList(savedBatteries), failedList);
    }

    @Override
    public SearchBatteryResponseDto findBatteryInRange(String fromPostCode, String toPostCode) {
        if (!StringUtils.hasText(fromPostCode) || !StringUtils.hasText(toPostCode))
            throw new IllegalArgumentException("Cannot accept null values for filtering criterias");

        List<Battery> batteriesInRange = repository.findBatteriesInRange(fromPostCode, toPostCode);
        if (CollectionUtils.isEmpty(batteriesInRange))
            return new SearchBatteryResponseDto(Collections.emptyList(), 0, 0d, 0);

        int sum = batteriesInRange.stream().mapToInt(Battery::getCapacity).sum();
        double avg = (double) sum / batteriesInRange.size();
        return new SearchBatteryResponseDto(entityMapper.toDtoList(batteriesInRange), sum, avg, batteriesInRange.size());
    }
}
