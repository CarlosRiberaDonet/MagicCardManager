package com.magic.investor_api.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.magic.investor_api.model.CardPrice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CardPriceMapper {

    public List<CardPrice> mapNodeToCardPrice(JsonNode node) {

        List<CardPrice> prices = new ArrayList<>();

        CardPrice newPrice = new CardPrice();
        newPrice.setCardmarketId(node.path("idProduct").asLong());
        newPrice.setAvg(node.path("avg").decimalValue());
        newPrice.setLow(node.path("low").decimalValue());
        newPrice.setTrend(node.path("trend").decimalValue());
        newPrice.setAvg1(node.path("avg1").decimalValue());
        newPrice.setAvg7(node.path("avg7").decimalValue());
        newPrice.setAvg30(node.path("avg30").decimalValue());
        newPrice.setAvgFoil(node.path("avg_foil").decimalValue());
        newPrice.setLowFoil(node.path("low_foil").decimalValue());
        newPrice.setTrendFoil(node.path("trend_foil").decimalValue());
        newPrice.setAvg1Foil(node.path("avg1_foil").decimalValue());
        newPrice.setAvg7Foil(node.path("avg7_foil").decimalValue());
        newPrice.setAvg30Foil(node.path("avg30_foil").decimalValue());

        newPrice.setUpdatedAt(LocalDateTime.now());

        prices.add(newPrice);

        return prices;
    }
}
