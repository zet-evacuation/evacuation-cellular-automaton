package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.LinkedList;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractEvacuationRule {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Mockery context = new Mockery();

    @Test
    public void executableIfOccupied() {
        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();
        RoomCell cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        Individual i = new IndividualBuilder().withAge(0).build();
        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void executesIfApplicable() {
        final LinkedList<EvacCellInterface> executedOn = new LinkedList<>();
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCellInterface cell) {
                executedOn.add(cell);
            }

            @Override
            public boolean executableOn(EvacCellInterface cell) {
                return true;
            }
        };
        RoomCell cell = new RoomCell(0, 0);
        rule.execute(cell);
        assertThat(executedOn, hasItem(cell));
        assertThat(executedOn.size(), is(equalTo(1)));
    }

    @Test
    public void executesNot() {
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCellInterface cell) {
                throw new AssertionError("onExecute called!");
            }

            @Override
            public boolean executableOn(EvacCellInterface cell) {
                return false;
            }
        };
        rule.execute(new RoomCell(0, 0));
    }

    @Test
    public void setEvacuationController() {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();
        rule.setEvacuationStateController(ec);
        assertThat(rule.ec, is(sameInstance(ec)));
    }

    @Test(expected = IllegalStateException.class)
    public void doubleSettingEvacuationStateControllerFails() {
        EvacuationStateControllerInterface ec = context.mock(EvacuationStateControllerInterface.class);
        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();
        rule.setEvacuationStateController(ec);
        rule.setEvacuationStateController(ec);
    }

    @Test
    public void settingCellularAutomatonFails() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationState es = context.mock(EvacuationState.class);
        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();

        rule.setEvacuationState(es);
        assertThat(rule.es, is(equalTo(es)));

        exception.expect(RuntimeException.class);
        rule.setEvacuationState(es);
    }

    @Test(expected = NullPointerException.class)
    public void evacuationSimulationProblemNotNull() {
        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();
        rule.setEvacuationState(null);
    }

    private static AbstractEvacuationRule simpleConcreteAbstractEvacuationRule() {
        return new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCellInterface cell) {
            }
        };

    }
}
