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
    private final Map<Integer, RoomCollection> roomCollections;
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
     * A map of rooms to identification numbers.
     */
    private Map<Integer, Room> rooms;

    /**
     * A mapping that maps exits to their capacity.
     */
    //private Map<Potential, Double> exitToCapacityMapping;

    /**
     * A {@code TreeMap} of all exits.
     */
    private final Map<Exit, Potential> staticPotentials;
    /**
     * The safe potential
     */
    private StaticPotential safePotential;

    /**
     * Constructs a EvacuationCellularAutomaton object with empty default objects.
     */
    public MultiFloorEvacuationCellularAutomaton() {
        this.roomCollections = new HashMap<>();
        neighborhood = null;
        floorNames = new HashMap<>();

        exits = new ArrayList<>();
        rooms = new HashMap<>();

        staticPotentials = new HashMap<>();
        safePotential = new StaticPotential();
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
            addFloor(i++, floor);
        }
        initialConfiguration.getRooms().stream().forEach(room -> addRoom(0, (Room) room));

        for (Room room : initialConfiguration.getRooms()) {
            for (EvacCell cell : room.getAllCells()) {
                if (!cell.getState().isEmpty()) {
                    //addIndividual(cell, cell.getState().getIndividual());
                }
            }
        }

        for (Entry<Exit, Potential> e : initialConfiguration.getStaticPotentials().entrySet()) {
            staticPotentials.put(e.getKey(), e.getValue());
        }

        //setAbsoluteMaxSpeed(initialConfiguration.getAbsoluteMaxSpeed());
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
     * Adds a new floor.
     *
     * @param index the floor's index
     * @param name the floor name
     */
    public final void addFloor(int index, String name) {
        if (roomCollections.containsKey(index)) {
            throw new IllegalArgumentException("Floor " + name + " already exists.");
        }
        floorNames.put(index, name);
        roomCollections.put(index, new RoomCollection());
    }

    /**
     * Adds a room to the List of all rooms of the building.
     *
     * @param floor the floor to which the room is added
     * @param room the Room object to be added
     * @throws IllegalArgumentException if the the specific room exists already in the list rooms
     */
    public final void addRoom(int floor, Room room) {
        if (rooms.containsKey(room.getID())) {
            throw new IllegalArgumentException("Specified room exists already in list rooms.");
        } else {
            rooms.put(room.getID(), room);
            if (!roomCollections.containsKey(floor)) {
                throw new IllegalStateException("No Floor with id " + floor + " has been added before.");
            }
            roomCollections.get(floor).addMatrix(room);
            computeAndAddExits(room);
        }
    }

    /**
     * Adds the in the given room into the list of exits. Throws an exception if any of the exits is already known, as
     * any exit can only be in one room.
     *
     * @param room
     */
    private void computeAndAddExits(Room room) {
        Set<EvacCellInterface> seen = new HashSet<>(room.getCellCount(false));
        
        for (EvacCell cell : room.getAllCells()) {
            if (cell instanceof ExitCell) {
                if (seen.contains(cell)) {
                    continue;
                }
                Exit exit = getExitOfCell((ExitCell)cell, seen);
                exits.add(exit);
            } else {
                seen.add(cell);
            }
        }
    }
    
    private Exit getExitOfCell(ExitCell cell, Set<EvacCellInterface> seen) {
        List<ExitCell> exitCluster = new LinkedList<>();
        Queue<ExitCell> d = new LinkedList<>();
        d.add(cell);
        while(!d.isEmpty()) {
            ExitCell c = d.poll();
            for (EvacCellInterface n : c.getNeighbours()) {
                if (n instanceof ExitCell) {
                    ExitCell newExitCell = (ExitCell) n;
                    if (!seen.contains(newExitCell)) {
                        seen.add(newExitCell);
                        d.add(newExitCell);
                    }
                }
                exitCluster.add(c);
            }
        }
        Exit exit = new Exit(exitCluster);
        return exit;
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
        return Collections.unmodifiableCollection(rooms.values());
    }

    @Override
    public Potential minPotentialFor(EvacCellInterface c) {
        // assign shortest path potential to individual, so it is not null.
        int currentMin = -1;
        Potential ret = null;
        for (Exit exit : getExits()) {
            Potential sp = getPotentialFor(exit);
            if (sp.getPotential(c) > -1 && sp.getPotential(c) < currentMin) {
                currentMin = sp.getPotential(c);
                ret = sp;
            }
        }
        if (ret != null) {
            throw new IllegalArgumentException("No valid potential for cell " + c);
        }
        return ret;
    }

    /**
     * This method recognizes clusters of neighbouring ExitCells (that means ExitCells lying next to another ExitCell)
     * and returns a list containing another list of {@link ExitCell}s for each cluster of {@link ExitCell}s.
     *
     * @return a list of exit clusters
     */
//    public List<List<ExitCell>> clusterExitCells() {
//        Set<ExitCell> alreadySeen = new HashSet<>();
//        List<List<ExitCell>> allClusters = new ArrayList<>();
//        List<Exit> allExitCells = this.getExitCluster();
//        for (ExitCell e : allExitCells) {
//            if (!alreadySeen.contains(e)) {
//                List<ExitCell> singleCluster = new ArrayList<>();
//                singleCluster = this.findExitCellCluster(e, singleCluster, alreadySeen);
//                allClusters.add(singleCluster);
//            }
//        }
//        return allClusters;
//    }

    /**
     * Private sub-method for finding a Cluster of neighboring ExitCells recursively.
     *
     * @param currentCell The cell from which the algorithm starts searching neighboring ExitCells.
     * @param cluster An empty ArrayList, in which the cluster will be created.
     * @param alreadySeen A HashSet storing all already clustered ExitCells to prevent them of being clustered a second
     * time.
     * @return Returns one Cluster of neighboring ExitCells as an ArrayList.
     */
    private List<ExitCell> findExitCellCluster(ExitCell currentCell, List<ExitCell> cluster, Set<ExitCell> alreadySeen) {
        if (!alreadySeen.contains(currentCell)) {
            cluster.add(currentCell);
            alreadySeen.add(currentCell);
            Collection<EvacCellInterface> cellNeighbours = currentCell.getDirectNeighbors();
            List<ExitCell> neighbours = new ArrayList<>();
            for (EvacCellInterface c : cellNeighbours) {
                if (c instanceof ExitCell) {
                    neighbours.add((ExitCell) c);
                }
            }
            for (ExitCell c : neighbours) {
                cluster = this.findExitCellCluster(c, cluster, alreadySeen);
            }
        }
        return cluster;
    }

    public String graphicalToString() {
        StringBuilder representation = new StringBuilder();

        CellMatrixFormatter formatter = new CellMatrixFormatter();
        formatter.registerFormatter(EvacCell.class, new EvacuationCellularAutomatonCellFormatter());
        for (Room aRoom : rooms.values()) {
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

    public void setsafePotential(StaticPotential potential) {
        safePotential = potential;
    }

    public Room getRoom(int id) {
        return rooms.get(id);
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
        return roomCollections.get(floor).getRooms();
    }

    /**
     * Returns the id of the floor containing the room. If no floor contains the room an exception is thrown.
     *
     * @param room
     * @return the id of the floor
     */
    public int getFloorId(Room room) {
        for (int i : roomCollections.keySet()) {
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
}
