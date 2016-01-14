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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.PotentialMemory;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.rndutils.RandomUtils;
import org.zetool.rndutils.generators.GeneralRandom;

/**
 * This sets Individual's initial {@link StaticPotential} according to the Individual's familiarity. If this
 * familiarity value is high, the individual chooses a good StaticPotential i.e. a potential which has a short distance
 * to the exit. If the familiarity value is low the individual will choose more or less randomly a StaticPotential,
 * which will not neccessarily guide the individual to an ExitCell in a short time.
 *
 * @author Jan-Philipp Kappmeier
 * @author Marcel Preuß
 */
public class InitialPotentialFamiliarityRule extends AbstractInitialRule {

    /**
     * The concrete method changing the individuals StaticPotential. For a detailed description read the class
     * description above.
     * @param cell the cell for which the rule is executed
     */
    @Override
    protected void onExecute(EvacCell cell) {
        List<PotentialMemory<StaticPotential>> potentialDistanceMapping = computeDistanceMapping(cell);
        if (potentialDistanceMapping.isEmpty()) {
            ec.die(cell.getState().getIndividual(), DeathCause.EXIT_UNREACHABLE);
        } else {
            selectPotential(potentialDistanceMapping, cell.getState().getIndividual());
        }
    }
    
    private List<PotentialMemory<StaticPotential>> computeDistanceMapping(EvacCell cell) {
        List<PotentialMemory<StaticPotential>> potentialToLengthOfWayMapper = new ArrayList<>();
        List<StaticPotential> staticPotentials = new ArrayList<>();
        staticPotentials.addAll(es.getCellularAutomaton().getStaticPotentials());
        staticPotentials.stream().filter(sp -> sp.getDistance(cell) >= 0).forEach(sp
                -> potentialToLengthOfWayMapper.add(new PotentialMemory<>(cell, sp)));
        return potentialToLengthOfWayMapper;
    }

    /**
     * Sort the Individual's StaticPotentials according to their distance value and select one randomly according to
     * the familiarity value.
     * 
     * @param distanceMapping maps potentials to their distance to their respective exit
     * @param individual the individual on the cell for this rule
     */
    private void selectPotential(List<PotentialMemory<StaticPotential>> distanceMapping, Individual individual) {
        Collections.sort(distanceMapping);
        int nrOfPossiblePotentials = Math.max((int) Math.round(
                (1 - individual.getFamiliarity()) * distanceMapping.size()), 1);

        GeneralRandom rnd = (RandomUtils.getInstance()).getRandomGenerator();
        int randomPotentialNumber = rnd.nextInt(nrOfPossiblePotentials);

        StaticPotential potential = distanceMapping.get(randomPotentialNumber).getStaticPotential();
        individual.setStaticPotential(potential);            
    }
}
