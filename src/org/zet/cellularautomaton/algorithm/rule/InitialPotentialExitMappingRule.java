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
import org.zet.cellularautomaton.EvacCellInterface;
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
    public boolean executableOn(EvacCellInterface cell) {
        return !cell.getState().isEmpty();
    }

    /**
     * Assignes an exit (more precisely: the potential) for an individual.
     *
     * @param cell the cell on which the individual stands
     * @throws java.lang.IllegalArgumentException if an individual has not been mapped to an exit.
     */
    @Override
    protected void onExecute(EvacCellInterface cell) {
        if (potentialMapping == null) {
            init();
        }

        Individual individual = cell.getState().getIndividual();
        TargetCell target = es.getIndividualToExitMapping().getExit(individual);
        if (target != null) {
            handleWithTarget(cell, target);
        } else {
            handleWithoutTarget(cell);
        }
    }

    /**
     * Initializes the {@code potentialMapping}.
     */
    protected void init() {
        potentialMapping = new HashMap<>();
        for (StaticPotential potential : es.getCellularAutomaton().getStaticPotentials()) {
            for (TargetCell target : potential.getAssociatedExitCells()) {
                if (potentialMapping.put(target, potential) != null) {
                    throw new UnsupportedOperationException("There were two potentials leading to the same exit. This method can currently not deal with this.");
                }
            }
        }
    }
    
    /**
     * Assigns a potential to the individual on {@code cell} which is heading to {@code target}.
     * @param cell the cell the rule is executed on, containing an individual
     * @param target a target cell associated with the individual's exit choice
     */
    protected void handleWithTarget(EvacCellInterface cell, TargetCell target) {
        StaticPotential potential = potentialMapping.get(target);
        if (potential == null) {
            throw new IllegalStateException("The target cell (room id, x, y) " + target.getRoom().getID() + ", " + target.getX() + ", " + target.getY() + " does not correspond to a static potential.");
        }
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(potential);
    }

    /**
     * Assigns a potential to the individual on {@code cell} that has no exit assigned.
     * @param cell the cell the rule is executed on, containing an individual
     */
    protected void handleWithoutTarget(EvacCellInterface cell) {
        InitialPotentialShortestPathRule.assignShortestPathPotential(cell, this.es, this.ec);
    }
}
