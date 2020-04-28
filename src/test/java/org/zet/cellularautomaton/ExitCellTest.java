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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.StairCellTest.assertAssignment;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ExitCellTest {
    
    @Test
    public void testInitialization() {
        ExitCell cell = new ExitCell(0, 0);
        assertThat(cell.getSpeedFactor(), is(both(greaterThan(0.0)).and(lessThanOrEqualTo(1.0))));
        assertThat(cell.getAttractivity(), is(greaterThanOrEqualTo(0)));
        assertThat(cell.isSafe(), is(true));
    }
    
    @Test
    public void correctAttractivityCheck() {
        ExitCell cell = new ExitCell(0, 0);
        assertAssignment(cell::setAttractivity, 0, true);
        assertThat(cell.getAttractivity(), is(equalTo(0)));
        assertAssignment(cell::setAttractivity, -1, false);
        assertAssignment(cell::setAttractivity, 1, true);        
        assertThat(cell.getAttractivity(), is(equalTo(1)));
    }
    
    @Test
    public void correctNaming() {
        ExitCell cell = new ExitCell(0, 0);
        assertAssignment(cell::setName, "foo", true);
        assertThat(cell.getName(), is(equalTo("foo")));
        assertAssignment(cell::setName, null, false, NullPointerException.class);
    }
}
