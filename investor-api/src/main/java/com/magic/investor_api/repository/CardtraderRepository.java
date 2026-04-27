package com.magic.investor_api.repository;

import com.magic.investor_api.model.CardtraderCard;
import com.magic.investor_api.model.ScryfallCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardtraderRepository extends JpaRepository<CardtraderCard, String>{
    CardtraderCard findByCardmarketId(Long cardmarketId);
}
