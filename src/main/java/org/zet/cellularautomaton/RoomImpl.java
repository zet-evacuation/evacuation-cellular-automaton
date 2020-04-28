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
package org.zet.cellularautomaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.zetool.simulation.cellularautomaton.GeometricCellMatrix;

/**
 * This class represents a room, which is a collection of Cells. Individuals can stay in a room: Each individual beeing
 * in a room is standing on one EvacCell.
 *
 * @author Marcel Preuß
 */
public class RoomImpl extends GeometricCellMatrix<EvacCell> implements Room {

    /** The id of the room (to calculate the hashCode). */
    private final int id;
    /** Counts the number of existing Rooms. Every new Room gets automatically a unique ID. */
    private static int idCount = 0;
    /** Manages the DoorCells existing in this room. */
    private final ArrayList<DoorCell> doors;
    /** Manages the individuals existing in this room. */
    private final ArrayList<Individual> individuals;

    private final int floorID;

    private boolean isAlarmed;

    public RoomImpl(int width, int height, int floorID, int xOffset, int yOffset) {
        this(width, height, floorID, idCount, xOffset, yOffset);
        idCount++;
    }

    protected RoomImpl(int width, int height, int floorID, int id, int xOffset, int yOffset) {
        super(width, height, xOffset, yOffset);
        this.floorID = floorID;
        doors = new ArrayList<>();
        individuals = new ArrayList<>();
        this.id = id;
        this.isAlarmed = false;
    }
    
    /**
     * Creates a copy of a room. Uses the same instances of cell and individuals, e.g. no deep-copy is done.
     * @param room 
     */
    public RoomImpl(Room room) {
        this(room.getWidth(), room.getHeight(), room.getFloor(), room.getID(), room.getXOffset(), room.getYOffset());

        isAlarmed = room.isAlarmed();

        room.getDoors().forEach(doors::add);

        room.getIndividuals().forEach(individuals::add);

        populate((x, y) -> room.getCell(x, y));
    }

    /**
     * Places the defined cell at EvacCell-Position (x,y) of the room. If parameter "cell" is a "DoorCell", it is added
     * to the lists of Door-Cells. If a new EvacCell overwrites an old cell, it is checked whether the old cell was a
     * DoorCell. In this case the DoorCell will be removed from the list of DoorCells.
     *
     * @param cell The cell, which should be referenced at position (x,y). If position (x,y) shall be empty, set
     * parameter cell = null.
     * @throws IllegalArgumentException if the x- or the y-value of the parameter "cell" is out of bounds.
     */
    public void setCell(EvacCell cell) {
        setCell(cell.getX(), cell.getY(), cell);
        if ((getCell(cell.getX(),cell.getY()) != null) && (getCell(cell.getX(), cell.getY()) instanceof DoorCell)) {
            doors.remove((DoorCell)cell);
        }
        if (cell instanceof DoorCell) {
            doors.add((DoorCell) cell);
        }
        cell.setRoom(this);
    }

    /**
     * Returns the setAlarmed status of this room.
     *
     * @return true if the setAlarmed status is true.
     */
    @Override
    public boolean isAlarmed() {
        return isAlarmed;
    }

    /**
     * Sets the setAlarmed status of this room. If set to true, the individuals are
     * <b>not</b> automatically alarmed.
     *
     * @param status the setAlarmed status
     */
    @Override
    public void setAlarmstatus(boolean status) {
        this.isAlarmed = status;
    }

    /**
     * Returns an ArrayList containing the doors of the room.
     *
     * @return An ArrayList containing the doors of the room.
     */
    @Override
    public List<DoorCell> getDoors() {
        return doors;
    }

    /**
     * Returns an ArrayList containing the individuals being the room.
     *
     * @return An ArrayList containing the individuals being the room.
     */
    @Override
    public List<Individual> getIndividuals() {
        return individuals;
    }

    /**
     *
     * @param c
     * @param i
     */
    @Override
    public void addIndividual(EvacCellInterface c, Individual i) {
        if (!c.getRoom().equals(this)) {
            throw new IllegalStateException("The cell does not belong to this room.");
        }
        if (individuals.contains(i)) {
            throw new IllegalStateException("Individual " + i.id() + " is already in the room.");
        }
        individuals.add(i);
    }

    @Override
    public void removeIndividual(Individual i) {
        if (!individuals.contains(i)) {
            throw new IllegalStateException("Individual " + i.id() + " is not in the room.");
        }
        individuals.remove(i);
    }

//  HashCode und Equals auskommentiert: Wenn zwei Räume gleich sind,
//  wenn sie die gleiche ID haben verlieren wird die Moeglichkeit,
//  Raeume unter Erhaltung der ID zu Klonen und in einer HashMap Klone
//  auf ihre Originale abzubilden. Dies wird an mehreren Stellen
//  benoetigt. Die oben beschriebene Gleichheit wird nirgendwo benutzt und
//  war fuer mehrere Bugs verantwortlich.
    @Override
    public int hashCode() {
        return id;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Room)) {
//            return false;
//        }
//        Room room = (Room) obj;
//
//    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RoomImpl)) {
            return false;
        }
        RoomImpl other = (RoomImpl) obj;
        if (this.floorID != other.floorID) {
            return false;
        }
        if (this.getXOffset() != other.getXOffset()) {
            return false;
        }
        if (this.getYOffset() != other.getYOffset()) {
            return false;
        }
        for( int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y<  getHeight(); ++y) {
                if (existsCellAt(x, y) != other.existsCellAt(x, y)) {
                   return false; 
                } else {
                    if (existsCellAt(x, y)) {
                        if (!getCell(x, y).getClass().equals(other.getCell(x, y).getClass()) ) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getFloor() {
        return floorID;
    }

    /**
     * Returns the id, width, height and floor
     */
    @Override
    public String toString() {
        return "id=" + id + ";" + super.toString() + ";floor=" + floorID;
    }

    @Override
    public void clear() {
        super.clear();
        this.doors.clear();
    }
}
