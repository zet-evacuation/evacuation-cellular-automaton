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
        EvacCell c = new RoomCell(0,0);
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
        
        
        potential.setDistance(new RoomCell(0, 0), minDistance);
        potential.setDistance(new RoomCell(0, 0), maxDistance);
        potential.setDistance(new RoomCell(0, 0), distance);
        
        assertThat(potential.getMaxDistance(), is(equalTo(maxDistance)));
    }
    
    @Test
    public void testAssociateExitCells() {
        
    }
}
