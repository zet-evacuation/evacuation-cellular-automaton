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

import java.util.Collection;

/**
 * An exit of the evacuation scenario. Individuals try to reach an exit.
 *
 * An exit consists of its {@link EvacCell}s and the corresponding floor field.
 *
 * @author Jan-Philipp Kappmeier
 */
public class Exit {

    private final String name;
    private Collection<ExitCell> exitCells;
    private int attractivity;

    public Exit(String name, Collection<ExitCell> exitCells) {
        this.name = name;
        this.exitCells = exitCells;
    }

    Exit(Collection<ExitCell> exitCluster) {
        this.exitCells = exitCluster;
        this.name = "";
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<ExitCell> getExitCluster() {
        return exitCells;
    }

    public int getAttractivity() {
        return attractivity;
    }

    public double getCapacity() {
        return Double.POSITIVE_INFINITY;
    }

    public void setAttractivity(int attractivity) {
        this.attractivity = attractivity;
    }

}
