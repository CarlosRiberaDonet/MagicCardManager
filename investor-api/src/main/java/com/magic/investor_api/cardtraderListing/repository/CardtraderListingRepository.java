package com.magic.investor_api.cardtraderListing.repository;

import com.magic.investor_api.cardtraderListing.model.CardtraderListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;;

@Repository
public interface CardtraderListingRepository extends JpaRepository<CardtraderListing, Long> {
}
