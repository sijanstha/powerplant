package com.virtual.power.plant.repository;

import com.virtual.power.plant.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Integer> {

    @Query("SELECT b from Battery b where b.postcode >= :fromPostCode and b.postcode <= :toPostCode order by b.name asc")
    List<Battery> findBatteriesInRange(String fromPostCode, String toPostCode);
}
