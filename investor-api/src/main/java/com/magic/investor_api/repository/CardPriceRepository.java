package com.magic.investor_api.repository;

import com.magic.investor_api.model.CardPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Aquí el ID es Long porque usamos un autoincremental
public interface CardPriceRepository extends JpaRepository<CardPrice, Long>{
}
