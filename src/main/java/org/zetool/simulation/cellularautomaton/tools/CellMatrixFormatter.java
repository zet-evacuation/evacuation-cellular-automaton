package org.zetool.simulation.cellularautomaton.tools;

import java.util.HashMap;
import java.util.Map;
import org.zetool.common.util.Bounds;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @param <E>
 */
public class CellMatrixFormatter<E extends Cell<?>> {
    @SuppressWarnings("rawtypes")
    private static final CellMatrixFormatter DEFAULT = new CellMatrixFormatter<>();
    @SuppressWarnings("rawtypes")
    private final CellFormatter fallback = new DefaultCellFormatter<>();
    private final Map<Class<E>, CellFormatter<E>> formatterMap = new HashMap<>();
    private final CellMatrixFormatterStyle style;
    private final String horizontalUpper;
    private final String horizontal;
    private final String horizontalLower;

    public CellMatrixFormatter() {
        this(new DefaultCellMatrixFormatterStyle());
    }
    
    public CellMatrixFormatter(CellMatrixFormatterStyle style) {
        this.style = style;
        horizontalUpper = getThree(style.getBound(Bounds.UPPER));
        horizontal = getThree(style.getGrid(Orientation.HORIZONTAL));
        horizontalLower = getThree(style.getBound(Bounds.LOWER));
    }
    
    private String getThree(char c) {
        return "" + c + c + c;
    }
    
    public void registerFormatter(Class<E> cellType, CellFormatter<E> formatter) {
        formatterMap.put(cellType, formatter);
    }
    
    private CellFormatter<E> getFormatter(E cell) {
        CellFormatter<E> result = formatterMap.get((Class<E>)cell.getClass());
        return result == null ? fallback : result;
    }

    public String graphicalToString(CellMatrix<E> matrix) {
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
        appendDelimiterRow(graphic, width, horizontalUpper, style.getDelimiterBound(Direction8.TopLeft),
                style.getDelimiterBound(Direction8.Top), style.getDelimiterBound(Direction8.TopRight));
    }
    
    private void appendDelimiterRow(StringBuilder graphic, int width) {
        appendDelimiterRow(graphic, width, horizontal, style.getDelimiterBound(Direction8.Left), style.getCenter(),
                style.getDelimiterBound(Direction8.Right));
    }
    
    private void appendLastRow(StringBuilder graphic, int width) {
        appendDelimiterRow(graphic, width, horizontalLower, style.getDelimiterBound(Direction8.DownLeft),
                style.getDelimiterBound(Direction8.Down), style.getDelimiterBound(Direction8.DownRight));
    }
    
    private void appendDelimiterRow(StringBuilder sb, int width, String delimiter, char left, char middle, char right) {
        sb.append(left).append(delimiter);
        for (int i = 1; i < width; i++) {
            sb.append(middle).append(delimiter);
        }
        sb.append(right).append('\n');
    }
    
    private void appendContentRow(StringBuilder graphic, int y, int width, CellMatrix<E> matrix) {
        graphic.append(style.getBound(Bounds.LEFT));
        for (int x = 0; x < width-1; x++) {
            graphic.append(appendCellContent(matrix, x, y));
            graphic.append(style.getGrid(Orientation.VERTICAL));
        }
        graphic.append(appendCellContent(matrix, width-1, y));
        graphic.append(style.getBound(Bounds.RIGHT)).append("\n");
    }
    
    private String appendCellContent(CellMatrix<E> matrix, int x, int y) {
        E cell = matrix.getCell(x, y);
        return  cell != null ? getFormatter(cell).format(cell) : " " + style.getUndefined() + " ";
    }
    
    /**
     * Formats a cell matrix using the default cell formatter, i.e. without any specialized cell renderers.
     * @param <E>
     * @param matrix the cell matrix
     * @return multi-line string representation of the matrix' geometry
     */
    public static <E extends Cell<?>> String format(CellMatrix<E> matrix) {
        return DEFAULT.graphicalToString(matrix);
    }
}
