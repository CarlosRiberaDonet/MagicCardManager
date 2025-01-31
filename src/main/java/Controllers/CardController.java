/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Domain.Card;
import Utils.MisMetodos;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author sovi8
 */

public class CardController {
    
    public static String se = File.separator;
    public static final String path = System.getProperty("user.dir");
    public static final File json = new File(path + se + "/cards.json"); // // JSON original
    public static final File newJson = new File(path + se + "/newCards.json"); // // Nuevo JSON generado
    // HashMap que almacena temporalmente las cartas originales del Json original
    public static Map<Integer, Card> cardMap;
    // HashMap que almacenará las cartas modificadas del Json original
    public static Map<Integer, Card> finalMap = new HashMap<>();
 
   public static Map<Integer, Card> cardReaderToJackson(File filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Integer, Card> cardMap = new HashMap<>();

        try {
            // Intentar deserializar como una lista
            List<Card> cards = objectMapper.readValue(filePath, objectMapper.getTypeFactory().constructCollectionType(List.class, Card.class));
            for (Card card : cards) {
                if (card.getMultiverse_ids() != null && !card.getMultiverse_ids().isEmpty()) {
                    card.setCardId(card.getMultiverse_ids().get(0));
                } else {
                    card.setCardId(generatedId(card.getName(), card.getSet_name(), card.getLang(), card.getCollector_number()));
                }
                cardMap.put(card.getCardId(), card);
            }
        } catch (MismatchedInputException e) {
            // Si falla como lista, intentar deserializar como mapa
            try {
                cardMap = objectMapper.readValue(filePath, new TypeReference<Map<Integer, Card>>() {});
            } catch (IOException ex) {
                System.err.println("Error al leer el archivo JSON como mapa: " + ex.getMessage());
                ex.printStackTrace();
                throw new IOException("El archivo JSON no tiene el formato esperado (ni lista ni mapa).");
            }
        }

        return cardMap;
    }

     
    public static void optimizeJSON() throws IOException {
        
        // Leer el JSON original y obtener el HashMap
        cardMap = cardReaderToJackson(json);
        newJSON(cardMap);
    }
    
    public static void newJSON(Map<Integer, Card> cards) {
        ObjectMapper mapper = new ObjectMapper();
        //List<Card> newCardList = new ArrayList<>();
        
        for (Card c : cards.values()) {
             
            Card newCard = new Card(); // Crear y copiar la carta

            newCard.setCardId(c.getCardId());           
            MisMetodos.getSpanishName(c); // Si puede, le asigna el nombre en español
            newCard.setName(c.getName()); // Aseguramos de que se use el nombre actualizado (sea en español o inglés)
            newCard.setSet_name(c.getSet_name());
            newCard.setLang(c.getLang());           
            newCard.setFoil(c.isFoil()); // Muestra si tiene versión Foil = true, si no tiene = false
            newCard.setRarity(c.getRarity());
            newCard.setCollector_number(c.getCollector_number());
            newCard.setPrices(c.getPrices());
            newCard.setNewUrl(getUrl(c));
            newCard.setImageUrl(c.getImageUrl());
            
            // Agregar la nueva carta a finalMap
            finalMap.put(c.getCardId(), newCard);
        }
        // Escribir el nuevo JSON en un archivo
        try {
            mapper.writeValue(newJson, finalMap);
            // Establecer cardMap en null para que no retenga datos en memoria
            cardMap = null;
            // Eliminar el archivo JSON original
            json.delete();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al escribir el nuevo JSON: " + e.getMessage());
        }
    }
     
   public static Map<Integer, Card> loadCards(File file)throws IOException {
       
       
       if (file.equals(CardController.newJson)){
          return CardController.cardMap = cardReaderToJackson(file);       
       }
       else {
           return UserController.cardMap = cardReaderToJackson(file);
       }
    }
    
   public static Card searchCard(String nameCard, boolean lang) {    

        // Verificar si las cartas fueron cargadas correctamente
        if (cardMap != null && !cardMap.isEmpty()) {
            // Recorrer el Map para buscar la carta por nombre
            for (Card card : cardMap.values()) {
                // Verificar el idioma que ha seleccionado el USR
                if (lang){
                    //comprobar que la carta tiene valor en el campo name y que coincida con el introducido por el USR
                    if (card.getName() != null && card.getName().equalsIgnoreCase(nameCard)){
                        System.out.println("Carta encontrada en inglés" + card);
                        return card;                       
                    }
                    } else {
                    if (card.getPrinted_name() != null && card.getPrinted_name().equalsIgnoreCase(nameCard)){
                        System.out.println("Carta encontrada en Español" + card);
                        return card;
                    }
                }                   
            }
        }
        return null;
    }  
   
    public static Set<Card> getSortedCards(Map<Integer, Card> cardMap){
       // Utilizar un TreeSet para ordenar automáticamente las cartas por nombre
       Set<Card> sortedCards = new TreeSet<>(Comparator.comparing(Card::getCardId));
       sortedCards.addAll(cardMap.values()); // Llenar el TreeSet con las cartas de cardMap
       return sortedCards;
   }
   
    public static Set<String> getEditionName(Map<Integer, Card> cardMap){
       // Utilizar un TreeSet para almacenar los nombres de las ediciones en orden alfabético
       Set<String> sortedEditionNames = new TreeSet<>();
       sortedEditionNames.add("TODAS"); // Opción para ver todas las cartas
       // Recorrer las cartas y añadir los nombres de las ediciones al TreeSet
       MisMetodos.iterateCards(cardMap, card -> {
           sortedEditionNames.add(card.getSet_name());
       });
       
       return sortedEditionNames; // Devolver el TreeSet de ediciones ordenadas
   }

