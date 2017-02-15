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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zetool.simulation.cellularautomaton.CompositeCellMatrix;
import org.zetool.simulation.cellularautomaton.Neighborhood;
import org.zetool.simulation.cellularautomaton.tools.CellMatrixFormatter;

/**
 * This class represents the structure of the cellular automaton. It holds the individuals, the rooms and the floor
 * fields which are important for the behavior of individuals. It also contains an object of the IndividualCreator which
 * is responsible for creating individuals with random attributes based upon the choices made by the user.
 *
 * @author Jan-Philipp Kappmeier
 * @author Matthias Woste
 */
public class MultiFloorEvacuationCellularAutomaton implements EvacuationCellularAutomaton {

    /**
     * The room collection for each floor.
     */
    private final Map<Integer, RoomCollection> floorRoomMapping;

    /**
     * The neighborhood.
     */
    private final Neighborhood<EvacCellInterface> neighborhood;

    /**
     * The names of floors.
     */
    private final Map<Integer, String> floorNames;

    /**
     * An ArrayList of all ExitCell objects (i.e. all exits) of the building.
     */
    private List<Exit> exits;

    /**
     * A {@code TreeMap} of all exits.
     */
    private final Map<Exit, Potential> staticPotentials;
    /**
     * The safe potential
     */
    private StaticPotential safePotential;
    private final Collection<Room> rooms = new LinkedList<>();

    /**
     * Constructs a EvacuationCellularAutomaton object with empty default objects.
     */
    public MultiFloorEvacuationCellularAutomaton() {
        this.floorRoomMapping = new HashMap<>();
        neighborhood = null;
        floorNames = new HashMap<>();

        exits = new ArrayList<>();

        staticPotentials = new HashMap<>();
        safePotential = new StaticPotential();
    }

    private MultiFloorEvacuationCellularAutomaton(Map<Integer, RoomCollection> floorRoomMapping, Map<Integer, String> floorNames,
            List<Exit> exits, Map<Exit, Potential> potentials) {
        this.floorRoomMapping = floorRoomMapping;
        this.floorNames = floorNames;
        this.exits = exits;
        neighborhood = null;
        staticPotentials = potentials;
        safePotential = new StaticPotential();
        for (RoomCollection fr : floorRoomMapping.values()) {
            rooms.addAll(fr.getRooms());
        }
    }

