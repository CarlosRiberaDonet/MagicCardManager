/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import Controllers.CardController;
import Utils.MisMetodos;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author sovi8
 */
public class CardSearchFrame extends JDialog {
    
    // Declaración de componentes
    JButton searchButton = new JButton("Buscar");
    JTextField nameField = new JTextField(20);
    JLabel nameLabel = new JLabel("Nombre:");
    JPanel bottomPanel = new JPanel(new BorderLayout());
    boolean language = true; // Variable para el idioma seleccionado (true = inglés, false = español)
    // Constructor
    public CardSearchFrame(Window parent) {
        super(parent, "Buscar Cartas", ModalityType.APPLICATION_MODAL);
        
        // Panel superior con la etiqueta y el campo de texto
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(nameLabel);
        topPanel.add(nameField);
        
        // Añadir el topPanel a la parte superior (NORTH)
        add(topPanel, BorderLayout.NORTH);
        
         // Añadir el panel de checkboxes para el idioma
         JPanel langPanel = MisMetodos.createLangBox(
                 e -> language = true,  // Si selecciona inglés
                 e -> language = false  // Si selecciona español
         );
        add(langPanel, BorderLayout.CENTER);
        
        // Añadir el botón de buscar en la parte derecha (SOUTH)
        bottomPanel.add(searchButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Tamaño y visibilidad
        setSize(400, 200);
        setLocationRelativeTo(parent);  
        
        searchButton.addActionListener(e -> {
            String nameSearch = nameField.getText().toLowerCase();
                   
            if (nameSearch.isBlank()){
                    JOptionPane.showMessageDialog(this, "Debe introducir un nombre de carta.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                CardController.searchCard(nameSearch, language);
            }
        });
    }
}
