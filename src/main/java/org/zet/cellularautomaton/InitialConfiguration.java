/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
package org.zet.cellularautomaton;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import org.zet.cellularautomaton.potential.DynamicPotential;
import org.zet.cellularautomaton.potential.Potential;
import org.zetool.simulation.cellularautomaton.tools.CellMatrixFormatter;

/**
 * This class is a container for an initial configuration of the cellular automaton. The configuration is given by all
 * of the automatons rooms (which include all cells and the initial placing of all individuals), its global potentials
 * and its initial dynamic potential.
 *
 * @author Daniel R. Schmidt
 *
 */
public class InitialConfiguration {

    private final MultiFloorEvacuationCellularAutomaton cellularAutomaton;
    /** The single DynamicPotential. */
    private final DynamicPotential dynamicPotential;

    private double absoluteMaxSpeed;
    private List<Individual> individuals;
    private Map<Individual, EvacCellInterface> individualStartPositions;

    public InitialConfiguration(MultiFloorEvacuationCellularAutomaton cellularAutomaton,
            List<Individual> individuals, Map<Individual,EvacCellInterface> individualStartPositions) {
        this.cellularAutomaton = cellularAutomaton;
        this.individuals = individuals;
        this.individualStartPositions = individualStartPositions;
        dynamicPotential = null;
    }
    
    /**
     * Constructs a new initial configuration of a cellular automaton.
     *
     * @param cellularAutomaton
     * @param individualMapping
     * @param absoluteMaxSpeed the maximal speed that any individual can have at maximum
     * @param dynamicPotential
     */
    public InitialConfiguration(MultiFloorEvacuationCellularAutomaton cellularAutomaton,
            Map<Individual, EvacCellInterface> individualMapping, DynamicPotential dynamicPotential,
            double absoluteMaxSpeed) {
        this.cellularAutomaton = cellularAutomaton;
        this.dynamicPotential = dynamicPotential;
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    public double getAbsoluteMaxSpeed() {
        return absoluteMaxSpeed;
    }

    public MultiFloorEvacuationCellularAutomaton getCellularAutomaton() {
        return cellularAutomaton;
    }

    /**
     * Get all rooms, including all cells and the initial placing of individuals
     *
     * @return The rooms of the automaton
     */
    public Collection<Room> getRooms() {
        return cellularAutomaton.getRooms();
    }

    /**
     * Get all floors, including the empty floors. (A list of possible floors, not actual used floors)
     *
     * @return
     */
    public Collection<String> getFloors() {
        return cellularAutomaton.getFloors();
    }

    /**
     * @return a string representation of the configuration
     */
    @Override
    public String toString() {
        String representation = "";

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        formatter.registerFormatter(EvacCell.class, new EvacuationCellularAutomatonCellFormatter());
        for (Room aRoom : cellularAutomaton.getRooms()) {
            representation += "\n Room (" + aRoom + "):\n";
            representation += formatter.graphicalToString(aRoom);
        }

        return representation;
    }

    void setAbsoluteMaxSpeed(double absoluteMaxSpeed) {
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    public Map<Exit, Potential> getStaticPotentials() {
        return cellularAutomaton.getExits().stream().collect(
                Collectors.toMap(identity(), exit -> cellularAutomaton.getPotentialFor(exit)));
    }

    public DynamicPotential getDynamicPotential() {
        return dynamicPotential;
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public Map<Individual, EvacCellInterface> getIndividualStartPositions() {
        return individualStartPositions;
    }
}
