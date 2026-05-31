package com.magic.investor_api.cardtrader.repository;

import com.magic.investor_api.cardtrader.model.CardtraderCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardtraderRepository extends JpaRepository<CardtraderCard, Long>{

}
