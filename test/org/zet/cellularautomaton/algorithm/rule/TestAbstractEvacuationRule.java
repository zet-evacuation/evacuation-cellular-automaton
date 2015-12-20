package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.LinkedList;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractEvacuationRule {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Mockery context = new Mockery();

    @Test
    public void testExecutableIfOccupied() {
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCell cell) {
            }
        };
        RoomCell cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        Individual i = new Individual();
        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }
    
    @Test
    public void executesIfApplicable() {
        final LinkedList<EvacCell> executedOn = new LinkedList<>();
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCell cell) {
                executedOn.add(cell);
            }

            @Override
            public boolean executableOn(EvacCell cell) {
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
            protected void onExecute(EvacCell cell) {
                throw new AssertionError("onExecute called!");
            }

            @Override
            public boolean executableOn(EvacCell cell) {
                return false;
            }
        };
        rule.execute(new RoomCell(0, 0));
    }

    @Test
    public void testSettingCellularAutomatonFails() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationState es = context.mock(EvacuationState.class);
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCell cell) {
            }
        };
        rule.setEvacuationSimulationProblem(es);
        assertThat(rule.es, is(equalTo(es)));

        exception.expect(RuntimeException.class);
        rule.setEvacuationSimulationProblem(es);
    }
    
    @Test(expected = NullPointerException.class)
    public void testEvacuationSimulationProblemNotNull() {
        AbstractEvacuationRule rule = new AbstractEvacuationRule() {

            @Override
            protected void onExecute(EvacCell cell) {
            }
        };
        rule.setEvacuationSimulationProblem(null);
    }
}
