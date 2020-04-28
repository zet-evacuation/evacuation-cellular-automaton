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
package org.zetool.algorithm.simulation.cellularautomaton;

import java.util.Optional;
import org.zetool.simulation.cellularautomaton.Cell;

/**
 * A rule that can be executed on a given cell type.
 * @param <R> the result type
 * @param <S> the cell type
 * @author Jan-Philipp Kappmeier
 */
public interface Rule<R, S extends Cell> {

    public Optional<R> execute(S cell);

    public boolean executableOn(S cell);
}
