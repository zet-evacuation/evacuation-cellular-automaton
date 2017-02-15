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
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 * @author Daniel R. Schmidt
 *
 */
public class DynamicPotentialChangeAction extends Action {

    protected double newPotential;
    protected EvacCellInterface affectedCell;
    private Map<EvacCellInterface, EvacCellInterface> selfMap;

    public DynamicPotentialChangeAction(EvacCellInterface affectedCell, double newPotential) {
        this.affectedCell = affectedCell;
        this.newPotential = newPotential;
    }

    @Override
    void adoptToCA(Map<EvacCellInterface, EvacCellInterface> selfMap) throws CADoesNotMatchException {
        this.selfMap = selfMap;
//        EvacCellInterface newAffectedCell = adoptCell(affectedCell, targetCA);
//        return new DynamicPotentialChangeAction(newAffectedCell, newPotential);
    }

    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        //onCA.setDynamicPotential(affectedCell, newPotential);
    }

    @Override
    public void executeDelayed(EvacuationState es) {
    }

    @Override
    public String toString() {
        return "The dynamic potential of cell " + affectedCell + " ist set to " + newPotential + ".";
    }

    public long getNewPotentialValue() {
        return Math.round(newPotential);
    }
}
