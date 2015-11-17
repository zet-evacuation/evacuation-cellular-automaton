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

import java.util.HashMap;
import java.util.Map;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.TargetCell;

/**
 * This rule applies the exit mapping to the cellular automaton. It is explicitly allowed to have individuals with no
 * mapped exit.
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialPotentialExitMappingRule extends AbstractInitialRule {

    /** Mapping of exit cells to their respective potentials. */
    protected Map<TargetCell, StaticPotential> potentialMapping;

    /**
     * Checks, whether the rule is executable or not.
     *
     * @param cell the cell on which the rule should be executed
     * @return Returns true, if an Individual is standing on this cell, and moreover this Individual does not already
     * have a StaticPotential.
     */
    @Override
    public boolean executableOn(EvacCell cell) {
        return cell.getIndividual() != null;
    }

    /**
     * Assignes an exit (more precisely: the potential) for an individual.
     *
     * @param cell the cell on which the individual stands
     * @throws java.lang.IllegalArgumentException if an individual has not been mapped to an exit.
     */
    @Override
    protected void onExecute(EvacCell cell) {
        if (potentialMapping == null) {
            init();
        }

        Individual individual = cell.getIndividual();
        TargetCell target = esp.getCa().getIndividualToExitMapping().getExit(individual);
        if (target != null) {
            handleWithTarget(target);
        } else {
            handleWithoutTarget(cell);
        }
    }

    /**
     * Initializes the {@code potentialMapping}.
     */
    protected void init() {
        potentialMapping = new HashMap<>();
        for (StaticPotential potential : esp.getCa().getPotentialManager().getStaticPotentials()) {
            for (TargetCell target : potential.getAssociatedExitCells()) {
                if (potentialMapping.put(target, potential) != null) {
                    throw new UnsupportedOperationException("There were two potentials leading to the same exit. This method can currently not deal with this.");
                }
            }
        }
    }
    
    protected void handleWithTarget(TargetCell target) {
        StaticPotential potential = potentialMapping.get(target);
        if (potential == null) {
            throw new IllegalStateException("The target cell (room id, x, y) " + target.getRoom().getID() + ", " + target.getX() + ", " + target.getY() + " does not correspond to a static potential.");
        }
        target.getIndividual().setStaticPotential(potential);
    }

    protected void handleWithoutTarget(EvacCell cell) {
        InitialPotentialShortestPathRule.assignShortestPathPotential(cell, this.esp);
    }
}
