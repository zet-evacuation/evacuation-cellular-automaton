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

import org.zet.cellularautomaton.potential.PotentialMemory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.VoidAction;

/**
 *
 * @author Joscha Kulbatzki
 */
public class ChangePotentialInsufficientAdvancementRule extends AbstractPotentialChangeRule {

    private static final int CHANGE_THRESHOLD = 3;
    //private final int cellCountToChange;
    private final Map< Individual, Integer> memoryIndex;
    private final Map<Individual, PotentialMemory> potentialMemoryStart;
    private final Map<Individual, PotentialMemory> potentialMemoryEnd;

    public ChangePotentialInsufficientAdvancementRule() {
        //cellCountToChange = new HashMap<>();
        memoryIndex = new HashMap<>();
        potentialMemoryStart = new HashMap<>();
        potentialMemoryEnd = new HashMap<>();
        /**
         * Calibratingfactor - The bigger {@code cellCountToChange}, the longer an individual moves before a possible
         * potential change
         */
        //cellCountToChange = (int) Math.round(relativeSpeed * 15 / 0.4);
        //memoryIndex = 0;
    }

    public PotentialMemory getPotentialMemoryStart(Individual i) {
        return potentialMemoryStart.get(i);
    }

    public PotentialMemory getPotentialMemoryEnd(Individual i) {
        return potentialMemoryEnd.get(i);
    }
    
    public void setPotentialMemoryStart(Individual i, PotentialMemory start) {
        potentialMemoryStart.put(i, start);
    }

    public void setPotentialMemoryEnd(Individual i, PotentialMemory end) {
        potentialMemoryEnd.put(i, end);
    }

    public int getCellCountToChange(Individual i) {
        return (int) Math.round(es.propertyFor(i).getRelativeSpeed() * 15 / 0.4);
    }

    /**
     * Used for some potential change rule...
     *
     * @return
     */
    public int getMemoryIndex(Individual i) {
        return memoryIndex.getOrDefault(es, 0);
    }

    /**
     * Used for some potential change rule...
     *
     * @param index
     */
    public void setMemoryIndex(Individual i, int index) {
        memoryIndex.put(i, index);
    }

    /**
     * Change potential, if not enough advancement to the designated exit cell has been made
     */
    @Override
    protected boolean wantsToChange(Individual individual) {
        Potential sp = es.propertyFor(individual).getStaticPotential();
        int cellCountToChange = getCellCountToChange(individual);
        int memoryIndex = getMemoryIndex(individual);
        EvacCellInterface cell = es.propertyFor(individual).getCell();

        // Update the {@code potentialMemory}
        if (memoryIndex == 0) {
            setPotentialMemoryStart(individual, new PotentialMemory<>(cell, sp));
        }
        
        boolean retVal = false;
        if (memoryIndex == cellCountToChange - 1) {
            retVal = true;
        }
        memoryIndex = (memoryIndex + 1) % cellCountToChange;
        setMemoryIndex(individual, memoryIndex);
        return retVal;
    }
    

    /**
     *
     * @param cell
     * @return 
     */
    @Override
    protected VoidAction onExecute(EvacCellInterface cell) {
        // Get the potential of the individual on the {@code cell} as well as some other concerning constants of the individual
        Individual individual = cell.getState().getIndividual();
        Potential sp = es.propertyFor(individual).getStaticPotential();

        /**
         * Calibratingfactor - The smaller {@code epsilon}, the lower the probability of a potential-change
         */
        int epsilon = 10;

        setPotentialMemoryEnd(individual, new PotentialMemory<>(cell, sp));

        int potentialDifference = getPotentialMemoryStart(individual).getLengthOfWay() - getPotentialMemoryEnd(individual).getLengthOfWay();
        if ((potentialDifference < epsilon) && (sp == getPotentialMemoryStart(individual).getStaticPotential())) {

            // Calculate the second best Potential and the associated potential value on the {@code cell}
            //List<Potential> staticPotentials = new ArrayList<>();
            //staticPotentials.addAll(es.getCellularAutomaton().getExits());
            Potential minWayLengthPotential = sp;
            int lengthOfWayValue = Integer.MAX_VALUE;
            for (Exit exit : es.getCellularAutomaton().getExits()) {
                Potential statPot = es.getCellularAutomaton().getPotentialFor(exit);
                if ((statPot.getPotential(cell) < lengthOfWayValue) && (statPot != sp)) {
                    minWayLengthPotential = statPot;
                    lengthOfWayValue = statPot.getPotential(cell);
                }
            }

            // Check if the new potential is promising enough to change.
            // This is the case, if at least CHANGE_THRESHOLD cells of
            // the free neighbors have a lower potential (with respect
            // to the new static potential) than the current cell
            List<EvacCellInterface> freeNeighbours = cell.getFreeNeighbours();
            int i = 0;
            int promisingNeighbours = 0;
            int curPotential = minWayLengthPotential.getPotential(cell);
            while (i < freeNeighbours.size() && promisingNeighbours <= CHANGE_THRESHOLD) {
                if (minWayLengthPotential.getPotential(freeNeighbours.get(i)) < curPotential) {
                    promisingNeighbours++;
                }
                i++;
            }

            if (promisingNeighbours > CHANGE_THRESHOLD) {
                es.propertyFor(individual).setStaticPotential(minWayLengthPotential);
                es.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic(individual, es.getTimeStep());
            }
        }

        return VoidAction.VOID_ACTION;
    }
}
