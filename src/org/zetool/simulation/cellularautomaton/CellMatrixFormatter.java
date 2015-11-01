package org.zetool.simulation.cellularautomaton;

import java.util.HashMap;
import java.util.Map;

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
    
    public String graphicalToString(CellMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        
        String graphic = "+---";
        for (int i = 1; i < width; i++) {
            graphic += "----";
        }
        graphic += "+\n";

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.getCell(x, y) != null) {
                    graphic += "|";
                    graphic += getFormatter(matrix.getCell(x, y)).format(matrix.getCell(x, y));
                } else {
                    graphic += "| X ";
                }
            }
            graphic += "|\n";
            graphic += "+---";
            for (int i = 1; i < width; i++) {
                graphic += "----";
            }
            graphic += "+\n";
        }

        graphic += "\n\n";

        return graphic;
    }
}
