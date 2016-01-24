package org.zet.cellularautomaton.algorithm.state;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.zetool.common.util.Helper.in;

import java.util.Collections;
import java.util.LinkedList;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestMutableEvacuationState {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final Mockery context = new Mockery();
    private final static IndividualBuilder builder = new IndividualBuilder();
    private ParameterSet ps;
    private EvacuationCellularAutomatonInterface eca;
    
    @Before
    public void init() {
        ps = context.mock(ParameterSet.class);
        eca = context.mock(EvacuationCellularAutomatonInterface.class);
    }
    
    @Test
    public void testInitialization() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        
        assertThat(es.getRemainingIndividualCount(), is(equalTo(0)));
        assertThat(es.getInitialIndividuals(), is(empty()));
        assertThat(es.getRemainingIndividuals(), is(empty()));
        assertThat(es.getInitialIndividualCount(), is(equalTo(0)));
    }

    @Test
    public void iterator() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        
        Individual i1 = builder.build();
        Individual safe = builder.build();
        Individual evacuated = builder.build();
        
        es.addIndividual(i1);
        es.addIndividual(safe);
        es.addIndividual(evacuated);
        
        es.propertyFor(safe).setSafetyTime(0);
        es.propertyFor(evacuated).setEvacuationTime(0);
        
        es.addToSafe(safe);
        es.addToEvacuated(evacuated);
        
        LinkedList<Individual> individualList = new LinkedList<>();
        for( Individual i : in(es.getRemainingIndividuals().iterator()) ) {
            individualList.add(i);
        }
        
        assertThat(individualList, contains(i1, safe));
    }

    @Test
    public void noAddingTwice() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual i = builder.build();
        es.addIndividual(i);
        exception.expect(IllegalArgumentException.class);
        es.addIndividual(i);
    }

    @Test
    public void testRemoveIndividuals() {
        Individual toEvacuate = builder.build();
        Individual notToEvacuate = builder.build();
        
        LinkedList<Individual> individuals = new LinkedList<>();
        individuals.add(toEvacuate);
        individuals.add(notToEvacuate);
        
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, individuals);

        context.checking(new Expectations() {{
            allowing(eca).setIndividualEvacuated(toEvacuate);
        }});

        es.markIndividualForRemoval(toEvacuate);
        es.removeMarkedIndividuals();
        
        assertThat(es.getRemainingIndividuals(), not(hasItem(toEvacuate)));
        assertThat(es.getRemainingIndividuals(), hasItem(notToEvacuate));
    }
    
    @Test
    public void remainingIndividuals() {
        Individual activeIndividual = builder.build();
        Individual evacuatedIndividual = builder.build();
        Individual safeIndividual = builder.build();
        Individual deadIndividual = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        es.addIndividual(activeIndividual);
        es.addIndividual(evacuatedIndividual);
        es.addIndividual(safeIndividual);
        es.addIndividual(deadIndividual);

        es.propertyFor(safeIndividual).setSafetyTime(0);
        es.propertyFor(evacuatedIndividual).setEvacuationTime(0);
        es.propertyFor(deadIndividual).setDeathCause(DeathCause.EXIT_UNREACHABLE);

        es.addToSafe(safeIndividual);
        es.addToEvacuated(evacuatedIndividual);
        es.addToDead(deadIndividual);

        assertThat(es.getRemainingIndividuals(), contains(activeIndividual, safeIndividual));
        assertThat(es.getEvacuatedIndividuals(), contains(evacuatedIndividual));
        assertThat(es.getDeadIndividuals(), contains(deadIndividual));
    }
    
    @Test
    public void safeIndividuals() {
        Individual safe = builder.build();
        Individual notSafe = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        es.addIndividual(safe);
        es.addIndividual(notSafe);

        es.propertyFor(safe).setSafetyTime(0);
        
        es.addToSafe(safe);
        
        assertThat(es.getRemainingIndividualCount(), is(equalTo(2)));
        assertThat(es.getRemainingIndividuals(), contains(safe, notSafe));
        assertThat(es.getNotSafeIndividualsCount(), is(equalTo(1)));    
    }

    @Test(expected = IllegalArgumentException.class)
    public void safeFailsIfPropertyNotSet() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual failIndividual = builder.build();
        es.addIndividual(failIndividual);
        es.addToSafe(failIndividual);
    }

    @Test
    public void evacuateIndividuals() {
        Individual evacuated1 = builder.build();
        Individual evacuated2 = builder.build();
        Individual notEvacuated1 = builder.build();
        Individual notEvacuated2 = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        es.addIndividual(evacuated1);
        es.addIndividual(evacuated2);
        es.addIndividual(notEvacuated1);
        es.addIndividual(notEvacuated2);

        es.propertyFor(evacuated1).setEvacuationTime(0);
        es.propertyFor(evacuated2).setEvacuationTime(0);

        es.addToEvacuated(evacuated1);
        es.addToEvacuated(evacuated2);

        assertThat(es.getEvacuatedIndividuals(), contains(evacuated1, evacuated2));
        assertThat(es.evacuatedIndividualsCount(), is(equalTo(2)));

        assertThat(es.getRemainingIndividualCount(), is(equalTo(2)));
        assertThat(es.getRemainingIndividuals(), contains(notEvacuated1, notEvacuated2));
        assertThat(es.getNotSafeIndividualsCount(), is(equalTo(2)));
    }
    
    @Test
    public void evacuateSafeIndividual() {
        Individual normal = builder.build();
        Individual safeFirst = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        es.addIndividual(safeFirst);
        es.addIndividual(normal);

        es.propertyFor(safeFirst).setSafetyTime(0);        
        es.addToSafe(safeFirst);

        es.propertyFor(safeFirst).setEvacuationTime(0);
        es.addToEvacuated(safeFirst);

        assertThat(es.getNotSafeIndividualsCount(), is(equalTo(1)));
        assertThat(es.evacuatedIndividualsCount(), is(equalTo(1)));
        assertThat(es.getEvacuatedIndividuals(), contains(safeFirst));
    }

    @Test(expected = IllegalArgumentException.class)
    public void evacuateFailsForNonExisting() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual nonExistent = builder.build();        
        es.addToEvacuated(nonExistent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evacuateFailsIfPropertyNotSet() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual failIndividual = builder.build();
        es.addIndividual(failIndividual);
        es.addToEvacuated(failIndividual);
    }

    @Test
    public void deadIndividuals() {
        Individual alive = builder.build();
        Individual deadNotEnoughTime1 = builder.build();
        Individual deadNotEnoughTime2 = builder.build();
        Individual deadUnreachable = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        es.addIndividual(alive);
        es.addIndividual(deadNotEnoughTime1);
        es.addIndividual(deadNotEnoughTime2);
        es.addIndividual(deadUnreachable);
        
        es.propertyFor(deadNotEnoughTime1).setDeathCause(DeathCause.NOT_ENOUGH_TIME);
        es.propertyFor(deadNotEnoughTime2).setDeathCause(DeathCause.NOT_ENOUGH_TIME);
        es.propertyFor(deadUnreachable).setDeathCause(DeathCause.EXIT_UNREACHABLE);

        es.addToDead(deadNotEnoughTime1);
        es.addToDead(deadNotEnoughTime2);
        es.addToDead(deadUnreachable);

        assertThat(es.deadIndividualsCount(), is(equalTo(3)));
        assertThat(es.getRemainingIndividualCount(), is(equalTo(1)));
        assertThat(es.getDeadIndividuals(), contains(deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dieFailsForNonExisting() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual failIndividual = builder.build();
        es.addToDead(failIndividual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dieFailsIfPropertyNotSet() {
        MutableEvacuationState es = new MutableEvacuationState(ps, eca, Collections.emptyList());
        Individual failIndividual = builder.build();        
        es.addIndividual(failIndividual);
        es.addToDead(failIndividual);
    }
}
