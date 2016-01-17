package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.algorithm.state.IndividualState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateController;
import org.zet.cellularautomaton.algorithm.state.MutableEvacuationState;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import java.util.Arrays;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuationStateController {

    private final static IndividualBuilder builder = new IndividualBuilder();

    @Test
    public void deadIndividuals() {
        Individual alive = builder.build();
        Individual deadNotEnoughTime1 = builder.build();
        Individual deadNotEnoughTime2 = builder.build();
        Individual deadUnreachable = builder.build();

        MutableEvacuationState es = new MutableEvacuationState(null, null,
                Arrays.asList(new Individual[]{alive, deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable}));

        EvacuationStateController ec = new EvacuationStateController(es);
        
        ec.die(deadNotEnoughTime1, DeathCause.NOT_ENOUGH_TIME);
        ec.die(deadNotEnoughTime2, DeathCause.NOT_ENOUGH_TIME);
        ec.die(deadUnreachable, DeathCause.EXIT_UNREACHABLE);

        IndividualState is = es.getIndividualState();
        
        assertThat(is.deadIndividualsCount(), is(equalTo(3)));
        assertThat(is.getRemainingIndividualCount(), is(equalTo(1)));
        assertThat(is.getDeadIndividuals(), contains(deadNotEnoughTime1, deadNotEnoughTime2, deadUnreachable));

        assertThat(es.getDeadIndividualCount(DeathCause.EXIT_UNREACHABLE), is(equalTo(1)));
        assertThat(es.getDeadIndividualCount(DeathCause.NOT_ENOUGH_TIME), is(equalTo(2)));
        assertThat(es.propertyFor(deadNotEnoughTime1).getDeathCause(), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(es.propertyFor(deadNotEnoughTime2).getDeathCause(), is(equalTo(DeathCause.NOT_ENOUGH_TIME)));
        assertThat(es.propertyFor(deadUnreachable).getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(es.propertyFor(alive).isDead(), is(equalTo(false)));
        assertThat(es.propertyFor(deadNotEnoughTime1).isDead(), is(equalTo(true)));
        assertThat(es.propertyFor(deadNotEnoughTime2).isDead(), is(equalTo(true)));
        assertThat(es.propertyFor(deadUnreachable).isDead(), is(equalTo(true)));
    }
}
