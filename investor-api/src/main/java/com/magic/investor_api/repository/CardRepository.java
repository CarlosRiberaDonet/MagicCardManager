package com.magic.investor_api.repository;

import com.magic.investor_api.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// El primer parámetro es la Entidad (Card)
// El segundo es el tipo de su ID (String, porque es el UUID de Scryfall)
public interface CardRepository extends JpaRepository<Card, String> {
}
