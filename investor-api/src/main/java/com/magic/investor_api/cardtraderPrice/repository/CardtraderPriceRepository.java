package com.magic.investor_api.cardtraderPrice.repository;

import com.magic.investor_api.cardtraderPrice.model.CardtraderPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardtraderPriceRepository extends JpaRepository<CardtraderPrice, Long> {
}
