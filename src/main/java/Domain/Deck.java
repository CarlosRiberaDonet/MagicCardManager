/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.util.Map;

/**
 *
 * @author sovi8
 */
public class Deck {
    
    private String nameDeck;
    private int copiesOwned;
    private Map<Integer, Card> userMap;     
    
    // CONSTRUCTOR
    public Deck(String nameDeck, Map<Integer, Card> userCards) {    
        this.nameDeck = nameDeck;
        this.userMap = userCards;
    }

    // GETTERS Y SETTERS
    public String getNameDeck() {
        return nameDeck;
    }

    public void setNameDeck(String nameDeck) {
        this.nameDeck = nameDeck;
    }
    
    public int getCopiesOwned(){
        return copiesOwned;
    }
    
    public void setCopiesOwned(int copiesOwned){
        this.copiesOwned = copiesOwned;
    }

    public Map<Integer, Card> getUserCards() {
        return userMap;
    }

    public void setUserCards(Map<Integer, Card> userCards) {
        this.userMap = userCards;
    }
    
}
