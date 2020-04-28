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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportCellTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
    public void onlyOneTargetPossible() {
        TeleportCell cell = new TeleportCell(0, 0);
        
        TeleportCell target1 = new TeleportCell(0, 1);
        TeleportCell target2 = new TeleportCell(1, 1);
        
        cell.addTarget(target1);
        exception.expect(IllegalStateException.class);
        cell.addTarget(target2);
    }
}
