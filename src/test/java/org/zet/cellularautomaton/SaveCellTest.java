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

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SaveCellTest {

    @Test
    public void initialization() {
        SaveCell cell = new SaveCell(0, 0);
        assertThat(cell.getSpeedFactor(), is(both(greaterThan(0.0)).and(lessThanOrEqualTo(1.0))));
        assertThat(cell.isSafe(), is(true));
    }
    
    @Test
    public void parameterSetting() {
        SaveCell cell = new SaveCell(0, 0);
        StaticPotential sp = new StaticPotential();
        
        cell.setExitPotential(sp);
        
        assertThat(cell.getExitPotential(), is(sameInstance(sp)));
    }
    
}
