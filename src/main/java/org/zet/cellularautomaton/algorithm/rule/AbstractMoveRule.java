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
package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.MoveAction;

/**
 * The base rule for all rules indicating movement.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractMoveRule extends AbstractEvacuationRule<MoveAction> {

    /**
     * Decides, if an individual can move in individual step. This is possible, when the last move
     * was already finished at a time earlier than this time step.
     *
     * @param individual An individual with a given parameterSet
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
    protected boolean canMove(Individual individual) {
        return es.getTimeStep() >= es.propertyFor(individual).getStepEndTime();
    }

    public abstract MoveAction move(EvacCellInterface from, EvacCellInterface target);
}
