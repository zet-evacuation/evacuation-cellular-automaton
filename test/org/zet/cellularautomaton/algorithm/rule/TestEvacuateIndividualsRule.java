package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestEvacuateIndividualsRule {

    private final Mockery context = new Mockery();

    @Test
    public void applicableIfOccupied() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {
            {
                allowing(p).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getTimeStep();
                will(returnValue(0));
            }
        });

        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        rule.setEvacuationSimulationProblem(es);

        ExitCell exit = new ExitCell(0, 0);
        Individual toEvacuate = new IndividualBuilder().build();
        exit.getState().setIndividual(toEvacuate);
        assertThat(rule, is(executeableOn(exit)));

        RoomCell other = new RoomCell(1, 1);
        Individual notToEvacuate = new IndividualBuilder().build();
        other.getState().setIndividual(notToEvacuate);
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void notApplicableOnEmptyCell() {
        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        ExitCell exit = new ExitCell(0, 0);
        assertThat(rule, is(not(executeableOn(exit))));
        RoomCell other = new RoomCell(1, 1);
        assertThat(rule, is(not(executeableOn(other))));
    }

    @Test
    public void testExecutionMarksIndividuals() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        Individual toEvacuate = new IndividualBuilder().build();
        context.checking(new Expectations() {
            {
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
                allowing(es).getTimeStep();
                will(returnValue(0));
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                
                exactly(1).of(es).markIndividualForRemoval(with(toEvacuate));
            }
        });

        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        rule.setEvacuationSimulationProblem(es);

        ExitCell exit = new ExitCell(0, 0);
        exit.getState().setIndividual(toEvacuate);

        assertThat(eca.isIndividualMarked(toEvacuate), is(false));
        rule.onExecute(exit);
        context.assertIsSatisfied();
    }
}
