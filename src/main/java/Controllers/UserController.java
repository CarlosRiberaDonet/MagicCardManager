/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Domain.Card;
import Utils.MisMetodos;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sovi8
 */
public class UserController {
    
    public static String se = File.separator;
    public static String path = System.getProperty("user.dir");
    public static File userJson = new File(path + se + "userCards.json");
    public static Map<Integer, Card> cardMap = new HashMap<>();
    public static boolean flag = false; // Utilizo esta variable para saber si tengo que actualizar el userJson (se actualizará si flag = true)
    
    // Método para añadir una carta al userMap
    public static void addCardToMap(Card card){
        
        try{
            if(!cardMap.containsKey(card.getCardId())){
                cardMap.put(card.getCardId(), card);
                flag = true;
            }           
        } catch (Exception e){
             e.printStackTrace();
             System.err.println("Error al intentar añadir la carta a la lista: " + e.getMessage());
        }
    }
    
    public static void removeCardFromMap(Card card){
               
        try{
            if(cardMap.containsKey(card.getCardId())){
                cardMap.remove(card.getCardId());
                System.out.println("Carta eliminada correctamente: " + card.getName());
                flag = true;
            }
            
        } catch(Exception e){
            System.err.println("Error al intentar eliminar la carta de la lista: " + e.getMessage());
        }
    }
    
    public static void addMapToJson(Map<Integer, Card> cardMap){
        ObjectMapper mapper = new ObjectMapper();
        
        
        if(!userJson.exists()){
            MisMetodos.CreateFile(userJson);
        }
        try{
            mapper.writeValue(userJson, cardMap);
        } catch(Exception e){
            e.printStackTrace();
        }
    }   

}
