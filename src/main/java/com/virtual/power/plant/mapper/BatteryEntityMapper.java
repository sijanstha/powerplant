package com.virtual.power.plant.mapper;

import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.entity.Battery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatteryEntityMapper {

    public BatteryDto toDto(Battery entity) {
        if (entity == null)
            return null;

        BatteryDto dto = new BatteryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPostcode(entity.getPostcode());
        dto.setCapacity(entity.getCapacity());
        return dto;
    }

    public Battery toEntity(BatteryDto dto) {
        if (dto == null)
            return null;

        Battery entity = new Battery();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPostcode(dto.getPostcode());
        entity.setCapacity(dto.getCapacity());
        return entity;
    }

    public List<BatteryDto> toDtoList(List<Battery> batteries) {
        if (CollectionUtils.isEmpty(batteries))
            return Collections.emptyList();

        return batteries.stream().map(battery -> toDto(battery)).collect(Collectors.toList());
    }
}
