package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestPotentialMemory {
    
    @Test
    public void testMemoryInitialization() {
        Mockery context = new Mockery();
        Potential p = context.mock(Potential.class);
        PotentialMemory<Potential> m = new PotentialMemory<>(13, p);
        
        assertThat(m.getLengthOfWay(), is(equalTo(13)));
        assertThat(m.getStaticPotential(), is(equalTo(p)));
    }
    
    @Test
    public void testComparableEqual() {
        Mockery context = new Mockery();
        Potential p1 = context.mock(Potential.class, "p1");
        Potential p2 = context.mock(Potential.class, "p2");
        
        PotentialMemory<Potential> smaller = new PotentialMemory<>(5, p1);
        PotentialMemory<Potential> equal1 = new PotentialMemory<>(13, p1);
        PotentialMemory<Potential> equal2 = new PotentialMemory<>(13, p2);
        PotentialMemory<Potential> greater = new PotentialMemory<>(27, p1);
        
        assertThat(smaller, lessThan(greater));
        assertThat(smaller, lessThan(equal2));
        assertThat(equal1, comparesEqualTo(equal2));
        assertThat(equal2, comparesEqualTo(equal1));
        assertThat(greater, greaterThan(equal2));
        assertThat(greater, greaterThan(smaller));
    }
}
