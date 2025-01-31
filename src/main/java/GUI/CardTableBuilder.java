/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import Controllers.CardController;
import Domain.Card;
import Utils.TableUtils;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sovi8
 */
public class CardTableBuilder {

    // Generar el modelo de tabla con columnas y datos de cartas
    public static DefaultTableModel generateCardTableModel(Map<Integer, Card> cards, CardListDialog dialog) {
        String[] columnNames = {"ID", "Name", "Edition", "Lang", "Num.", "Rarity", "Value", "Foil"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Llamar a CardController para obtener el conjunto de cartas ordenadas
        Set<Card> cardList = CardController.getSortedCards(cards);

        // Método para rellenar el modelo de tabla con todas las cartas
        cardsToTable(model, cardList);

        // Llama a este método después de que editionComboBox esté inicializado
        Set<String> editionNames = CardController.getEditionName(cards);
        dialog.updateEditionComboBox(editionNames.toArray(new String[0]));

        return model;
    }
    
    // Método para llenar el modelo de la tabla con las cartas
    public static void cardsToTable(DefaultTableModel model, Set<Card> cardSet){
        model.setRowCount(0); // Limpia el modelo antes de agregar filas
       for (Card card : cardSet){
            Object[] rowData = getRowData(card);
            model.addRow(rowData);
        }
    }

    // Obtener los datos de una fila de la carta
    public static Object[] getRowData(Card card) {

        return new Object[]{
            card.getCardId(),
            card.getName(),
            card.getSet_name(),
            card.getLang(),
            card.getCollector_number(),
            TableUtils.getColorPorRareza(card.getRarity()),
            CardController.getRegularPrice(card),
            CardController.getFoilPrice(card),
        };
    }

    // Configurar la tabla con los renderizadores y ajustes visuales
    public static void configureTableVisuals(JTable table) {
        // Aplicar el tema visual y ajustes de columnas
        TableUtils.aplicarTemaVisual(table);
        TableUtils.ajustarTamañoColumnas(table);

        // Centrar columnas específicas (lang, num, valor, foil)
        int[] columnasACentrar = {3, 4, 5, 6, 7};
        TableUtils.centrarColumnas(table, columnasACentrar);

        // Aplicar el renderizador de círculos a la columna de rareza (columna 5)
        table.getColumnModel().getColumn(5).setCellRenderer(new TableUtils.CircleRenderer());
    }
}