package com.magic.investor_api.repository;

import com.magic.investor_api.model.ScryfallCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// El primer parámetro es la Entidad (Card)
// El segundo es el tipo de su ID (String, porque es el UUID de Scryfall)
public interface ScryfallRepository extends JpaRepository<ScryfallCard, String> {

}
