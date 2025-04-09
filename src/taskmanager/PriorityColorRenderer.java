package taskmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PriorityColorRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        //  CUSTOM IMPLEMENTATION 
        // Color-coding for task priorities (High=Red, Medium=Orange, Low=Green)
        // Designed for intuitive user experience and for visualization 
        if (column == 1) {
            String priority = (String) value;
            switch (priority) {
                case "High":
                    cell.setForeground(Color.RED);
                    break;
                case "Medium":
                    cell.setForeground(Color.ORANGE);
                    break;
                case "Low":
                    cell.setForeground(Color.GREEN);
                    break;
                default:
                    cell.setForeground(Color.BLACK);
            }
        } else {
            cell.setForeground(Color.BLACK); 
        }

        return cell;
    }
}