package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.potential.Potential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class AbstractEvacuationRuleTest {

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

    @Test(expected = IllegalStateException.class)
    public void selectNearestPotential() {
        Potential p1 = context.mock(Potential.class, "p1");
        Potential p2 = context.mock(Potential.class, "p2");
        Exit e1 = new Exit("exit1", Collections.emptyList());
        Exit e2 = new Exit("exit2", Collections.emptyList());
        List<Exit> exits = Arrays.asList(e1, e2);

        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        EvacuationCellularAutomaton ca = context.mock(EvacuationCellularAutomaton.class);
        context.checking(new Expectations() {
            {
                allowing(p1).getPotential(cell);
                will(returnValue(-1));
                allowing(p2).getPotential(cell);
                will(returnValue(-1));

                allowing(ca).getExits();
                will(returnValue(exits));
                allowing(ca).getPotentialFor(e1);
                will(returnValue(p1));
                allowing(ca).getPotentialFor(e2);
                will(returnValue(p2));
            }
        });

        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();

        rule.getNearestExitStaticPotential(ca, cell);
        //rule.getNearestExitStaticPotential(Arrays.asList(p1, p2), cell);
    }

    @Test
    public void minimumPotentialSelected() {
        Potential p1 = context.mock(Potential.class, "p1");
        Potential p2 = context.mock(Potential.class, "p2");
        Potential p3 = context.mock(Potential.class, "p3");
        Exit e1 = new Exit("exit1", Collections.emptyList());
        Exit e2 = new Exit("exit2", Collections.emptyList());
        Exit e3 = new Exit("exit3", Collections.emptyList());
        List<Exit> exits = Arrays.asList(e1, e2, e3);

        EvacCellInterface cell = context.mock(EvacCellInterface.class);
        EvacuationCellularAutomaton ca = context.mock(EvacuationCellularAutomaton.class);
        context.checking(new Expectations() {
            {
                allowing(p1).getPotential(cell);
                will(returnValue(10));
                allowing(p2).getPotential(cell);
                will(returnValue(0));
                allowing(p3).getPotential(cell);
                will(returnValue(-1));

                allowing(ca).getExits();
                will(returnValue(exits));
                allowing(ca).getPotentialFor(e1);
                will(returnValue(p1));
                allowing(ca).getPotentialFor(e2);
                will(returnValue(p2));
                allowing(ca).getPotentialFor(e3);
                will(returnValue(p3));
            }
        });

        AbstractEvacuationRule rule = simpleConcreteAbstractEvacuationRule();
        Potential p = rule.getNearestExitStaticPotential(ca, cell);

        assertThat(p, is(sameInstance(p2)));
    }

    private static AbstractEvacuationRule simpleConcreteAbstractEvacuationRule() {
        return new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCellInterface cell) {
            }
        };

    }
}
