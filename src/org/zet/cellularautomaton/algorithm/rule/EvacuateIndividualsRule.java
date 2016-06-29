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
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * A rule that evacuates the individuals.
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuateIndividualsRule extends AbstractEvacuationRule {

    public EvacuateIndividualsRule() {
    }

    /**
     * Marks individuals standing on an exit to be removed. All actions on the state are executed after all rules are
     * evaluated.
     * @param cell the cell
     */
    @Override
    protected void onExecute(EvacCellInterface cell) {
        es.markIndividualForRemoval(cell.getState().getIndividual());
        // Potential needed for statistics:
        StaticPotential exit = getNearestExitStaticPotential(es.getCellularAutomaton().getStaticPotentials(), cell);
        es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic(cell.getState().getIndividual(), exit);
        // safetyTime etc will be set in the SaveIndividualsRule
    }

    /**
     * Evacuation rule is applicable if the cell it is standing on is an exit cell. Additionally, as for all evacuation
     * rules the cell must be occupied by an individual.
     * @param cell the cell the rule is applied on
     * @return {@true if the cell is an exit cell and an individual stands on it}
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return cell instanceof ExitCell &&
                cell.isOccupied() &&
                es.propertyFor(cell.getState().getIndividual()).getStepEndTime() < es.getTimeStep() + 1;
    }
}
