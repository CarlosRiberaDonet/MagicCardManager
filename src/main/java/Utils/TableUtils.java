/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
/**
 *
 * @author sovi8
 */
public class TableUtils {

    // Método para obtener el color por rareza
    public static Color getColorPorRareza(String rarity) {
        switch (rarity.toLowerCase()) {
            case "common":
                return Color.BLACK;
            case "uncommon":
                return new Color(192, 192, 192);
            case "rare":
                return new Color(255, 215, 0);
            case "mythic":
                return new Color(255, 69, 0);
            default:
                return Color.BLACK;
        }
    }

    // Renderizador de celdas para los círculos de rareza
    public static class CircleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);

            // Ajustar la alineación para que el círculo esté centrado
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);

            if (value instanceof Color) {
                label.setIcon(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        g.setColor((Color) value);

                        // Definir dimensiones más pequeñas para el círculo
                        int circleSize = 10;
                        int xPos = (c.getWidth() - circleSize) / 2;
                        int yPos = (c.getHeight() - circleSize) / 2;

                        // Dibujar el círculo centrado
                        g.fillOval(xPos, yPos, circleSize, circleSize);
                    }

                    @Override
                    public int getIconWidth() {
                        return 10; // Reducir el tamaño del círculo
                    }

                    @Override
                    public int getIconHeight() {
                        return 10; // Reducir el tamaño del círculo
                    }
                });
            }

            if (isSelected) {
                label.setBackground(new Color(173, 216, 230));  // Fondo azul claro cuando está seleccionado
            } else {
                label.setBackground(Color.WHITE);  // Fondo blanco por defecto
            }

            return label;
        }
    }

    // Método para centrar el contenido de las celdas
    public static DefaultTableCellRenderer createCenteredRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    // Método para ajustar el tamaño de las columnas de una tabla
    public static void ajustarTamañoColumnas(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = 70; // Ancho predeterminado
            int maxWidth = 400;

            // Ajustar ancho específico para la columna de rareza (columna 5)
            if (column == 5) {
                preferredWidth = 50; // Ancho específico más pequeño para la columna "Rz."
            } else {
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                    Component c = table.prepareRenderer(cellRenderer, row, column);
                    int width = c.getPreferredSize().width + table.getIntercellSpacing().width + 15;
                    preferredWidth = Math.max(preferredWidth, width);

                    if (preferredWidth >= maxWidth) {
                        preferredWidth = maxWidth;
                        break;
                    }
                }
            }
            tableColumn.setPreferredWidth(preferredWidth);
        }

        // Ajustar la altura de las filas para que sea más agradable visualmente
        table.setRowHeight(30);
    }

    // Método para aplicar el renderizador centrado a las columnas especificadas
    public static void centrarColumnas(JTable table, int[] columnas) {
        DefaultTableCellRenderer centroRenderer = createCenteredRenderer();
        for (int columna : columnas) {
            table.getColumnModel().getColumn(columna).setCellRenderer(centroRenderer);
        }
    }

    // Método para aplicar un tema visual general a la tabla
    public static void aplicarTemaVisual(JTable table) {
        table.setGridColor(Color.LIGHT_GRAY);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(173, 216, 230));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(200, 200, 200));
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        header.setPreferredSize(new Dimension(100, 35));
    }
}