/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import Domain.Card;
import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author sovi8
 */
public class CardDetailDialog extends JDialog {

    public CardDetailDialog(JFrame parent, Card card) {
        super(parent, "Detalles de la Carta", true);

        // Configuración de la ventana de diálogo
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(parent);

        // Crear el constructor de detalles de carta
        CardDetailBuilder builder = new CardDetailBuilder(card);

        // Añadir los paneles al diálogo
        add(builder.createImagePanel(), BorderLayout.WEST);
        add(builder.createRightPanel(), BorderLayout.CENTER);

        // Botón de cierre de diálogo
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }

    // Método estático para mostrar el diálogo con la referencia a CardListDialog
    public static void showCardDetail(JFrame parent, CardListDialog cardListDialog, Map<Integer, Card> cardMap, Integer cardId) {
        Card cardSelect = cardMap.get(cardId);
        if (cardSelect != null) {
            CardDetailDialog dialog = new CardDetailDialog(parent, cardSelect);
            dialog.setVisible(true);
        }
    }
}

