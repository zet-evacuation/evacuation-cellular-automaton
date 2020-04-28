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
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.Action;
import org.zet.cellularautomaton.results.SaveAction;

/**
 * The save rule must be executed before the evacuation rule is executed.
 *
 * @author Jan-Philipp Kappmeier
 */
public class SaveIndividualsRule extends AbstractSaveRule {

    public SaveIndividualsRule() {
    }

    @Override
    protected Action onExecute(EvacCellInterface cell) {
        Individual savedIndividual = cell.getState().getIndividual();
        if (!(es.propertyFor(savedIndividual).isSafe())) {

            if (cell instanceof SaveCell) {
                setExitPotential((SaveCell) cell, savedIndividual);
            }
            return new SaveAction(savedIndividual, es.getTimeStep());
            //es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic(savedIndividual);
        }
        return null;
    }

    private void setExitPotential(SaveCell cell, Individual savedIndividual) {
        Potential correspondingExitPotential = cell.getExitPotential();
        if (correspondingExitPotential == null) {
            es.propertyFor(savedIndividual).setStaticPotential(es.getCellularAutomaton().getSafePotential());
        } else {
            if (es.propertyFor(savedIndividual).getStaticPotential() != correspondingExitPotential) {
                es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic(savedIndividual, es.getTimeStep());
                es.propertyFor(savedIndividual).setStaticPotential(correspondingExitPotential);
            }
            //TODO: exit for the save potential?
            //es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic(savedIndividual, correspondingExitPotential);
        }
    }

}
