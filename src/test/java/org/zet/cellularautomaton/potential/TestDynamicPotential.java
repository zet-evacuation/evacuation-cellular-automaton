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
package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.potential.TestAbstractPotential.getCell;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDynamicPotential {
    private final Mockery context = new Mockery();

    @Test
    public void testSetPlotential() {
        DynamicPotential potential = new DynamicPotential() {
        };
        EvacCell c = getCell(0);
        potential.setPotential(c, 2.7);
        assertThat(potential.getPotential(c), is(equalTo(3)));
        assertThat(potential.getMappedCells(), contains(c));
        assertThat(potential.getMaxPotential(), is(equalTo(3)));
        assertThat(potential.getMaxPotentialDouble(), is(closeTo(2.7, 10e-6)));
    }

    @Test
    public void testDelete() {
        DynamicPotential potential = new DynamicPotential() {
        };
        EvacCell c = getCell(0);
        potential.setPotential(c, 3);
        potential.deleteCell(c);
        assertThat(potential.getPotential(c), is(equalTo(0)));
        assertThat(potential.getMappedCells(), is(empty()));
        assertThat(potential.getMaxPotential(), is(equalTo(0)));
    }
    
    @Test
    public void increaseDynamicPotential() {
        DynamicPotential potential = new DynamicPotential();
        EvacCellInterface cell = context.mock(EvacCellInterface.class);

        potential.increase(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(1)));
        
        potential.increase(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(2)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void decreaseFailsForNonExisting() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.decrease(cell);
    }
    
    @Test
    public void decreasePotential() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.setPotential(cell, 3);
        potential.decrease(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(2)));
    }
    
    @Test
    public void decreasePotentialVanishes() {
        DynamicPotential potential = new DynamicPotential();
        
        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        potential.setPotential(cell, 1);
        potential.decrease(cell);
        
        assertThat(potential.getPotential(cell), is(equalTo(0)));
        assertThat(potential.hasValidPotential(cell), is(false));
    }
}
