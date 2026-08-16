package com.magic.investor_api.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public enum CardCondition {

        M("Mint"),
        NM("Near Mint"),
        EX("Slightly Played"),
        GD("Moderately Played"),
        LP("Played"),
        PL("Heavily Played"),
        PO("Poor");

        private final String cardTraderValue;

        CardCondition(String cardTraderValue) {
            this.cardTraderValue = cardTraderValue;
        }

        public String getCardTraderValue() {
            return cardTraderValue;
        }

        // Mapa invertido: "Near Mint" -> NM. Se calcula una sola vez al cargar la clase.
        private static final Map<String, CardCondition> BY_CARDTRADER_VALUE =
                Arrays.stream(values())
                        .collect(Collectors.toMap(CardCondition::getCardTraderValue, c -> c));

        public static CardCondition fromCardTraderValue(String value) {
            CardCondition condition = BY_CARDTRADER_VALUE.get(value);
            if (condition == null) {
                throw new IllegalArgumentException("Condición de CardTrader desconocida: " + value);
            }
            return condition;
        }
    }
}