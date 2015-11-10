/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import org.zet.cellularautomaton.EvacCell;
import java.util.Iterator;
import java.util.List;
import org.zet.cellularautomaton.potential.CellularAutomatonDirectionChecker;

public class EvacuationPlanMovementRule extends WaitingMovementRule {

    CellularAutomatonDirectionChecker checker;

    public void setChecker(CellularAutomatonDirectionChecker checker) {
        this.checker = checker;
    }

    @Override
    protected List<EvacCell> computePossibleTargets(EvacCell fromCell, boolean onlyFreeNeighbours) {
        List<EvacCell> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);
        Iterator<EvacCell> it = targets.iterator();
        while (it.hasNext()) {
            EvacCell cell = it.next();
            if (cell != fromCell) {
                if (!checker.canPass(fromCell.getIndividual(), fromCell, cell)) {
                    it.remove();
                }
            }
        }
        return targets;
    }
}
