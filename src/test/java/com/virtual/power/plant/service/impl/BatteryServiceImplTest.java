package com.virtual.power.plant.service.impl;

import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.BulkLoadResponseDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.SearchBatteryResponseDto;
import com.virtual.power.plant.entity.Battery;
import com.virtual.power.plant.mapper.BatteryEntityMapper;
import com.virtual.power.plant.repository.BatteryRepository;
import com.virtual.power.plant.service.BatteryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatteryServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;
    @Mock
    private BatteryEntityMapper entityMapper;

    private BatteryService batteryService;

    @BeforeEach
    void setUp() {
        batteryService = new BatteryServiceImpl(batteryRepository, entityMapper);
    }

    @Test
    void shouldThrowExceptionWhenNameNotProvided() {
        BatteryDto request = Instancio.create(BatteryDto.class);
        request.setName("");
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class, () -> batteryService.save(request));
        assertEquals("Name cannot be empty or null", argumentException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPostCodeProvided() {
        BatteryDto request = Instancio.create(BatteryDto.class);
        request.setPostcode("");
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class, () -> batteryService.save(request));
        assertEquals("Post code cannot be empty or null", argumentException.getMessage());
    }

    @Test
    void shouldSaveBatteryWhenValidArgumentsProvided() {
        BatteryDto request = Instancio.of(BatteryDto.class)
                .set(field(BatteryDto::getId), 0)
                .create();
        Battery toSaveBattery = new Battery();
        BeanUtils.copyProperties(request, toSaveBattery);

        Battery savedMockBattery = Instancio.of(Battery.class)
                .set(field(Battery::getName), request.getName())
                .set(field(Battery::getPostcode), request.getPostcode())
                .set(field(Battery::getCapacity), request.getCapacity())
                .set(field(Battery::getId), Instancio.create(Integer.class))
                .create();
        BatteryDto savedMockBatteryDto = new BatteryDto();
        BeanUtils.copyProperties(savedMockBattery, savedMockBatteryDto);

        when(entityMapper.toEntity(request)).thenReturn(toSaveBattery);
        when(batteryRepository.saveAndFlush(toSaveBattery)).thenReturn(savedMockBattery);
        when(entityMapper.toDto(savedMockBattery)).thenReturn(savedMockBatteryDto);

        BatteryDto savedBattery = batteryService.save(request);

        assertNotNull(savedBattery);
        assertNotNull(savedBattery.getId());
        assertNotNull(savedBattery.getName());
        assertNotNull(savedBattery.getPostcode());
        assertNotNull(savedBattery.getCapacity());
        assertEquals(savedMockBattery.getId(), savedBattery.getId());
        assertEquals(request.getName(), savedBattery.getName());
        assertEquals(request.getPostcode(), savedBattery.getPostcode());
        assertEquals(request.getCapacity(), savedBattery.getCapacity());
    }

    @Test
    void shouldThrowExceptionWhenEmptyListBatteryListProvided() {
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class, () -> batteryService.saveAll(Collections.emptyList()));
        assertEquals("Cannot accept empty list", argumentException.getMessage());
    }

    @Test
    void shouldPartialLoadBatteriesWhenInvalidBatteryListProvided() {
        BatteryDto request1 = Instancio.create(BatteryDto.class);
        BatteryDto request2 = Instancio.create(BatteryDto.class);
        BatteryDto request3 = Instancio.create(BatteryDto.class);
        BatteryDto request4 = Instancio.of(BatteryDto.class)
                .set(field(BatteryDto::getName), null)
                .create();
        BatteryDto request5 = Instancio.of(BatteryDto.class)
                .set(field(BatteryDto::getPostcode), null)
                .create();

        Battery toSaveBattery1 = new Battery();
        Battery toSaveBattery2 = new Battery();
        Battery toSaveBattery3 = new Battery();

        BeanUtils.copyProperties(request1, toSaveBattery1);
        BeanUtils.copyProperties(request2, toSaveBattery2);
        BeanUtils.copyProperties(request3, toSaveBattery3);

        when(entityMapper.toEntity(request1)).thenReturn(toSaveBattery1);
        when(entityMapper.toEntity(request2)).thenReturn(toSaveBattery2);
        when(entityMapper.toEntity(request3)).thenReturn(toSaveBattery3);
        when(entityMapper.toDtoList(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3))).thenReturn(List.of(request1, request2, request3));
        when(batteryRepository.saveAllAndFlush(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3))).thenReturn(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3));

        List<BatteryDto> requestList = List.of(request1, request2, request3, request4, request5);
        BulkLoadResponseDto bulkLoadResponseDto = batteryService.saveAll(requestList);

        assertNotNull(bulkLoadResponseDto);
        assertNotNull(bulkLoadResponseDto.savedList());
        assertNotNull(bulkLoadResponseDto.failedEntries());
        assertTrue(!CollectionUtils.isEmpty(bulkLoadResponseDto.savedList()));
        assertTrue(!CollectionUtils.isEmpty(bulkLoadResponseDto.failedEntries()));

        assertEquals(3, bulkLoadResponseDto.savedList().size());
        assertEquals(2, bulkLoadResponseDto.failedEntries().size());

        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request1)));
        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request2)));
        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request3)));
        assertTrue(bulkLoadResponseDto.savedList().stream().noneMatch(dto -> dto.equals(request4)));
        assertTrue(bulkLoadResponseDto.savedList().stream().noneMatch(dto -> dto.equals(request5)));

        assertTrue(bulkLoadResponseDto.failedEntries().stream().noneMatch(failedEntryWithReason -> failedEntryWithReason.entry().equals(request1)));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().noneMatch(failedEntryWithReason -> failedEntryWithReason.entry().equals(request2)));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().noneMatch(failedEntryWithReason -> failedEntryWithReason.entry().equals(request3)));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().anyMatch(failedEntryWithReason -> failedEntryWithReason.reason().equals("Name cannot be empty or null")));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().anyMatch(failedEntryWithReason -> failedEntryWithReason.reason().equals("Post code cannot be empty or null")));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().filter(failedEntryWithReason -> failedEntryWithReason.reason().equals("Name cannot be empty or null")).findFirst().get().entry().equals(request4));
        assertTrue(bulkLoadResponseDto.failedEntries().stream().filter(failedEntryWithReason -> failedEntryWithReason.reason().equals("Post code cannot be empty or null")).findFirst().get().entry().equals(request5));
    }

    @Test
    void shouldBulkLoadBatteriesWhenValidBatteryListProvided() {
        BatteryDto request1 = Instancio.create(BatteryDto.class);
        BatteryDto request2 = Instancio.create(BatteryDto.class);
        BatteryDto request3 = Instancio.create(BatteryDto.class);

        Battery toSaveBattery1 = new Battery();
        Battery toSaveBattery2 = new Battery();
        Battery toSaveBattery3 = new Battery();

        BeanUtils.copyProperties(request1, toSaveBattery1);
        BeanUtils.copyProperties(request2, toSaveBattery2);
        BeanUtils.copyProperties(request3, toSaveBattery3);

        when(entityMapper.toEntity(request1)).thenReturn(toSaveBattery1);
        when(entityMapper.toEntity(request2)).thenReturn(toSaveBattery2);
        when(entityMapper.toEntity(request3)).thenReturn(toSaveBattery3);
        when(entityMapper.toDtoList(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3))).thenReturn(List.of(request1, request2, request3));
        when(batteryRepository.saveAllAndFlush(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3))).thenReturn(List.of(toSaveBattery1, toSaveBattery2, toSaveBattery3));

        List<BatteryDto> requestList = List.of(request1, request2, request3);
        BulkLoadResponseDto bulkLoadResponseDto = batteryService.saveAll(requestList);

        assertNotNull(bulkLoadResponseDto);
        assertNotNull(bulkLoadResponseDto.savedList());
        assertNotNull(bulkLoadResponseDto.failedEntries());
        assertTrue(!CollectionUtils.isEmpty(bulkLoadResponseDto.savedList()));
        assertTrue(CollectionUtils.isEmpty(bulkLoadResponseDto.failedEntries()));

        assertEquals(3, bulkLoadResponseDto.savedList().size());
        assertEquals(0, bulkLoadResponseDto.failedEntries().size());

        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request1)));
        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request2)));
        assertTrue(bulkLoadResponseDto.savedList().stream().anyMatch(dto -> dto.equals(request3)));
    }

    @Test
    void shouldThrowExceptionWhenInvalidSearchParameterProvided() {
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class, () -> batteryService.findBatteryInRange(null, null));
        assertEquals("Cannot accept null values for filtering criterias", argumentException.getMessage());
    }

    @Test
    void shouldReturnEmptyListIfBatteriesDoNotFallOnTheProvidedPostalRange() {
        String fromPostCode = Instancio.create(String.class);
        String toPostCode = Instancio.create(String.class);

        when(batteryRepository.findBatteriesInRange(fromPostCode, toPostCode)).thenReturn(Collections.emptyList());

        SearchBatteryResponseDto searchBatteryResponseDto = batteryService.findBatteryInRange(fromPostCode, toPostCode);
        assertNotNull(searchBatteryResponseDto);
        assertTrue(CollectionUtils.isEmpty(searchBatteryResponseDto.data()));
        assertEquals(0, searchBatteryResponseDto.totalCapacity());
        assertEquals(0d, searchBatteryResponseDto.avgCapacity());
    }

    @Test
    void shouldReturnBatteryListIfFallsOnTheProvidedPostalRange() {
        String fromPostCode = Instancio.create(String.class);
        String toPostCode = Instancio.create(String.class);

        Battery battery1 = Instancio.create(Battery.class);
        Battery battery2 = Instancio.create(Battery.class);
        Battery battery3 = Instancio.create(Battery.class);
        Battery battery4 = Instancio.create(Battery.class);
        Battery battery5 = Instancio.create(Battery.class);

        List<Battery> batteries = List.of(battery1, battery2, battery3, battery4, battery5);
        int expectedSum = batteries.stream().mapToInt(Battery::getCapacity).sum();
        double expectedAvg = (double) expectedSum / batteries.size();

        when(batteryRepository.findBatteriesInRange(fromPostCode, toPostCode)).thenReturn(batteries);

        BatteryDto batteryDto1 = new BatteryDto();
        BatteryDto batteryDto2 = new BatteryDto();
        BatteryDto batteryDto3 = new BatteryDto();
        BatteryDto batteryDto4 = new BatteryDto();
        BatteryDto batteryDto5 = new BatteryDto();

        BeanUtils.copyProperties(battery1, batteryDto1);
        BeanUtils.copyProperties(battery2, batteryDto2);
        BeanUtils.copyProperties(battery3, batteryDto3);
        BeanUtils.copyProperties(battery4, batteryDto4);
        BeanUtils.copyProperties(battery5, batteryDto5);

        when(entityMapper.toDtoList(batteries)).thenReturn(List.of(batteryDto1, batteryDto2, batteryDto3, batteryDto4, batteryDto5));

        SearchBatteryResponseDto searchBatteryResponseDto = batteryService.findBatteryInRange(fromPostCode, toPostCode);
        assertNotNull(searchBatteryResponseDto);
        assertTrue(!CollectionUtils.isEmpty(searchBatteryResponseDto.data()));
        assertEquals(5, searchBatteryResponseDto.data().size());
        assertEquals(expectedSum, searchBatteryResponseDto.totalCapacity());
        assertEquals(expectedAvg, searchBatteryResponseDto.avgCapacity());

        assertTrue(searchBatteryResponseDto.data().stream().anyMatch(dto -> dto.equals(batteryDto1)));
        assertTrue(searchBatteryResponseDto.data().stream().anyMatch(dto -> dto.equals(batteryDto2)));
        assertTrue(searchBatteryResponseDto.data().stream().anyMatch(dto -> dto.equals(batteryDto3)));
        assertTrue(searchBatteryResponseDto.data().stream().anyMatch(dto -> dto.equals(batteryDto4)));
        assertTrue(searchBatteryResponseDto.data().stream().anyMatch(dto -> dto.equals(batteryDto5)));
    }
}