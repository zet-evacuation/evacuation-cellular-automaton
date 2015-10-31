package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
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

        Individual i = new Individual();
        saveCell.getState().setIndividual(i);
        exitCell.getState().setIndividual(i);
        other.getState().setIndividual(i);

        assertThat(rule.executableOn(saveCell), is(true));
        assertThat(rule.executableOn(exitCell), is(true));
        assertThat(rule.executableOn(other), is(false));
    }

    @Test
    public void notApplicableOnEmptyCell() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

        assertThat(rule.executableOn(saveCell), is(false));
        assertThat(rule.executableOn(exitCell), is(false));
        assertThat(rule.executableOn(other), is(false));
    }

    @Test
    public void unsaveIndividualsSaved() {
        SaveCell cell = new SaveCell(0, 0);
        Individual i = new Individual();
        cell.getState().setIndividual(i);

        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
                allowing(p).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
            }
        });
        rule.setEvacuationSimulationProblem(p);

        rule.execute(cell);
        assertThat(i.isSafe(), is(true));
    }

    @Test
    public void saveIndividualNotSaved() {
        SaveCell cell = new SaveCell(0, 0);

        Individual i = new Individual() {
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

        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
                allowing(p).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
            }
        });
        rule.setEvacuationSimulationProblem(p);

        rule.execute(cell);
    }
}
