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

import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.DeathCause;
import java.util.ArrayList;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.Individual;

/**
 * This rule chooses an Individual's (the one standing on the current cell) initial StaticPotential according to the
 * distances to the exits. If the Individual standing on "cell" is caged (it cannot leave the building, because there is
 * now passable way to an exit), this Individual has to die because it cannot be evacuated.
 *
 * @author Marcel Preuß
 * @author Sylvie Temme
 *
 */
public class InitialPotentialShortestPathRule extends AbstractInitialRule {

    /**
     * This rule chooses an initial {@link StaticPotential} for the {@link Individual} on a cell. This potential is
     * chosen such that it leads to the nearest exit. If the {@code Individual} standing is caged, i.e. it cannot leave
     * the building, because there is now passable way to an exit, it dies.
     *
     * @param cell the cell
     */
    @Override
    protected void onExecute(EvacCell cell) {
        assignShortestPathPotential(cell, this.esp);
    }

    public static void assignShortestPathPotential(EvacCell cell, EvacuationSimulationProblem esp) {
        Individual individual = cell.getIndividual();
        ArrayList<StaticPotential> staticPotentials = new ArrayList<>();
        staticPotentials.addAll(esp.getCa().getPotentialManager().getStaticPotentials());
        StaticPotential initialPotential = new StaticPotential();
        double minDistanceToEvacArea = Double.MAX_VALUE;
        double distanceToEvacArea;
        for (StaticPotential sp : staticPotentials) {
            distanceToEvacArea = sp.getDistance(individual.getCell());
            if (distanceToEvacArea >= 0 && distanceToEvacArea <= minDistanceToEvacArea) {
                minDistanceToEvacArea = sp.getDistance(individual.getCell());
                initialPotential = sp;
            }
        }

        if (minDistanceToEvacArea == Double.MAX_VALUE) {
            esp.getCa().setIndividualDead(individual, DeathCause.ExitUnreachable);
        }

        individual.setStaticPotential(initialPotential);
        esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addMinDistancesToStatistic(individual, minDistanceToEvacArea, initialPotential.getDistance(cell));
        esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic(individual, 0);
        esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic(individual, 0, individual.getExhaustion());
        esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic(individual, 0, individual.getPanic());
    }
}
