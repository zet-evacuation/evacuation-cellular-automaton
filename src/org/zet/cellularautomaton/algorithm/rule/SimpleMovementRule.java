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

import org.zetool.rndutils.RandomUtils;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple movement rule that does not care about anything like slack, speed, panic or anything else. Steps are always
 * performed, there is no special behaviour on {@link #isDirectExecute()}.
 *
 * @author Jan-Philipp Kappmeier
 */
public class SimpleMovementRule extends AbstractMovementRule {

    /**
     * Returns {@code true} if the rule can be executed. That is the case if an {@link ds.ca.Individual} stands on the
     * specified {@link EvacCell}.
     *
     * @param cell the cell
     * @return {@code true} if an individual stands on the cell, {@code false} otherwise
     */
    @Override
    public boolean executableOn(EvacCell cell) {
        return !cell.getState().isEmpty();
    }

    /**
     *
     * @param cell
     */
    @Override
    protected void onExecute(EvacCell cell) {
        EvacCell targetCell = selectTargetCell(cell, computePossibleTargets(cell, true));
        Logger.getGlobal().log(Level.INFO, "Target cell: {0}", targetCell);
        if (cell.equals(targetCell)) {
            return;
        }
        move(cell, targetCell);
    }

    @Override
    public void move(EvacCell from, EvacCell targetCell) {
        Logger.getGlobal().log(Level.INFO, "Move from {0} to {1}", new Object[]{from, targetCell});
        ec.move(from, targetCell);        
    }

    @Override
    public EvacCell selectTargetCell(EvacCell cell, List<EvacCell> targets) {
        Individual ind = cell.getState().getIndividual();
        if (targets.isEmpty()) {
            return cell;
        }

        double p[] = new double[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            p[i] = Math.exp(c.effectivePotential(ind, targets.get(i), es::getDynamicPotential));
        }

        return targets.get(RandomUtils.getInstance().chooseRandomlyAbsolute(p));
    }

    @Override
    public void swap(EvacCell cell1, EvacCell cell2) {
        ec.swap(cell1, cell2);
    }
}
