package org.zetool.simulation.cellularautomaton.tools;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.zetool.common.util.Direction8;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellMatrixFormatter {
    private final CellFormatter fallback = new DefaultCellFormatter();
    private final Map<Class<? extends Cell>, CellFormatter> formatterMap = new HashMap<>();

    private CellFormatter getFormatter(Cell cell) {
        CellFormatter result = formatterMap.get(cell.getClass());
        return result == null ? fallback : result;
    }
    
    public <C extends Cell> void registerFormatter(Class<C> cellType, CellFormatter<C> formatter) {
        formatterMap.put(cellType, formatter);
    }
    
    Map<Direction8, Character> gridBound = new EnumMap<>(Direction8.class);
    {
        gridBound.put(Direction8.Left, '├');
        gridBound.put(Direction8.Right, '┤');
        gridBound.put(Direction8.Top, '┬');
        gridBound.put(Direction8.Down, '┴');
        gridBound.put(Direction8.TopLeft, '┌');
        gridBound.put(Direction8.TopRight, '┐');
        gridBound.put(Direction8.DownLeft, '└');
        gridBound.put(Direction8.DownRight, '┘');
    }
    char gridCenter = '┼';
    char vertical = '│';
    String horizontal = "───";
    String nullSignature = " X ";
    
    public String graphicalToString(CellMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        StringBuilder graphic = new StringBuilder();

        appendFirstRow(graphic, width);        
        for (int y = 0; y < height-1; y++) {
            appendContentRow(graphic, y, width, matrix);
            appendDelimiterRow(graphic, width);
        }
        appendContentRow(graphic, height-1, width, matrix);        
        appendLastRow(graphic, width);

        return graphic.toString();
    }
    
    private void appendFirstRow(StringBuilder graphic, int width) {
        appendDelimiterLine(graphic, width, gridBound.get(Direction8.TopLeft), gridBound.get(Direction8.Top),
                gridBound.get(Direction8.TopRight));
    }
    
    private void appendDelimiterRow(StringBuilder graphic, int width) {
        appendDelimiterLine(graphic, width, gridBound.get(Direction8.Left), gridCenter, gridBound.get(Direction8.Right));
    }
    
    private void appendLastRow(StringBuilder graphic, int width) {
        appendDelimiterLine(graphic, width, gridBound.get(Direction8.DownLeft), gridBound.get(Direction8.Down),
                gridBound.get(Direction8.DownRight));
    }
    
    private void appendDelimiterLine(StringBuilder graphic, int width, char left, char middle, char right) {
        graphic.append(left).append(horizontal);
        for (int i = 1; i < width; i++) {
            graphic.append(middle).append(horizontal);
        }
        graphic.append(right).append('\n');
    }
    
    private void appendContentRow(StringBuilder graphic, int y, int width, CellMatrix matrix) {
        for (int x = 0; x < width; x++) {
            if (matrix.getCell(x, y) != null) {
                graphic.append(vertical);
                graphic.append(getFormatter(matrix.getCell(x, y)).format(matrix.getCell(x, y)));
            } else {
                graphic.append(vertical).append(nullSignature);
            }
        }
        graphic.append(vertical).append("\n");
    }
}
