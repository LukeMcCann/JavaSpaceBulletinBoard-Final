package util.helper;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * CellWrapRenderer -
 *                 A custom renderer for rendering "pretty" JTable cells
 *                 this renderer uses Word style wrapping.
 *
 * Simply create new instance to utilise:
 *          setCellRenderer(new CellWrapRenderer());
 *
 * Can be applied to columns with:
 *          table.getColumnModel().getColumn(colNum).setCellRenderer(new CellWrapRenderer());
 */
public class CellWrapRenderer extends JTextArea implements TableCellRenderer
{
    // Constructor(needed)
    public CellWrapRenderer()
    {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    /**
     * Creates a custom TableCellRendererComponent.
     *
     * @param table - the JTable to apply the renderer to.
     * @param value - the value of the cell
     * @param isSelected - if the cell is selected
     * @param hasFocus - if the cell has focus
     * @param row - the targeted row (x co-ordinate)
     * @param column - the targeted column (y co-ordinate)
     *
     * @return the created component
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setText(value.toString());
        setSize(
                table.getColumnModel().getColumn(column).getWidth(),
                getPreferredSize().height);

        if (table.getRowHeight(row) != getPreferredSize().height)
        {
            table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
    }
}