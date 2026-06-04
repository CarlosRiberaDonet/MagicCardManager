package com.magic.investor_api.cardmarketPrice.repository;

import com.magic.investor_api.cardmarketPrice.model.CardmarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Aquí el ID es Long porque usamos un autoincremental
public interface CardmarketPriceRepository extends JpaRepository<CardmarketPrice, Long>{
}