    /**
     * Creates a {@code Cellularautomaton} from an {@link InitialConfiguration} that is stored in an visual results
     * recorder. This is used to replay a simulation.
     *
     * @param initialConfiguration the initial configuration of the simulation.
     */
    public MultiFloorEvacuationCellularAutomaton(InitialConfiguration initialConfiguration) {
        this();

        // set up floors and rooms
        int i = 0;
        for (String floor : initialConfiguration.getFloors()) {
            //addFloor(i++, floor);
        }
        //initialConfiguration.getRooms().stream().forEach(room -> addRoom(0, (Room) room));

        for (Entry<Exit, Potential> e : initialConfiguration.getStaticPotentials().entrySet()) {
            staticPotentials.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Neighborhood<EvacCellInterface> getNeighborhood() {
        return neighborhood;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    /**
     * Returns all exits of the building.
     *
     * @return the exits
     */
    @Override
    public List<Exit> getExits() {
        return Collections.unmodifiableList(exits);
    }

    /**
     * Returns the number of cells in the whole cellular automaton
     *
     * @return the number of cells
     */
    public int getCellCount() {
        int count = 0;
        count = getRooms().stream().map(room -> room.getCellCount(false)).reduce(count, Integer::sum);
        return count;
    }

    /**
     * Returns an ArrayList of all rooms of the cellular automaton
     *
     * @return the ArrayList of rooms
     */
    @Override
    public Collection<Room> getRooms() {
        return Collections.unmodifiableCollection(rooms);
    }

    @Override
    public Potential minPotentialFor(EvacCellInterface c) {
        // assign shortest path potential to individual, so it is not null.
        int currentMin = Integer.MAX_VALUE;
        Potential ret = null;
        for (Exit exit : getExits()) {
            Potential sp = getPotentialFor(exit);
            if (sp.hasValidPotential(c) && sp.getPotential(c) < currentMin) {
                currentMin = sp.getPotential(c);
                ret = sp;
            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("No valid potential for cell " + c);
        }
        return ret;
    }

    public String graphicalToString() {
        StringBuilder representation = new StringBuilder();

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        EvacuationCellularAutomatonCellFormatter cellFormatter = new EvacuationCellularAutomatonCellFormatter();
        formatter.registerFormatter(EvacCell.class, cellFormatter);
        formatter.registerFormatter(RoomCell.class, cellFormatter);
        formatter.registerFormatter(ExitCell.class, cellFormatter);
        for (Room aRoom : rooms) {
            representation.append(formatter.graphicalToString(aRoom)).append("\n\n");
        }
        return representation.toString();
    }

    /**
     * Get the StaticPotential with the specified ID. The method throws {@code IllegalArgumentException} if the
     * specified ID not exists.
     *
     * @param id
     * @return The StaticPotential
     * @throws IllegalArgumentException
     */
    public Potential getStaticPotential(int id) {
        if (!(staticPotentials.containsKey(id))) {
            throw new IllegalArgumentException("No StaticPotential with this ID exists!");
        }
        return staticPotentials.get(id);
    }

    @Override
    public Potential getSafePotential() {
        return safePotential;
    }

    public Room getRoom(int id) {
        //return rooms.get(id);
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a collection containing all floor ids.
     *
     * @return the collection of floor ids
     */
    public Collection<String> getFloors() {
        return Collections.unmodifiableCollection(floorNames.values());
    }

    /**
     * Returns the name of the floor with a specified id. The id corresponds to the floor numbers in the z-format.
     *
     * @param id the floor id
     * @return the floors name
     */
    public String getFloorName(int id) {
        return floorNames.get(id);
    }

    /**
     * Returns a collection of all rooms on a specified floor.
     *
     * @param floor the floor
     * @return the collection of rooms
     */
    public Collection<Room> getRoomsOnFloor(int floor) {
        return floorRoomMapping.get(floor).getRooms();
    }

    /**
     * Returns the id of the floor containing the room. If no floor contains the room an exception is thrown.
     *
     * @param room
     * @return the id of the floor
     */
    public int getFloorId(Room room) {
        for (int i : floorRoomMapping.keySet()) {
            if (getRoomsOnFloor(i).contains(room)) {
                return i;
            }
        }
        throw new IllegalStateException("Room not in cellular automaton.");
    }

    @Override
    public Potential getPotentialFor(Exit exit) {
        return staticPotentials.get(exit);
    }

    public static class EvacuationCellularAutomatonBuilder {

        private final Map<Integer, RoomCollection> floorRoomMapping = new HashMap<>();
        private final Map<Integer, String> floorNames = new HashMap<>();
        private final List<Exit> exits = new LinkedList<>();
        private final Map<Exit, Potential> potentials = new HashMap<>();

        /**
         * Adds a new floor.
         *
         * @param level the floor's level
         * @param name the floor name
         * @throws IllegalArgumentException if a floor for the given level already exists
         */
        public final void addFloor(int level, String name) {
            if (floorRoomMapping.containsKey(level)) {
                throw new IllegalArgumentException("A floor on level " + level + " already exists with name " + floorNames.get(level));
            }
            floorNames.put(level, name);
            floorRoomMapping.put(level, new RoomCollection());
        }

        /**
         * Adds a room to the List of all rooms of the building.
         *
         * @param floor the floor to which the room is added
         * @param room the Room object to be added
         * @return 
         * @throws IllegalArgumentException if the the specific room exists already in the list rooms
         */
        public final Collection<Exit> addRoom(int floor, Room room) {
            checkValidity(floor, room);
            floorRoomMapping.get(floor).addMatrix(room);
            Collection<Exit> newExits = computeAndAddExits(room);
            exits.addAll(newExits);
            return newExits;
        }

        public final void addRoom(int floor, Room room, Collection<Exit> exits) {
            checkValidity(floor, room);
            floorRoomMapping.get(floor).addMatrix(room);
            checkValidity(room, exits);
            this.exits.addAll(exits);
        }

        private void checkValidity(int floor, Room room) {
            if (room.getCellCount(false) == 0) {
                throw new IllegalArgumentException("Room contains no cells.");
            }
            if (!floorRoomMapping.containsKey(floor)) {
                throw new IllegalArgumentException("No Floor with id " + floor + " has been added before.");
            }
            checkOverlapping(floorRoomMapping.get(floor).getRooms(), room);
        }

        private void checkOverlapping(List<Room> rooms, Room newRoom) {
            // Simple implementation
            for (Room room : rooms) {
                int roomBottom = room.getYOffset() + room.getHeight() - 1;
                int roomTop = room.getYOffset();
                int roomLeft = room.getXOffset();
                int roomRight = room.getXOffset() + room.getWidth() - 1;
                boolean above = roomBottom < newRoom.getYOffset();
                boolean below = roomTop > newRoom.getYOffset() + newRoom.getHeight() - 1;
                boolean right = roomLeft > newRoom.getXOffset() + newRoom.getWidth() - 1;
                boolean left = roomRight < newRoom.getXOffset();
                if (!(above || below || right || left)) {
                    throw new IllegalArgumentException("Intersects with " + room);
                }
            }
        }

        /**
         * Checks that all cells of the exit cluster are contained in the room.
         *
         * @param room the new room to be added
         * @param exitCluster the list of exit clusters in the room
         */
        private void checkValidity(Room room, Collection<Exit> exits) {
            for (Exit e : exits) {
                Collection<ExitCell> exitCluster = e.getExitCluster();
                for (ExitCell cell : exitCluster) {
                    if (!room.existsCellAt(cell.getX(), cell.getY()) || !room.getCell(cell.getX(), cell.getY()).equals(cell)) {
                        throw new IllegalArgumentException("Exit cell " + cell + " not contained in " + room);
                    }
                }
            }
        }

        /**
         * Adds the in the given room into the list of exits. Throws an exception if any of the exits is already known,
         * as any exit can only be in one room.
         *
         * @param room
         * @return a list of exits
         */
        private Collection<Exit> computeAndAddExits(Room room) {
            Set<EvacCellInterface> seen = new HashSet<>(room.getCellCount(false));
            LinkedList<Exit> newExits = new LinkedList<>();

            for (EvacCell cell : room.getAllCells()) {
                if (cell instanceof ExitCell) {
                    if (seen.contains(cell)) {
                        continue;
                    }
                    Exit exit = getExitOfCell((ExitCell) cell, seen);
                    newExits.add(exit);
                } else {
                    seen.add(cell);
                }
            }

            return newExits;
        }

        /**
         * Computes the cluster of exit cells that contains a given {@link ExitCell}. The method continues a breadth
         * first search through all cells.
         *
         * @param cell the cell from which the algorithm starts searching neighboring {@link ExitCell}s.
         * @param seen a set of all cells that have been seein in the current search process
         * @return Returns one Cluster of neighboring ExitCells as an ArrayList.
         */
        private Exit getExitOfCell(ExitCell cell, Set<EvacCellInterface> seen) {
            List<ExitCell> exitCluster = new LinkedList<>();
            Queue<ExitCell> d = new LinkedList<>();
            d.add(cell);
            seen.add(cell);
            while (!d.isEmpty()) {
                ExitCell c = d.poll();
                for (EvacCellInterface n : c.getNeighbours()) {
                    if (n instanceof ExitCell) {
                        ExitCell newExitCell = (ExitCell) n;
                        if (!seen.contains(newExitCell)) {
                            seen.add(newExitCell);
                            d.add(newExitCell);
                        }
                    }
                }
                exitCluster.add(c);
            }
            Exit exit = new Exit(exitCluster);
            return exit;
        }

        public MultiFloorEvacuationCellularAutomaton build() {
            return new MultiFloorEvacuationCellularAutomaton(floorRoomMapping, floorNames, exits, potentials);
        }

        public void setPotentialFor(Exit exit, Potential potential) {
            potentials.put(exit, potential);
        }
    }

    private static class RoomCollection extends CompositeCellMatrix<Room, EvacCell> {

        List<Room> getRooms() {
            return super.getMatrices();
        }

    }

}
