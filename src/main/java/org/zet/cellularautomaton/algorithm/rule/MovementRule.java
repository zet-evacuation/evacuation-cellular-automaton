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

import java.util.List;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;

/**
 * A {@code MovementRule} is a rule that moves an {@link Individual} from one cell to another. The {@code Rule}
 * supports also swapping and switching between direct execution and delayed execution.
 * 
 * @author Jan-Philipp Kappmeier
 */
public interface MovementRule extends EvacuationRule<MoveAction> {

    /**
     * Returns the possible targets already sorted by priority. The possible targets either have been set before using {@link #setPossibleTargets(java.util.ArrayList)
     * }
     * ore been computed using {@link #getPossibleTargets(ds.ca.evac.EvacCell, boolean) }.
     *
     * @return a list of possible targets.
     */
    List<EvacCellInterface> getPossibleTargets();

    /**
     * Decides whether direct execution or delayed execution is enabled.
     * 
     * @return {@literal true} when the move is directly executed, {@literal false} if it is only recorded for later execution
     */
    boolean isDirectExecute();

    void setDirectExecute(boolean directExecute);

    boolean isMoveCompleted();

    /**
     * In this simple implementation always the first possible cell is returned. As this method should be overridden, a
     * warning is printed to the err log if it is used.
     *
     * @param cell not used in the simple imlementation
     * @param targets possible targets (only the first one is used)
     * @return the first cell of the possible targets
     */
    EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets);

    MoveAction move(EvacCellInterface from, EvacCellInterface target);

    SwapAction swap(EvacCellInterface cell1, EvacCellInterface cell2);
    
}
