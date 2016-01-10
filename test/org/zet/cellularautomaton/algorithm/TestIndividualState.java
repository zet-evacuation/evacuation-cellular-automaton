package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestIndividualState {

    private final Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testInitialization() {
        IndividualState is = new IndividualState();
        assertThat(is.getIndividualCount(), is(equalTo(0)));
        assertThat(is.getIndividuals(), is(empty()));
        assertThat(is.getInitialIndividualCount(), is(equalTo(0)));
    }

    @Test
    public void remainingIndividuals() {
        Individual activeIndividual = builder.build();
        Individual evacuatedIndividual = builder.build();
        Individual saveIndividual = builder.build();
        Individual deadIndividual = builder.build();

        IndividualState is = new IndividualState();
        is.addIndividual(activeIndividual);
        is.addIndividual(evacuatedIndividual);
        is.addIndividual(saveIndividual);
        is.addIndividual(deadIndividual);

        is.setSafe(saveIndividual);
        is.setIndividualEvacuated(evacuatedIndividual);
        is.die(deadIndividual, DeathCause.NOT_ENOUGH_TIME);

        assertThat(is.getRemainingIndividuals(), contains(activeIndividual, saveIndividual));
        assertThat(is.getEvacuatedIndividuals(), contains(evacuatedIndividual));
        assertThat(is.getDeadIndividuals(), contains(deadIndividual));
    }
    
    @Test
    public void safeIndividuals() {
        Individual safe = builder.build();
        Individual notSafe = builder.build();

        IndividualState is = new IndividualState();
        is.addIndividual(safe);
        is.addIndividual(notSafe);
        
        is.setSafe(safe);
        
        assertThat(is.isSafe(safe), is(equalTo(true)));
        assertThat(is.isSafe(notSafe), is(equalTo(false)));
        assertThat(is.isDead(safe), is(equalTo(false)));
        assertThat(is.isDead(notSafe), is(equalTo(false)));
        assertThat(is.getIndividualCount(), is(equalTo(2)));
        assertThat(is.getRemainingIndividuals(), contains(safe, notSafe));
        assertThat(is.getNotSafeIndividualsCount(), is(equalTo(1)));    
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void safeFailsForNonExisting() {
        IndividualState is = new IndividualState();
        Individual nonExistent = builder.build();        
        is.isSafe(nonExistent);
    }
    
    @Test
    public void evacuateIndividuals() {
        Individual evacuated1 = builder.build();
        Individual evacuated2 = builder.build();
        Individual notEvacuated1 = builder.build();
        Individual notEvacuated2 = builder.build();

        IndividualState is = new IndividualState();
        is.addIndividual(evacuated1);
        is.addIndividual(evacuated2);
        is.addIndividual(notEvacuated1);
        is.addIndividual(notEvacuated2);
        
        is.setIndividualEvacuated(evacuated1);
        is.setIndividualEvacuated(evacuated2);
        
        assertThat(is.isEvacuated(evacuated1), is(true));
        assertThat(is.isEvacuated(evacuated2), is(true));
        assertThat(is.isEvacuated(notEvacuated1), is(false));
        assertThat(is.isEvacuated(notEvacuated2), is(false));
        assertThat(is.getEvacuatedIndividuals(), contains(evacuated1, evacuated2));
        assertThat(is.isDead(evacuated1), is(equalTo(false)));
        assertThat(is.isDead(notEvacuated1), is(equalTo(false)));
        
        assertThat(is.getIndividualCount(), is(equalTo(2)));
        assertThat(is.getRemainingIndividuals(), contains(notEvacuated1, notEvacuated2));
        assertThat(is.getNotSafeIndividualsCount(), is(equalTo(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void evacuateFailsForNonExisting() {
        IndividualState is = new IndividualState();
        Individual nonExistent = builder.build();        
        is.isEvacuated(nonExistent);
    }

    @Test
    public void deadIndividuals() {
        Individual alive = builder.build();
        Individual deadNotEnoughTime1 = builder.build();
        Individual deadNotEnoughTime2 = builder.build();
        Individual deadUnreachable = builder.build();

        IndividualState is = new IndividualState();
        is.addIndividual(alive);
        is.addIndividual(deadNotEnoughTime1);
        is.addIndividual(deadNotEnoughTime2);
        is.addIndividual(deadUnreachable);

        is.die(deadNotEnoughTime1, DeathCause.NOT_ENOUGH_TIME);
        is.die(deadNotEnoughTime2, DeathCause.NOT_ENOUGH_TIME);
        is.die(deadUnreachable, DeathCause.EXIT_UNREACHABLE);

        assertThat(is.deadIndividualsCount(), is(equalTo(3)));
        assertThat(is.getDeadIndividualCount(DeathCause.EXIT_UNREACHABLE), is(equalTo(1)));
        assertThat(is.getDeadIndividualCount(DeathCause.NOT_ENOUGH_TIME), is(equalTo(2)));
        assertThat(is.getIndividualCount(), is(equalTo(1)));
        assertThat(is.getDeathCause(deadNotEnoughTime1), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(is.getDeathCause(deadNotEnoughTime2), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(is.getDeathCause(deadUnreachable), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(is.isDead(alive), is(equalTo(false)));
        assertThat(is.isDead(deadNotEnoughTime1), is(equalTo(true)));
        assertThat(is.isDead(deadNotEnoughTime2), is(equalTo(true)));
        assertThat(is.isDead(deadUnreachable), is(equalTo(true)));
        assertThat(is.getDeadIndividuals(), contains(deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deathCauseFailsForAliveIndividuals() {
        Individual alive = builder.build();
        IndividualState is = new IndividualState();
        is.addIndividual(alive);
        is.getDeathCause(alive);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deadFailsForNonExisting() {
        IndividualState is = new IndividualState();
        Individual nonExistent = builder.build();        
        is.isDead(nonExistent);
    }
}