    public static Card getCard(Integer cardId, boolean isUserMap){
        Card cardSelect;
        
        // Si isUserMap = false, carga el cardMap, si no, carga el userCardMap
        if(!isUserMap){
            // Buscar la carta en el Map de CardController
            cardSelect = CardController.cardMap.get(cardId);

            // Si encuentra el cardId devuelve la carta
            if (cardSelect != null){
                return cardSelect;
            }          
        }
        else {
            cardSelect = UserController.cardMap.get(cardId);           
        }
        return cardSelect;
    }
    
    private static int generatedId(String name, String setName, String lang, String collectorNumber){
    
        // Concatenar los atributos en un String y luego calcular su hashCode para obtener un int
        String newId =  name + "_" + setName + "_" + lang + "_" + collectorNumber;
        int hash = newId.hashCode();
        return Math.abs(hash);  // Asegurar que el ID sea siempre positivo
    }
     
    public static String getUrl(Card card) {
        // Verificar que los URIs de compra y el idioma de la carta no sean nulos
        if (card.getPurchaseUris() != null) {
            // Verificar y modificar la URL de CardMarket si está disponible y el idioma no es nulo
            if (card.getPurchaseUris().get("cardmarket") != null && card.getLang() != null) {
                return MisMetodos.changeUrlLang(card.getPurchaseUris().get("cardmarket"),card.getName(), card.getLang(), card.getSet_name());
            }
            // Si no, usar la URL de TCGPlayer si está disponible
            if (card.getPurchaseUris().get("tcgplayer") != null) {
                return card.getPurchaseUris().get("tcgplayer");
            }
            // Finalmente, si no están disponibles las anteriores, usa la URL de Cardhoarder
            if (card.getPurchaseUris().get("cardhoarder") != null) {
                return card.getPurchaseUris().get("cardhoarder");
            }
        }
        // Si no hay URLs válidas, retornar una cadena vacía
        return "";
    }
    
    // Método para precios
    public static String getRegularPrice(Card card){
        
        if (card.getPrices() != null){
            if(card.getPrices().get("eur") != null){
                return card.getPrices().get("eur") + "€";
            }else if(card.getPrices().get("usd") != null){
                return card.getPrices().get("usd") + "$";
            }
        }
        return "N/A";
    }
    // Método para precios Foil
    public static String getFoilPrice(Card card){
        
        if (card.getPrices() != null){
            if(card.getPrices().get("eur_foil") != null){
                return card.getPrices().get("eur_foil") + "€";
            }else if ( card.getPrices().get("usd_foil") != null ){
                return card.getPrices().get("usd_foil") + "$";
            }
        }
        return "N/A";
    }
    
        
    /* public static Card findCardByName( Map<Integer, Card> cardList, Integer cardName) {
        final Card[] foundCard = {null}; // Para almacenar el resultado
        MisMetodos.iterateCards(cardList, card -> {
            if (card.getName().equalsIgnoreCase(cardName)) {
                foundCard[0] = card; // Si coincide, la almacena
            }
        });
        return foundCard[0]; // Devuelve la carta encontrada, o null si no existe
    }*/
    

    // Elimina objetos Card que tengan duplicada la url de cardmarket
   /*public static void removeDuplicatesByUrl( Map<Integer, Card> cardMap) {
        Set<String> seenUrls = new HashSet<>();
        List<Integer> keysToRemove = new ArrayList<>(); // Lista para almacenar claves a eliminar

        for (Map.Entry<Integer, Card> entry : cardMap.entrySet()) {
            Card card = entry.getValue();
            PurchaseUris purchaseUris = card.getPurchaseUris();

            // Verificar si purchaseUris es null y marcar la carta para eliminar si no tiene URLs válidas
            if (purchaseUris == null || 
                (purchaseUris.getCardmarket() == null && purchaseUris.getTcgplayer() == null)) {
                keysToRemove.add(entry.getKey()); // Añadir clave a la lista
                continue; // Saltar a la siguiente carta
            }

            // Comprobar la URL de CardMarket
            String cardMarketUrl = purchaseUris.getCardmarket();
            if (cardMarketUrl != null && !cardMarketUrl.isEmpty()) {
                if (seenUrls.contains(cardMarketUrl)) {
                    keysToRemove.add(entry.getKey()); // Añadir clave a la lista
                } else {
                    cardMarketUrl = MisMetodos.changeUrl(cardMarketUrl, card.getLang());
                    seenUrls.add(cardMarketUrl); // Agregar al conjunto si es nueva
                    System.out.println("Lang " + card.getLang());
                }
            }

            // Comprobar la URL de TCGPlayer
            String tcgPlayerUrl = purchaseUris.getTcgplayer();
            if (tcgPlayerUrl != null && !tcgPlayerUrl.isEmpty()) {
                if (seenUrls.contains(tcgPlayerUrl)) {
                    keysToRemove.add(entry.getKey()); // Añadir clave a la lista
                } else {
                    seenUrls.add(tcgPlayerUrl); // Agregar al conjunto si es nueva
                }
            }
        }
        // Eliminar las claves marcadas del cardMap
        for (Integer key : keysToRemove) {
            cardMap.remove(key);
        }
    }*/
   
}