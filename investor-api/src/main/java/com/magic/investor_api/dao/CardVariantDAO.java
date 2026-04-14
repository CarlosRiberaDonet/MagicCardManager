package com.magic.investor_api.dao;

import com.magic.investor_api.dto.CardVariantDTO;
import com.magic.investor_api.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CardVariantDAO {

    @Autowired
    private DataSource dataSource;
    private final CardDAO cardDAO;

    public CardVariantDAO(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    // Insertar carta en la tabla card_variant
    public void insertCardVariant(CardVariantDTO cardVariantDTO){

        // Obtengo los id, cardmarket_id y scryfall_id de la tabla card
        List<Card> cardList = cardDAO.getCardsIds();

        String INSERT_CARD_VARIANT = "INSERT INTO card_variant(card_id, cardtrader_id, " +
                "cardmarket_id, scryfall_id, expansion_id, " +
                "version, collector_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_CARD_VARIANT)){

            stmt.setLong(1, cardVariantDTO.getId()); // BluePrint de card trader
            //stmt.setLong();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
