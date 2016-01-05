package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSaveIndividualsRule {

    private SaveIndividualsRule rule;
    private final Mockery context = new Mockery();

    @Before
    public void init() {
        rule = new SaveIndividualsRule();
    }

    @Test
    public void applicableIfOccupied() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        saveCell.getState().setIndividual(i);
        exitCell.getState().setIndividual(i);
        other.getState().setIndividual(i);

        assertThat(rule, is(executeableOn(saveCell)));
        assertThat(rule, is(executeableOn(exitCell)));
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void notApplicableOnEmptyCell() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

        assertThat(rule, is(not(executeableOn(saveCell))));
        assertThat(rule, is(not(executeableOn(exitCell))));
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void unsaveIndividualsSaved() {
        SaveCell cell = new SaveCell(0, 0);
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        cell.getState().setIndividual(i);

        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
                exactly(1).of(es).setIndividualSave(with(i));
            }
        });
        rule.setEvacuationSimulationProblem(es);

        rule.execute(cell);
        context.assertIsSatisfied();
    }

    @Test
    public void saveIndividualNotSaved() {
        SaveCell cell = new SaveCell(0, 0);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0) {
            @Override
            public void setSafe(boolean saveStatus) {
                throw new AssertionError("setSafe called!");
            }

            @Override
            public boolean isSafe() {
                return true;
            }
        };

        cell.getState().setIndividual(i);

        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
            }
        });
        rule.setEvacuationSimulationProblem(es);

        rule.execute(cell);
    }
}
