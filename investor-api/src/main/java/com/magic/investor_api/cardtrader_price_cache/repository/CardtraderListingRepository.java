package com.magic.investor_api.cardtrader_price_cache.repository;

import com.magic.investor_api.cardtrader_price_cache.model.CardtraderListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;;

@Repository
public interface CardtraderListingRepository extends JpaRepository<CardtraderListing, Long> {
}
