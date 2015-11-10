/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
package org.zetool.simulation.cellularautomaton;

import java.util.Objects;

/**
 * @param <S> the cell state
 * @author Jan-Philipp Kappmeier
 */
public abstract class SquareCell<S> implements Cell<S> {
    /** x-coordinate of the cell in the room. */
    protected final int x;
    /** y-coordinate of the cell in the room. */
    protected final int y;
    /** The Status object for the cell. */
    private S state;

    public SquareCell( S initialState, int x, int y) {
        this.x = x;
        this.y = y;
        this.state = Objects.requireNonNull( initialState, "Cell status must not be null!" );
    }

    @Override
    public S getState() {
        return state;
    }
    
    public void setState(S state) {
        this.state = state;
    }

    @Override
    public int getSides() {
        return 4;
    }
}
