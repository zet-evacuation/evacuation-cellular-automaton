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
public class TestSaveCell {

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
