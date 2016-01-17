package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestIndividualProperty {

    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void deadIndividuals() {
        IndividualProperty property = new IndividualProperty(builder.build());
        assertThat(property.isDead(), is(false));
        property.setDeathCause(DeathCause.EXIT_UNREACHABLE);
        assertThat(property.isDead(), is(true));
        assertThat(property.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
    }

    @Test(expected = IllegalStateException.class)
    public void deathCauseFailsForAliveIndividuals() {
        IndividualProperty is = new IndividualProperty(builder.build());
        is.getDeathCause();
    }

    @Test(expected = IllegalStateException.class)
    public void deathCauseOnlyOnce() {
        IndividualProperty is = new IndividualProperty(builder.build());
        is.setDeathCause(DeathCause.EXIT_UNREACHABLE);
        is.setDeathCause(DeathCause.EXIT_UNREACHABLE);
    }

}
