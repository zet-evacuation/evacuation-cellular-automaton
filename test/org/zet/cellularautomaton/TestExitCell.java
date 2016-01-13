package org.zet.cellularautomaton;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.TestStairCell.assertAssignment;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestExitCell {
    
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
