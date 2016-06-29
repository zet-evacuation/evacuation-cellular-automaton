/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.statistic.results;

/**
 *
 * @author Sylvie Temme
 */
import java.util.HashMap;
import java.util.ArrayList;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Room;

/**
 *
 * @author Sylvie
 */
public class StoredCAStatisticResultsForCells {

    /** Associate each cell with an ArrayList which contains all timesteps the cell is occupied. */
    private HashMap<EvacCellInterface, ArrayList<Integer>> cellUtilization;

    /** Associate each cell with an ArrayList which contains all timesteps at which an individual standing at the cell
     * is waiting. */
    private HashMap<EvacCellInterface, ArrayList<Integer>> waitingTime;

    /** Stores utilization for each room. */
    private HashMap<Room, Double> overallRoomUtilization;

    /** Stores waiting time for each room. */
    private HashMap<Room, Double> overallRoomWaitingTime;

    public StoredCAStatisticResultsForCells() {

        cellUtilization = new HashMap<>();
        waitingTime = new HashMap<>();
    }

    /**
     * Is invoked if an individual occupied a cell c in timestep t and stores this values.
     *
     * @param c cell occupied
     * @param t timestep
     */
    public void addCellToUtilizationStatistic(EvacCellInterface c, int t) {
        if (cellUtilization.containsKey(c)) {
            cellUtilization.get(c).add(t);
        } else {
            cellUtilization.put(c, new ArrayList<>());
            cellUtilization.get(c).add(t);
        }
    }

    /**
     * Is invoked if an individual occupied a cell c in timestep t and is waiting.
     *
     * @param c cell occupied
     * @param t timestep
     */
    public void addCellToWaitingStatistic(EvacCellInterface c, int t) {
        if (waitingTime.containsKey(c)) {
            waitingTime.get(c).add(t);
        } else {
            waitingTime.put(c, new ArrayList<>());
            waitingTime.get(c).add(t);
        }
    }

    public HashMap<EvacCellInterface, ArrayList<Integer>> getHashMapCellUtilization() {
        return cellUtilization;
    }

    public HashMap<EvacCellInterface, ArrayList<Integer>> getHashMapWaitingTime() {
        return waitingTime;
    }

}
