package com.virtual.power.plant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BatteryDto {
    private int id;
    private String name;
    private String postcode;
    private int capacity;

    public BatteryDto(String name, String postcode, int capacity) {
        this.name = name;
        this.postcode = postcode;
        this.capacity = capacity;
    }

    public void validate() {
        if (!StringUtils.hasText(this.name))
            throw new IllegalArgumentException("Name cannot be empty or null");

        if (!StringUtils.hasText(this.postcode))
            throw new IllegalArgumentException("Post code cannot be empty or null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BatteryDto other = (BatteryDto) o;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.postcode, other.postcode)
                && this.capacity == other.capacity
                && this.id == other.id;
    }
}
