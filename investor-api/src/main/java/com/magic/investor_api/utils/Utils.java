package com.magic.investor_api.utils;

public class Utils {

    public enum CardCondition {

        M("Mint"),
        NM("Near Mint"),
        EX("Excellent"),
        GD("Good"),
        LP("Slightly Played"),
        PL("Moderately Played"),
        PO("Poor");

        private final String cardTraderValue;

        CardCondition(String cardTraderValue) {
            this.cardTraderValue = cardTraderValue;
        }

        public String getCardTraderValue() {
            return cardTraderValue;
        }
    }
}
