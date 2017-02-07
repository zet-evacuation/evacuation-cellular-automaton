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
import java.util.Map;
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

    /** The rooms of the cellular automaton, including cells. */
    private final Collection<Room> rooms;
    private final Collection<String> floors;
    /** A {@code TreeMap} of all StaticPotentials. */
    private final Map<Exit, Potential> staticPotentials;
    /** The single DynamicPotential. */
    private final DynamicPotential dynamicPotential;

    private double absoluteMaxSpeed;

    /**
     * Constructs a new initial configuration of a cellular automaton.
     *
     * @param floors floors
     * @param rooms The automaton's rooms, including cells and the initial placing of individuals
     * @param staticPotentials
     * @param absoluteMaxSpeed the maximal speed that any individual can have at maximum
     * @param dynamicPotential
     */
    public InitialConfiguration(Collection<String> floors, Collection<Room> rooms, Map<Exit, Potential> staticPotentials,
            DynamicPotential dynamicPotential, double absoluteMaxSpeed) {
        this.rooms = rooms;
        this.floors = floors;
        this.staticPotentials = staticPotentials;
        this.dynamicPotential = dynamicPotential;
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    public double getAbsoluteMaxSpeed() {
        return absoluteMaxSpeed;
    }

    /**
     * Get the global static potential layers of the automaton
     *
     * @return The initial static potentials
     */
//    public PotentialManager getPotentialManager() {
//        return potentialManager;
//    }

    /**
     * Get all rooms, including all cells and the initial placing of individuals
     *
     * @return The rooms of the automaton
     */
    public Collection<Room> getRooms() {
        return rooms;
    }

    /**
     * Get all floors, including the empty floors. (A list of possible floors, not actual used floors)
     *
     * @return
     */
    public Collection<String> getFloors() {
        return floors;
    }

    /**
     * @return a string representation of the configuration
     */
    @Override
    public String toString() {
        String representation = "";

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        formatter.registerFormatter(EvacCell.class, new EvacuationCellularAutomatonCellFormatter());
        for (Room aRoom : rooms) {
            representation += "\n Room (" + aRoom + "):\n";
            representation += formatter.graphicalToString(aRoom);
        }

        return representation;
    }

    void setAbsoluteMaxSpeed(double absoluteMaxSpeed) {
        this.absoluteMaxSpeed = absoluteMaxSpeed;
    }

    public Map<Exit, Potential> getStaticPotentials() {
        return staticPotentials;
    }

    public DynamicPotential getDynamicPotential() {
        return dynamicPotential;
    }
}
