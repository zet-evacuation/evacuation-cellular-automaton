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
package evacuationplan;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * A copy of a static potential that excludes some possible paths.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class EvacPotential extends StaticPotential {

    Individual ind;
    CellularAutomatonDirectionChecker checker;
    PropertyAccess es;

    public EvacPotential(StaticPotential sp, Individual i, CellularAutomatonDirectionChecker checker) {
        this.ind = i;
        this.checker = checker;
        sp.getMappedCells().stream().forEach(c -> setPotential(c, sp.getPotential(c)));
        setAssociatedExitCells(sp.getAssociatedExitCells());
        setAttractivity(sp.getAttractivity());
        setName(sp.getName());
        id = sp.getID();
    }

    public void setPropertyAccess(PropertyAccess es) {
        this.es = es;
    }

    @Override
    public int getPotential(EvacCellInterface cell) {
        if (hasValidPotential(cell)) {
            if (checker.canPass(ind, es.propertyFor(ind).getCell(), cell)) {
                return super.getPotential(cell);
            } else {
                return Integer.MAX_VALUE;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }
}
