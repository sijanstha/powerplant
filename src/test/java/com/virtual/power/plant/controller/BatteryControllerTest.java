package com.virtual.power.plant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtual.power.plant.dto.BatteryDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.BulkLoadResponseDto;
import com.virtual.power.plant.dto.ResponseRecordsDto.FailedEntryWithReason;
import com.virtual.power.plant.dto.ResponseRecordsDto.SearchBatteryResponseDto;
import com.virtual.power.plant.service.BatteryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BatteryController.class)
class BatteryControllerMockMvcTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BatteryService batteryService;

    @Test
    public void shouldReturn400BadRequestWhenEmptyListProvided() throws Exception {
        var request = new ArrayList<BatteryDto>();

        when(batteryService.saveAll(request)).thenThrow(new IllegalArgumentException("Cannot accept empty list"));

        mvc.perform(post("/api/battery/bulk/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.body").isEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message").value("Cannot accept empty list"));
    }

    @Test
    public void shouldReturn200SuccessWhenValidListProvided() throws Exception {
        var request = List.of(Instancio.create(BatteryDto.class),
                Instancio.create(BatteryDto.class),
                Instancio.create(BatteryDto.class),
                Instancio.create(BatteryDto.class));

        when(batteryService.saveAll(request)).thenReturn(new BulkLoadResponseDto(request, Collections.emptyList()));

        mvc.perform(post("/api/battery/bulk/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.body").isNotEmpty())
                .andExpect(jsonPath("$.body").isMap())
                .andExpect(jsonPath("$.body.savedList").isArray())
                .andExpect(jsonPath("$.body.savedList", hasSize(4)))
                .andExpect(jsonPath("$.body.savedList", hasToString(objectMapper.writeValueAsString(request))))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Data loaded successfully"));
    }

    @Test
    public void shouldReturn200SuccessWhenPartialValidListProvided() throws Exception {
        var battery1 = Instancio.create(BatteryDto.class);
        var battery2 = Instancio.create(BatteryDto.class);
        var battery3 = Instancio.create(BatteryDto.class);
        var battery4 = Instancio.create(BatteryDto.class);
        var battery5 = Instancio.create(BatteryDto.class);

        battery3.setName("");
        battery4.setPostcode("");

        var request = List.of(battery1, battery2, battery3, battery4, battery5);

        List<FailedEntryWithReason> failedEntries = List.of(new FailedEntryWithReason("Name cannot be empty or null", battery4),
                new FailedEntryWithReason("Post code cannot be empty or null", battery5));
        List<BatteryDto> savedList = List.of(battery1, battery2, battery3);
        when(batteryService.saveAll(request)).thenReturn(new BulkLoadResponseDto(savedList, failedEntries));

        mvc.perform(post("/api/battery/bulk/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.body").isNotEmpty())
                .andExpect(jsonPath("$.body").isMap())
                .andExpect(jsonPath("$.body.savedList").isArray())
                .andExpect(jsonPath("$.body.savedList", hasSize(3)))
                .andExpect(jsonPath("$.body.savedList", hasToString(objectMapper.writeValueAsString(savedList))))
                .andExpect(jsonPath("$.body.failedEntries").isArray())
                .andExpect(jsonPath("$.body.failedEntries", hasSize(2)))
                .andExpect(jsonPath("$.body.failedEntries", hasToString(objectMapper.writeValueAsString(failedEntries))))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Partial data loaded"));
    }

    @Test
    public void shouldReturn200SuccessWithEmptyListWhenSearchedByInvalidPostRange() throws Exception {
        var fromPostCode = Instancio.create(String.class);
        var toPostCode = Instancio.create(String.class);

        when(batteryService.findBatteryInRange(fromPostCode, toPostCode)).thenReturn(new SearchBatteryResponseDto(Collections.emptyList(), 0, 0, 0));

        mvc.perform(get("/api/battery")
                        .param("from_post_code", fromPostCode)
                        .param("to_post_code", toPostCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.body").isNotEmpty())
                .andExpect(jsonPath("$.body.data").isArray())
                .andExpect(jsonPath("$.body.data", hasSize(0)))
                .andExpect(jsonPath("$.body.totalCapacity").value(0))
                .andExpect(jsonPath("$.body.avgCapacity").value(0.0))
                .andExpect(jsonPath("$.body.totalCount").value(0))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Fetched successfully"));
    }

    @Test
    public void shouldReturn200SuccessWithQualifiedListWhenSearchedByValidPostRange() throws Exception {
        var fromPostCode = Instancio.create(String.class);
        var toPostCode = Instancio.create(String.class);

        BatteryDto batteryDto1 = Instancio.create(BatteryDto.class);
        BatteryDto batteryDto2 = Instancio.create(BatteryDto.class);
        int sum = batteryDto1.getCapacity() + batteryDto2.getCapacity();
        double avg = (double) sum / 2;
        when(batteryService.findBatteryInRange(fromPostCode, toPostCode)).thenReturn(new SearchBatteryResponseDto(List.of(batteryDto1, batteryDto2), sum, avg, 2));

        mvc.perform(get("/api/battery")
                        .param("from_post_code", fromPostCode)
                        .param("to_post_code", toPostCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.body").isNotEmpty())
                .andExpect(jsonPath("$.body.data").isArray())
                .andExpect(jsonPath("$.body.data", hasSize(2)))
                .andExpect(jsonPath("$.body.data", hasToString(objectMapper.writeValueAsString(List.of(batteryDto1, batteryDto2)))))
                .andExpect(jsonPath("$.body.totalCapacity").value(sum))
                .andExpect(jsonPath("$.body.avgCapacity").value(avg))
                .andExpect(jsonPath("$.body.totalCount").value(2))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Fetched successfully"));
    }
}
