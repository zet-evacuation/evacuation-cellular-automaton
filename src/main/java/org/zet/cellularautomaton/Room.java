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

import java.util.List;
import org.zetool.simulation.cellularautomaton.LocatedCellMatrix;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Room extends LocatedCellMatrix<EvacCell> {

    // Overridden from interface and specialized returns
    @Override
    public List<EvacCell> getAllCells();

    // multi-matrix cellular automata
    @Override
    public int getXOffset();

    @Override
    public int getYOffset();
    
    // necessary, but probably bad name?
    public List<DoorCell> getDoors();
    /**
     * Returns the number of cells in the room.
     * 
     * @param allCells
     * @return 
     */
    public int getCellCount(boolean allCells);
    
    // special parameters for evacuation simulation
    public int getID();

    public boolean isAlarmed();

    public void setAlarmstatus(boolean status);

    public int getFloor();


    // status-related, probably to be moved somewhere else!
    
    public void addIndividual(EvacCellInterface c, Individual i);
    public List<Individual> getIndividuals();

    public void removeIndividual(Individual i);
}
