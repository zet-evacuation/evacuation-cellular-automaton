package org.zetool.simulation.cellularautomaton.tools;

import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;

/**
 * A fully ASCII compliant cell formatter.
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicCellMatrixFormatterStyle implements CellMatrixFormatterStyle {

    private final static char VERTICAL = '|';
    private final static char HORIZONTAL = '-';
    private final static char CROSS = '+';
    private final static char NULL_SIGNATURE = 'X';

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
