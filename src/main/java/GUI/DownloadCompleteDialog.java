/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;
import java.awt.FlowLayout;
import javax.swing.*;

/**
 *
 * @author sovi8
 */
public class DownloadCompleteDialog extends JDialog {
    public DownloadCompleteDialog(JFrame parent, double megabytesDownloaded) {
        super(parent, "Descarga Completa", true);
        setLayout(new FlowLayout());

        JLabel messageLabel = new JLabel(String.format("Descarga completa: %.2f MB", megabytesDownloaded));
        add(messageLabel);

        JButton okButton = new JButton("Aceptar");
        okButton.addActionListener(e -> dispose());
        add(okButton);

        pack();
        setLocationRelativeTo(parent);
    }
}
