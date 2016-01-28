package org.zet.cellularautomaton;

import java.util.List;
import org.zetool.simulation.cellularautomaton.CompositeCellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RoomCollection extends CompositeCellMatrix<Room, EvacCell> {

    List<Room> getRooms() {
        return super.getMatrices();
    }

}
