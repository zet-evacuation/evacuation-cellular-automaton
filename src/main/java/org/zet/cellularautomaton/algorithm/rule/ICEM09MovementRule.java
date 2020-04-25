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

import org.zet.cellularautomaton.results.IndividualStateChangeAction;
import org.zetool.rndutils.RandomUtils;
import java.util.List;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.VoidAction;

/**
 *
 * @author Sylvie Temme
 */
public class ICEM09MovementRule extends SimpleMovementRule2 {

    public ICEM09MovementRule() {
    }

    /**
     * An easier version of the rule ignoring the alarmed status of individuals. For the paper, it is not necessary to
     * alarm people.
     *
     * @param cell
     * @return 
     */
    @Override
    protected MoveAction onExecute(EvacCellInterface cell) {
        individual = cell.getState().getIndividual();

        if (canMove(individual)) {
            if (isDirectExecute()) {
                EvacCellInterface targetCell = selectTargetCell(cell, computePossibleTargets(cell, true));
                setMoveRuleCompleted(true);
                return move(cell, targetCell);
            } else {
                computePossibleTargets(cell, false);
                setMoveRuleCompleted(true);
            }
        } else { // Individual can't move, it is already moving
            setMoveRuleCompleted(false);
            return null;
        }
        return MoveAction.NO_MOVE;
    }

    /**
     * Given a starting cell, this method picks one of its reachable neighbours at random. The i-th neighbour is chosen
     * with probability {@code p(i) := N * exp[mergePotentials(i, cell)]} where N is a constant used for normalisation.
     *
     * @param cell The starting cell
     * @return A neighbour of {@code cell} chosen at random.
     */
    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        Individual ind = cell.getState().getIndividual();
        if (targets.isEmpty()) {
            return cell;
        }

        double p[] = new double[targets.size()];

        for (int i = 0; i < targets.size(); i++) {
            p[i] = Math.exp(c.effectivePotential(ind, targets.get(i), es::getDynamicPotential));
        }

        int number = RandomUtils.getInstance().chooseRandomlyAbsolute(p);
        return targets.get(number);
    }
}
