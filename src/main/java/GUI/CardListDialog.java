/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import Controllers.CardController;
import Domain.Card;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *
 * @author sovi8
 */
public class CardListDialog extends JDialog {
    private final Map<Integer, Card> cardMap;
    private JTable cardTable;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> editionComboBox;
    public JLabel totalCards;

    public CardListDialog(Window parent, Map<Integer, Card> cardMap, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        
        this.cardMap = new HashMap<>(cardMap); // Copia el mapa para evitar interferencias externas
        configureWindow(parent);
        addSearchAndFilterPanel();
        configureTable(cardMap);  // Llama a configureTable con cardMap
        loadData(cardMap);        // Cargar datos en la tabla y actualizar el comboBox de ediciones
        addEditionComboBoxListener();     
        
    }

    protected void configureWindow(Window parent) {
        setLayout(new BorderLayout());
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    protected void configureTable(Map<Integer, Card> cardMap) {
        model = CardTableBuilder.generateCardTableModel(cardMap, this);
        cardTable = new JTable(model);

        sorter = new TableRowSorter<>(model);
        cardTable.setRowSorter(sorter);
        ocultarColumnaID();
        
       updateTotalCards(cardMap.size());

        JScrollPane scrollPane = new JScrollPane(cardTable);
        add(scrollPane, BorderLayout.CENTER);

        cardTable.addMouseListener(new CardTableMouseListener(cardMap, cardTable, getParentFrame()));
        CardTableBuilder.configureTableVisuals(cardTable);
    }
    
    // Método para actualizar el texto de la etiqueta totalCards
    protected void updateTotalCards(int count){
       totalCards.setText("Cartas totales: " + count);
    }

    // Método para llenar el modelo de la tabla con los datos de las cartas y actualizar el comboBox
    protected void loadData(Map<Integer, Card> cardMap) {
        model.setRowCount(0);

        String[] editions = CardController.getEditionName(cardMap).toArray(new String[0]);
        updateEditionComboBox(editions);

        CardTableBuilder.cardsToTable(model, new HashSet<>(cardMap.values()));
        updateTotalCards(cardMap.size());
    }
    
    public void refreshTable() {
        if (this.getTitle().equals("Mis Cartas")) { // Solo recarga si es "Mis Cartas"
            loadData(cardMap); // Recarga los datos en la tabla
            model.fireTableDataChanged(); // Notifica a la tabla que los datos han cambiado
        }
}
    
    // Método para actualizar el JComboBox de ediciones
    protected void updateEditionComboBox(String[] editions) {
        if (editionComboBox == null) {
            System.err.println("Error: editionComboBox no ha sido inicializado.");
            return;
        }
        editionComboBox.setModel(new DefaultComboBoxModel<>(editions));
        editionComboBox.setSelectedItem("TODAS"); // Selecciona "TODAS" como predeterminado
    }

    // Método para ocultar la columna de ID (columna 0) en la tabla
    protected void ocultarColumnaID() {
        cardTable.getColumnModel().getColumn(0).setMinWidth(0);
        cardTable.getColumnModel().getColumn(0).setMaxWidth(0);
        cardTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    }

    // Panel de búsqueda y filtros, incluyendo el comboBox de edición y totalCards
    protected void addSearchAndFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout()); // Cambia a BorderLayout
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel para los elementos de búsqueda y filtro

        JLabel searchLabel = new JLabel("Buscar Carta:");
        JTextField searchField = new JTextField(20);

        JLabel editionLabel = new JLabel("Edición:");
        editionComboBox = new JComboBox<>();

        // Inicializar la etiqueta de totalCards con margen usando EmptyBorder
        totalCards = new JLabel("Cartas totales: 0");
        totalCards.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20)); // Añade margen alrededor de totalCards

        // Buscar carta por nombre ( presionando enter)
        searchField.addActionListener(e -> {
           filterTable(searchField.getText()); 
        });
        
        //Busqueda de cartas en tiempo real, lo desactivo porque da problemas al haber un gran volumen de cartas
       /* searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
        });*/

        // Añadir el comportamiento del comboBox de edición
        editionComboBox.addActionListener(e -> {
            String selectedEdition = (String) editionComboBox.getSelectedItem();
            filterByEdition(selectedEdition);
        });

        // Añadir los componentes al searchPanel (parte izquierda)
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(editionLabel);
        searchPanel.add(editionComboBox);

        // Añadir searchPanel al lado izquierdo del filterPanel
        filterPanel.add(searchPanel, BorderLayout.WEST);
        
        // Añadir totalCards al lado derecho del filterPanel
        filterPanel.add(totalCards, BorderLayout.EAST);

        // Añadir el filterPanel a la parte superior de CardListDialog
        add(filterPanel, BorderLayout.NORTH);
    }

    protected void filterTable(String query) {
        sorter.setRowFilter(query.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + query, 1));
    }

    protected void addEditionComboBoxListener() {
        editionComboBox.addActionListener(e -> {
            String selectedEdition = (String) editionComboBox.getSelectedItem();
            filterByEdition(selectedEdition);
        });
    }

    protected void filterByEdition(String selectedEdition) {
        if (model == null) {
            return;
        }

        model.setRowCount(0); // Limpia el modelo antes de cargar los datos filtrados
        AtomicInteger count = new AtomicInteger(0);

        // Filtra el mapa local
        if ("TODAS".equals(selectedEdition)) {
            // Mostrar todas las cartas del mapa local
            cardMap.values().forEach(card -> {
                model.addRow(CardTableBuilder.getRowData(card));
                count.incrementAndGet();
            });
        } else {
            // Mostrar solo cartas de la edición seleccionada
            cardMap.values().forEach(card -> {
                if (card.getSet_name().equals(selectedEdition)) {
                    model.addRow(CardTableBuilder.getRowData(card));
                    count.incrementAndGet();
                }
            });
        }

        updateTotalCards(count.get()); // Actualiza el total de cartas visibles
    }

    // Obtener el JFrame desde el componente actual usando getParent()
    protected JFrame getParentFrame() {
        Component parent = this.getParent();
        while (!(parent instanceof JFrame) && parent != null) {
            parent = parent.getParent();
        }
        return (JFrame) parent;
    }
}