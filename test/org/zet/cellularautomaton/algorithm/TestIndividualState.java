package org.zet.cellularautomaton.algorithm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.zetool.common.util.Helper.in;

import java.util.LinkedList;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestIndividualState {
  @Rule
  public ExpectedException exception = ExpectedException.none();
    private final Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void testInitialization() {
        IndividualState is = new IndividualState();
        assertThat(is.getRemainingIndividualCount(), is(equalTo(0)));
        assertThat(is.getInitialIndividuals(), is(empty()));
        assertThat(is.getRemainingIndividuals(), is(empty()));
        assertThat(is.getInitialIndividualCount(), is(equalTo(0)));
    }
    
    @Test
    public void noAddingTwice() {
        IndividualState is = new IndividualState();
        Individual i = builder.build();
        is.addIndividual(i);
        exception.expect(IllegalArgumentException.class);
        is.addIndividual(i);
    }

    @Test
    public void iterator() {
        IndividualState is = new IndividualState();
        Individual i1 = builder.build();
        Individual safe = builder.build();
        Individual evacuated = builder.build();
        
        is.addIndividual(i1);
        is.addIndividual(safe);
        is.addIndividual(evacuated);
        
        is.setSafe(safe);
        is.setIndividualEvacuated(evacuated);
        
        LinkedList<Individual> individualList = new LinkedList<>();
        for( Individual i : in(is.getRemainingIndividuals().iterator()) ) {
            individualList.add(i);
        }
        
        assertThat(individualList, contains(i1, safe));
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
        is.die(deadIndividual);

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
        assertThat(is.getRemainingIndividualCount(), is(equalTo(2)));
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
        assertThat(is.evacuatedIndividualsCount(), is(equalTo(2)));
        
        assertThat(is.getRemainingIndividualCount(), is(equalTo(2)));
        assertThat(is.getRemainingIndividuals(), contains(notEvacuated1, notEvacuated2));
        assertThat(is.getNotSafeIndividualsCount(), is(equalTo(2)));
    }
    
    @Test
    public void evacuateSafeIndividual() {
        Individual normal = builder.build();
        Individual safe = builder.build();
        
        IndividualState is = new IndividualState();
        is.addIndividual(safe);
        is.addIndividual(normal);
        
        is.setSafe(safe);
        assertThat(is.isSafe(safe), is(equalTo(true)));
        
        is.setIndividualEvacuated(safe);
        
        assertThat(is.isSafe(safe), is(equalTo(true)));
        assertThat(is.isEvacuated(safe), is(equalTo(true)));
        assertThat(is.getNotSafeIndividualsCount(), is(equalTo(1)));
        assertThat(is.evacuatedIndividualsCount(), is(equalTo(1)));
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

        is.die(deadNotEnoughTime1);
        is.die(deadNotEnoughTime2);
        is.die(deadUnreachable);

        assertThat(is.deadIndividualsCount(), is(equalTo(3)));
        assertThat(is.getRemainingIndividualCount(), is(equalTo(1)));
        assertThat(is.getDeadIndividuals(), contains(deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable));
    }
    
}
