package com.example.webapp.repository;

import com.example.webapp.entity.PhongKham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhongKhamRepository extends JpaRepository<PhongKham, Long> {
}
