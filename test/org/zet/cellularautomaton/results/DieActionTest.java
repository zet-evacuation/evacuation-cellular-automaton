package org.zet.cellularautomaton.results;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DieActionTest {

    private final Mockery context = new Mockery();

    private final Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);

    @Test
    public void individualDies() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        EvacCell cell = new RoomCell(0, 0);
        cell.getState().setIndividual(i);

        DieAction actionUnderTest = new DieAction(cell, DeathCause.EXIT_UNREACHABLE, i);
        context.checking(new Expectations() {
            {
                exactly(1).of(ec).die(with(i), with(DeathCause.EXIT_UNREACHABLE));
            }
        });
        actionUnderTest.execute(es, ec);
        context.assertIsSatisfied();
    }
    
    @Test(expected = InconsistentPlaybackStateException.class)
    public void failsInconsistentEmptyCell() throws InconsistentPlaybackStateException {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        EvacuationState es = context.mock(EvacuationState.class);
        EvacCell cell = new RoomCell(0, 0);

        DieAction actionUnderTest = new DieAction(cell, DeathCause.EXIT_UNREACHABLE, i);
        actionUnderTest.execute(es, ec);
    }

}
