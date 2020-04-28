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
package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualToExitMapping;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;

/**
 * Provides information about the current evacuation simulation to rules.
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationState extends PropertyAccess, Iterable<Individual> {

    @Override
    public IndividualProperty propertyFor(Individual i);

    /**
     * Returns the current time step of the evacuation simulation.
     *
     * @return the current time step of the evacuation simulation
     */
    public int getTimeStep();

    /**
     * Gets the minimal number of time steps that are necessary to finish all (virtually not
     * discrete) movements.
     *
     * @return the time necessary to complete the simulation at least
     */
    public int getNecessaryTime();

    public double getDynamicPotential(EvacCellInterface cell);

    // To be removed from the interface completely:
    public CAStatisticWriter getStatisticWriter();

    public EvacuationCellularAutomaton getCellularAutomaton();

    /**
     * Returns the mapping between individuals and exit cells.
     *
     * @return the mapping between individuals and exit cells
     */
    public IndividualToExitMapping getIndividualToExitMapping();

    /**
     * Sets a mapping between individuals and exit cells.
     *
     * @param individualToExitMapping the mapping
     */
    public void setIndividualToExitMapping(IndividualToExitMapping individualToExitMapping);
    
    public int getInitialIndividualCount();
    
    public int getRemainingIndividualCount();

}
