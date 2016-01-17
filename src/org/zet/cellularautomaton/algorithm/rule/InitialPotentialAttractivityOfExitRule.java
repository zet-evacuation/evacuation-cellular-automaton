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
import java.util.List;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * This rule changes the Individuals StaticPotential. It chooses the admissible StaticPotential with the highest
 * attractivity value. A StaticPotential is admissible, if it leads the Individual to an ExitCell. Be careful using this
 * rule: Using this rule excessively might have the effect that all individuals run to the same exit with the highest
 * attractivity value!
 *
 * @author Jan-Philipp Kappmeier
 * @author Marcel Preuß
 */
public class InitialPotentialAttractivityOfExitRule extends AbstractInitialRule {

    /**
     * The concrete method changing the individuals StaticPotential. For a detailed description read the class
     * description above.
     *
     * @param cell
     */
    @Override
    protected void onExecute(EvacCell cell) {
        List<StaticPotential> staticPotentials = new ArrayList<>(es.getCellularAutomaton().getStaticPotentials());
        StaticPotential initialPotential = initialPotential(staticPotentials, cell);
        if (initialPotential == null) {
            ec.die(cell.getState().getIndividual(), DeathCause.EXIT_UNREACHABLE);
        } else {
            assignMostAttractivePotential(staticPotentials, initialPotential, cell);
        }
    }
    
    /**
     * Find any admissible StaticPotential for this Individual
     * @param staticPotentials list of static potentials
     * @param individual
     * @return 
     */
    private StaticPotential initialPotential(List<StaticPotential> staticPotentials, EvacCell cell) {
        for( StaticPotential mostAttractiveSP : staticPotentials) {
            if (mostAttractiveSP.getDistance(cell) >= 0) {
                return mostAttractiveSP;
            }
        }
        return null;
    }

    /**
     * Find the best admissible StaticPotential for the individual on a cell.
     * @param staticPotentials a list of all static potentials
     * @param referencePotential some reference potential
     * @param cell the cell
     */
    private void assignMostAttractivePotential(List<StaticPotential> staticPotentials,
            StaticPotential referencePotential, EvacCell cell) {
        StaticPotential mostAttractivePotential = referencePotential;
        for (StaticPotential sp : staticPotentials) {
            if ((sp.getAttractivity() > referencePotential.getAttractivity())
                    && (sp.getDistance(cell) >= 0)) {
                mostAttractivePotential = sp;
            }
        }
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(mostAttractivePotential);
    }
}
