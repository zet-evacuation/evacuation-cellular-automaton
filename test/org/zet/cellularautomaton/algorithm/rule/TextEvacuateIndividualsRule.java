package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.StaticPotential;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.PotentialController;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TextEvacuateIndividualsRule {

    private final Mockery context = new Mockery();

    @Test
    public void applicableIfOccupied() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
            }
        });

        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        rule.setEvacuationSimulationProblem(p);

        ExitCell exit = new ExitCell(0, 0);
        Individual toEvacuate = new Individual();
        exit.getState().setIndividual(toEvacuate);
        assertThat(rule.executableOn(exit), is(true));

        RoomCell other = new RoomCell(1, 1);
        Individual notToEvacuate = new Individual();
        other.getState().setIndividual(notToEvacuate);
        assertThat(rule.executableOn(other), is(false));
    }

    @Test
    public void notApplicableOnEmptyCell() {
        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        ExitCell exit = new ExitCell(0, 0);
        assertThat(rule.executableOn(exit), is(false));
        RoomCell other = new RoomCell(1, 1);
        assertThat(rule.executableOn(other), is(false));
    }

    @Test
    public void testExecutionMarksIndividuals() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        PotentialController pc = context.mock(PotentialController.class);
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
                allowing(p).getPotentialController();
                will(returnValue(pc));
                allowing(pc).getNearestExitStaticPotential(with(any(ExitCell.class)));
                will(returnValue(new StaticPotential()));
                allowing(p).getStatisticWriter();
                will(returnValue(new CAStatisticWriter()));
            }
        });

        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        rule.setEvacuationSimulationProblem(p);

        ExitCell exit = new ExitCell(0, 0);
        Individual toEvacuate = new Individual();
        exit.getState().setIndividual(toEvacuate);

        assertThat(eca.isIndividualMarked(toEvacuate), is(false));
        rule.onExecute(exit);
        assertThat(eca.isIndividualMarked(toEvacuate), is(true));
    }
}
