/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import Domain.Card;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author sovi8
 */
public class MisMetodos {
    
   public static boolean CreateFile(File filePath) {
        try {
            // Verificar si el archivo ya existe
            if (filePath.exists()) {
                return false; // El archivo ya existía
            } else {
                if (filePath.createNewFile()) {
                    return true; // Archivo creado con éxito
                } else {
                    return false; // Fallo al crear el archivo
                }
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo: " + e.getMessage());
            return false; // Ocurrió un error
        }
    }
        
   public static void iterateCards(Map<Integer, Card> cards, Consumer<Card> action) {
        for (Card card : cards.values()) {
            action.accept(card);
        }
    }   
   // Cambio el contenido de la URL para que el enlace abra la carta en su lenguaje y edición correspondiente
    public static String changeUrlLang(String url, String name, String lang, String edition) {
        // Crear un mapa de lenguajes a su correspondiente parámetro "language"
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("en", "language=1");
        languageMap.put("fr", "language=2");
        languageMap.put("de", "language=3");
        languageMap.put("es", "language=4");
        languageMap.put("it", "language=5");
        languageMap.put("zhs", "language=6");
        languageMap.put("jp", "language=7");
        languageMap.put("pt", "language=8");
        languageMap.put("ru", "language=9");
        languageMap.put("ko", "language=10");
        languageMap.put("zht", "language=11");

        // Si la URL contiene el parámetro "Search" lo reemplazo por la edición de la carta
        if (url.contains("Search?searchString=")) {
            // Cambio los espacios que contiene el String de la edición por "-", para adaptarlo a la URL
            edition = edition.replace(" ", "-");
            name = name.replace(" ", "-");

            // Reemplazo parte de la URL por la ruta correcta
            url = url.replace("Search?searchString=", "/Singles/" + edition + "/" + name);
            System.out.println("URL después de la modificación: " + url);
        }

        // Comprobar que lang no es null y que su valor esté en el mapa creado
        if (lang != null && languageMap.containsKey(lang)) {
            // Obtener el parámetro del idioma correspondiente
            String languageCard = languageMap.get(lang);

            // Modificar si la URL contiene "utm_campaign=card_prices&utm_medium=text&utm_source=scryfall"
            if (url.contains("utm_campaign=card_prices&utm_medium=text&utm_source=scryfall")) {
                // Reemplazar el parámetro "utm_campaign" por el valor de "languageCard"
                url = url.replace("utm_campaign=card_prices&utm_medium=text&utm_source=scryfall", languageCard);
                System.out.println("URL con idioma añadido: " + url);
            }
        }

        // Retornar la URL modificada
        return url;
    }
   
   public static String changeUrlEdition(String url, String edition){
       return null;
   }
   
   public static JPanel createLangBox(ActionListener updateEnglishListener, ActionListener updateSpanishListener) {
            // Crear los checkboxes para seleccionar el idioma
            JCheckBox checkBoxIngles = new JCheckBox("Inglés", true);  // Inglés seleccionado por defecto
            JCheckBox checkBoxEspanol = new JCheckBox("Español");

            // Sincronizar la selección de los checkboxes
            checkBoxIngles.addActionListener(e -> {
                checkBoxEspanol.setSelected(false); // Desmarcar español si seleccionamos inglés
                updateEnglishListener.actionPerformed(e); // Llamar al ActionListener para inglés
            });

            checkBoxEspanol.addActionListener(e -> {
                checkBoxIngles.setSelected(false); // Desmarcar inglés si seleccionamos español
                updateSpanishListener.actionPerformed(e); // Llamar al ActionListener para español
            });

            // Añadir los checkboxes a un panel
            JPanel langPanel = new JPanel();
            langPanel.add(checkBoxIngles);
            langPanel.add(checkBoxEspanol);

            return langPanel; // Devolver el panel
    }

    public static void abrirURL(String url) {
        if (url != null && !url.isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (java.io.IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "No se pudo abrir el navegador para la URL: " + url, "Error al abrir la URL", JOptionPane.ERROR_MESSAGE);
            } catch (java.net.URISyntaxException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "La URL no es válida: " + url, "Error en la URL", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "La URL no está disponible para esta carta.", "URL no disponible", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public static void getSpanishName(Card card){
        
        if (card.getLang().equals("es")){
            if (card.getPrinted_name()!= null && !card.getPrinted_name().isEmpty()){
                card.setName(card.getPrinted_name());
            }     
        }
    }
}
