package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;

/**
 * A fully ASCII compliant cell formatter.
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicCellMatrixFormatterStyle implements CellMatrixFormatterStyle {

    private static final char VERTICAL = '|';
    private static final char HORIZONTAL = '-';
    private static final char CROSS = '+';
    private static final char NULL_SIGNATURE = 'X';

    @Override
    public char getDelimiterBound(Direction8 dir) {
        return CROSS;
    }

    @Override
    public char getCenter() {
        return CROSS;
    }

    @Override
    public char getGrid(Orientation c) {
        return c == Orientation.HORIZONTAL ? HORIZONTAL : VERTICAL;
    }

    @Override
    public char getUndefined() {
        return NULL_SIGNATURE;
    }
}
