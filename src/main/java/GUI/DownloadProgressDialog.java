/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import API.MtgApi;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *
 * @author sovi8
 */
    public class DownloadProgressDialog extends JDialog {
    private JButton acceptButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private AtomicBoolean cancelRequested = new AtomicBoolean(false);

   public DownloadProgressDialog(JFrame parent) {
    super(parent, "Magic: The Gathering", true);
    setLayout(new BorderLayout());

    statusLabel = new JLabel("...");
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(statusLabel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    acceptButton = new JButton("Iniciar");
    acceptButton.addActionListener(e -> startDownload());
    buttonPanel.add(acceptButton);

    cancelButton = new JButton("Cancelar");
    cancelButton.addActionListener(e -> cancelDownload());
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);

    setSize(300, 150);
    setLocationRelativeTo(parent);
}

    private void startDownload() {
        acceptButton.setEnabled(false); // Desactiva el botón para evitar múltiples clics
        cancelRequested.set(false); // Resetea el estado de cancelación

        new Thread(() -> {
            MtgApi mtgApi = new MtgApi();
            try {
                mtgApi.downloadJson("cards.json", cancelRequested, this);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Descarga completa.");
                    dispose(); // Cierra el diálogo
                });
            }
        }).start();
    }

    private void cancelDownload() {
        cancelRequested.set(true); // Indica que se debe cancelar la descarga
        acceptButton.setEnabled(true); // Reactiva el botón de inicio
        statusLabel.setText("Descarga cancelada."); // Muestra que la descarga fue cancelada
        dispose();
    }
    
    public void actualizarProgreso(double megabytesDescargados) {
    SwingUtilities.invokeLater(() -> statusLabel.setText(String.format("Descargado: %.2f MB", megabytesDescargados)));
}

    public void notificarDescargaCompleta(double megabytesDescargados) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, String.format("Descarga completa: %.2f MB", megabytesDescargados));
            dispose(); // Cierra el diálogo de progreso
        });
    }
}