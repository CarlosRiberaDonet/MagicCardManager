/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author sovi8
 */
public class ExceptionHandler {
    
     // Método genérico para mostrar diálogos
    public static void showDialog(Component parentComponent, String message, String title, int messageType) {
    JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
    }
    
    public static void showInfoDialog(Component parentComponent, String message, String title) {
    showDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

public static void showWarningDialog(Component parentComponent, String message, String title) {
    showDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
}

public static void showErrorDialog(Component parentComponent, String message, String title) {
    showDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
}

}
