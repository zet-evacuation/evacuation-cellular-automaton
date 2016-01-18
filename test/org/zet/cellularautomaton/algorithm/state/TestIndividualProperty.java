package org.zet.cellularautomaton.algorithm.state;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestIndividualProperty {

    private final static IndividualBuilder builder = new IndividualBuilder();

    private IndividualProperty property;

    @Before
    public void initIndividual() {
        property = new IndividualProperty(builder.build());
    }
    
    @Test
    public void deadIndividuals() {
        assertThat(property.isDead(), is(false));
        property.setDeathCause(DeathCause.EXIT_UNREACHABLE);
        assertThat(property.isDead(), is(true));
        assertThat(property.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
    }

    @Test(expected = IllegalStateException.class)
    public void deathCauseFailsForAliveIndividuals() {
        property.getDeathCause();
    }

    @Test(expected = IllegalStateException.class)
    public void deathCauseOnlyOnce() {
        callTwice(property::setDeathCause, DeathCause.EXIT_UNREACHABLE, DeathCause.EXIT_UNREACHABLE);
    }
    
    @Test
    public void safetyTime() {
        assertThat(property.isSafe(), is(false));

        property.setSafetyTime(3);
        
        assertThat(property.isSafe(), is(true));
        assertThat(property.getSafetyTime(), is(equalTo(3)));
    }
    
    @Test(expected = IllegalStateException.class)
    public void safetyTimeTwiceFails() {
        callTwice(property::setSafetyTime, 2, 3);
    }
    
    @Test
    public void safetyTimeTwiceSpecialCase() {
        callTwice(property::setSafetyTime, 3, 3);
    }
    
    @Test
    public void safetyTimeZero() {
        property.setSafetyTime(0);
        assertThat(property.getSafetyTime(), is(equalTo(0)));        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void safetyTimeNonNegative() {
        property.setSafetyTime(-1);
    }
    
    @Test(expected = IllegalStateException.class)
    public void safetyTimeFails() {
        property.getSafetyTime();
    }
    
    
    @Test
    public void evacuationTime() {
        assertThat(property.isEvacuated(), is(false));

        property.setEvacuationTime(3);
        
        assertThat(property.isEvacuated(), is(true));
        assertThat(property.getEvacuationTime(), is(equalTo(3)));
    }

    
    @Test(expected = IllegalStateException.class)
    public void evacuationTimeTwiceFails() {
        callTwice(property::setEvacuationTime, 2, 3);
    }
    
    @Test
    public void evacuationTimeZero() {
        property.setEvacuationTime(0);
        assertThat(property.getEvacuationTime(), is(equalTo(0)));        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void evacuationTimeNonNegative() {
        property.setEvacuationTime(-1);
    }
    
    @Test(expected = IllegalStateException.class)
    public void evacuationTimeFails() {
        property.getEvacuationTime();
    }
    
    @Test
    public void evacuationTimeSetsSafetyTime() {
        assertThat(property.isSafe(), is(false));
        assertThat(property.isEvacuated(), is(false));
        property.setEvacuationTime(4);
        assertThat(property.isSafe(), is(true));
        assertThat(property.isEvacuated(), is(true));
        assertThat(property.getSafetyTime(), is(equalTo(4)));
        assertThat(property.getEvacuationTime(), is(equalTo(4)));
    }
    
    @Test
    public void evacuationTimeDoesNotOverrideSafetyTime() {
        assertThat(property.isSafe(), is(false));
        assertThat(property.isEvacuated(), is(false));
        property.setSafetyTime(3);
        property.setEvacuationTime(4);
        assertThat(property.isSafe(), is(true));
        assertThat(property.isEvacuated(), is(true));
        assertThat(property.getSafetyTime(), is(equalTo(3)));
        assertThat(property.getEvacuationTime(), is(equalTo(4)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void evacuationTimeBeforeSafetyTimeFails() {
        property.setSafetyTime(4);
        property.setEvacuationTime(2);
    }
    
    private <E> void callTwice(Consumer<E> c, E first, E second) {
        c.accept(first);
        c.accept(second);
    }

}
