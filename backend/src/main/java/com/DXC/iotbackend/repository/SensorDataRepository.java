package com.DXC.iotbackend.repository;


import com.DXC.iotbackend.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorData, Long>
{
}

//By extending JpaRepository, I automatically get:
//save()
//findAll()
//findById()
//delete()
//count() with no need to write sql queries

