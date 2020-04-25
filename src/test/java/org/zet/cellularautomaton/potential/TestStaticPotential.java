package org.zet.cellularautomaton.potential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.RoomCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestStaticPotential {
    double minDistance = 1.0;
    double maxDistance = 3.5;
    double distance = 2.0;
    
    @Test
    public void storeDistance() {
        StaticPotential potential = new StaticPotential();
        EvacCell c = new RoomCell(0, 0);
        assertThat(potential.getDistance(c), is(closeTo(-1, 10e-8)));
        potential.setPotential(c, distance);
        assertThat(potential.getPotential(c), is(equalTo(2)));
        assertThat(potential.getDistance(c), is(closeTo(2, 10e-8)));
        potential.setPotential(c, minDistance);
        assertThat(potential.getPotential(c), is(equalTo(1)));
        assertThat(potential.getDistance(c), is(closeTo(1, 10e-8)));
        potential.setPotential(c, maxDistance);
        assertThat(potential.getPotential(c), is(equalTo(4)));
        assertThat(potential.getDistance(c), is(closeTo(3.5, 10e-8)));
    }
    
    @Test
    public void computeMaxDistance() {
        StaticPotential potential = new StaticPotential();
        assertThat(potential.getMaxDistance(), is(closeTo(0, 10e-8)));
        
        
        RoomCell r = new RoomCell(0, 0);
        potential.setPotential(r, 0);
        potential.setDistance(r, minDistance);
        r = new RoomCell(1, 0);
        potential.setPotential(r, 0);
        potential.setDistance(r, maxDistance);
        r = new RoomCell(2, 0);
        potential.setPotential(r, 0);
        potential.setDistance(r, distance);
        
        assertThat(potential.getMaxDistance(), is(equalTo(maxDistance)));
    }
}
