package org.zetool.simulation.cellularautomaton.tools;

import java.util.EnumMap;
import java.util.Map;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Orientation;

/**
 * A cell formatter that uses box-drawing characters defined in the extended ASCII code of code page 437. In fact, all
 * characters are also included in the more limited code page 850. The characters are not contained in the 127 ASCII
 * characters but are likely to exist in any modern UTF font. A more limited but not so nice formatter style is defined
 * {@link BasicCellMatrixFormatterStyle}. This style only uses ASCII letters.
 *
 * @author Jan-Philipp Kappmeier
 */
class DefaultCellMatrixFormatterStyle implements CellMatrixFormatterStyle {
    
    private final Map<Direction8, Character> delimiterBounds = new EnumMap<>(Direction8.class);
    {
        delimiterBounds.put(Direction8.Left, '├');
        delimiterBounds.put(Direction8.Right, '┤');
        delimiterBounds.put(Direction8.Top, '┬');
        delimiterBounds.put(Direction8.Down, '┴');
        delimiterBounds.put(Direction8.TopLeft, '┌');
        delimiterBounds.put(Direction8.TopRight, '┐');
        delimiterBounds.put(Direction8.DownLeft, '└');
        delimiterBounds.put(Direction8.DownRight, '┘');
    }
    char delimiterCenter = '┼';
    
    char vertical = '│';
    char horizontal = '─';
    char nullSignature = 'X';
    char empty = ' ';
    
    @Override
    public char getDelimiterBound(Direction8 dir) {
        return delimiterBounds.get(dir);
    }

    @Override
    public char getCenter() {
        return delimiterCenter;
    }

    @Override
    public char getGrid(Orientation c) {
        return c == Orientation.HORIZONTAL ? horizontal : vertical;
    }

    @Override
    public char getUndefined() {
        return nullSignature;
    }
}
