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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.jmock.AbstractExpectations.returnValue;
import static org.junit.Assert.assertThat;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestPotentialMemory {
    Mockery context = new Mockery();
    
    private void expect(Potential p, EvacCell c, int v) {
        context.checking(new Expectations() {
            {
                allowing(p).getPotential(with(c));
                will(returnValue(v));
            }
        });
    }
    
    @Test
    public void testMemoryInitialization() {
        Potential p = context.mock(Potential.class);
        EvacCell c = TestAbstractPotential.getCell(0);
        expect(p, c, 13);
        PotentialMemory<Potential> m = new PotentialMemory<>(c, p);
        
        assertThat(m.getLengthOfWay(), is(equalTo(13)));
        assertThat(m.getStaticPotential(), is(equalTo(p)));
    }
    
    @Test
    public void testEquals() {
        Potential p1 = context.mock(Potential.class, "p1");
        Potential p2 = context.mock(Potential.class, "p2");
        
        EvacCell c1 = TestAbstractPotential.getCell(0);
        EvacCell c2 = TestAbstractPotential.getCell(1);
        expect(p1, c1, 5);
        expect(p1, c2, 13);
        expect(p2, c2, 13);
        
        PotentialMemory<Potential> smaller = new PotentialMemory<>(c1, p1);
        PotentialMemory<Potential> equal1 = new PotentialMemory<>(c2, p1);
        PotentialMemory<Potential> equal2 = new PotentialMemory<>(c2, p2);
        assertThat(equal1, is(equalTo(equal2)));
        assertThat(equal1, is(not(equalTo(null))));
        assertThat(equal1, is(not(equalTo(smaller))));
        assertThat(equal1, is(not(equalTo((double) 1))));
        assertThat(equal1.hashCode(), is(equalTo(equal2.hashCode())));
        assertThat(equal1.hashCode(), is(not(equalTo(smaller.hashCode()))));
    }
    
    @Test
    public void testComparableEqual() {
        Potential p1 = context.mock(Potential.class, "p1");
        Potential p2 = context.mock(Potential.class, "p2");
        EvacCell c1 = TestAbstractPotential.getCell(0);
        EvacCell c2 = TestAbstractPotential.getCell(1);
        EvacCell c3 = TestAbstractPotential.getCell(2);
        expect(p1, c1, 5);
        expect(p1, c2, 13);
        expect(p2, c2, 13);
        expect(p1, c3, 27);
        
        PotentialMemory<Potential> smaller = new PotentialMemory<>(c1, p1);
        PotentialMemory<Potential> equal1 = new PotentialMemory<>(c2, p1);
        PotentialMemory<Potential> equal2 = new PotentialMemory<>(c2, p2);
        PotentialMemory<Potential> greater = new PotentialMemory<>(c3, p1);
        
        assertThat(smaller, lessThan(greater));
        assertThat(smaller, lessThan(equal2));
        assertThat(equal1, comparesEqualTo(equal2));
        assertThat(equal2, comparesEqualTo(equal1));
        assertThat(greater, greaterThan(equal2));
        assertThat(greater, greaterThan(smaller));
    }
}
