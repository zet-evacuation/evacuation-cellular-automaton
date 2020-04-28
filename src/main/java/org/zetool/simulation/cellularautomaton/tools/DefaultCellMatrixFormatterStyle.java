/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
    
    private static final Map<Direction8, Character> DELIMITER_BOUNDS = new EnumMap<>(Direction8.class);
    static {
        DELIMITER_BOUNDS.put(Direction8.Left, '├');
        DELIMITER_BOUNDS.put(Direction8.Right, '┤');
        DELIMITER_BOUNDS.put(Direction8.Top, '┬');
        DELIMITER_BOUNDS.put(Direction8.Down, '┴');
        DELIMITER_BOUNDS.put(Direction8.TopLeft, '┌');
        DELIMITER_BOUNDS.put(Direction8.TopRight, '┐');
        DELIMITER_BOUNDS.put(Direction8.DownLeft, '└');
        DELIMITER_BOUNDS.put(Direction8.DownRight, '┘');
    }
    private static final char DELIMITER_CENTER = '┼';
    private static final char VERTICAL = '│';
    private static final char HORIZONTAL = '─';
    private static final char NULL_SIGNATURE = 'X';
    
    @Override
    public char getDelimiterBound(Direction8 dir) {
        return DELIMITER_BOUNDS.get(dir);
    }

    @Override
    public char getCenter() {
        return DELIMITER_CENTER;
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
