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
package org.zet.cellularautomaton.results;

import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 * This abstract class represents an action on the cellular automaton. The action is performed by an individual and
 * starts and ends in a cell. It can start and end in the same cell.
 *
 * @author Daniel R. Schmidt
 *
 */
public abstract class Action {

    protected class CADoesNotMatchException extends RuntimeException {

        
        private static final long serialVersionUID = 1L;

        public CADoesNotMatchException() {
            super("The action could not be adopted to the new CA because the new CA is incompatible with the old one.");
        }

        public CADoesNotMatchException(Action action, String message) {
            super("The action \""
                    + action
                    + "could not be converted: "
                    + message
            );
        }
    }

    /**
     * Updates all references in this action in a way that allows it to be replayed based on the given cellular
     * automaton.
     *
     * @param targetCa The cellular automaton to which this action should be adopted.
     * @return The adopted action
     */
    abstract void adoptToCA(Map<EvacCellInterface, EvacCellInterface> selfMap) throws CADoesNotMatchException;

    /**
     * Executes the action with respect to the starting and ending cell of the action.
     * @param es
     * @param ec 
     * @throws InconsistentPlaybackStateException
     */
    public abstract void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException;

    public abstract void executeDelayed(EvacuationState es);
    
    /**
     * Every subclass of this class should override the {@code toString()}.
     * 
     * @return string representation
     */
    @Override
    public abstract String toString();

    protected EvacCellInterface adoptCell(EvacCellInterface cell, EvacuationCellularAutomaton targetCA) {
        return cell;
    }
}
