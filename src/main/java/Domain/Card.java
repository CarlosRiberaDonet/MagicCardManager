/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author sovi8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    // Id de la base de datos de oracla
    @JsonProperty("multiverse_ids")
    private List<Integer> multiverse_ids;
    // Id generado por mi aplicación
    private int cardId;
    // Nombre de la carta en inglés
    private String name;
    // Nombre que está impreso originalmente en la carta
    private String printed_name;
    // Nombre de edición
    private String set_name;
    // Idioma
    private String lang;
    // Variable para verificar si la carta dispone de versión FOIL
    private boolean foil;
    private String rarity;
    private String collector_number;
    private Map<String, String> prices;    
    private String newUrl;
    @JsonProperty("purchase_uris") // Mapea el campo desde el JSON
    private Map<String, String> purchaseUris; // Map para las url
    @JsonProperty("image_uris")
    private Map<String, String> imageUris; // Mapea todas las URLs de las imágenes

    // Getters y Setters

    public List<Integer> getMultiverse_ids() {
        return multiverse_ids;
    }

    public void setMultiverse_ids(List<Integer> multiverse_ids) {
        this.multiverse_ids = multiverse_ids;
    }
    
    public int getCardId() {
        return cardId;
    }
    
    public void setCardId(int id) {
        this.cardId = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrinted_name() {
        return printed_name;
    }

    public void setPrinted_name(String printed_name) {
        this.printed_name = printed_name;
    }

    public String getSet_name() {
        return set_name;
    }

    public void setSet_name(String set_name) {
        this.set_name = set_name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public boolean isFoil() {
        return foil;
    }

    public void setFoil(boolean foil) {
        this.foil = foil;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getCollector_number() {
        return collector_number;
    }

    public void setCollector_number(String collector_number) {
        this.collector_number = collector_number;
    }

    public Map<String, String> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, String> prices) {
        this.prices = prices;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public Map<String, String> getPurchaseUris() {
        return purchaseUris;
    }

    public void setPurchaseUris(Map<String, String> purchaseUris)  {
        this.purchaseUris = purchaseUris;
    }

    public String getImageUrl() {
        return imageUris != null ? imageUris.get("normal") : null;
    }
    
    public void setImageUrl(String imageUrl) {
        if (imageUris == null) {
            imageUris = new HashMap<>(); // Inicializar si es null
        }
        imageUris.put("normal", imageUrl); // Asignar la URL en el mapa
    }
}
