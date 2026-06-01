package com.magic.investor_api.cardtrader_price_cache.repository;

import com.magic.investor_api.cardtrader_price_cache.model.CardtraderPriceCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardtraderPriceCacheRepository extends JpaRepository<CardtraderPriceCache, Long> {
}
