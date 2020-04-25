package org.zet.cellularautomaton.algorithm.rule;

import java.util.Collections;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.FakePotential;
import org.zet.cellularautomaton.results.ExitAction;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuateIndividualsRuleTest {

    private final Mockery context = new Mockery();

    @Test
    public void applicableIfOccupied() {
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        MultiFloorEvacuationCellularAutomaton eca = new MultiFloorEvacuationCellularAutomaton();
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
        rule.setEvacuationState(es);

        ExitCell exit = new ExitCell(0, 0);
        Individual toEvacuate = new IndividualBuilder().build();
        Individual notToEvacuate = new IndividualBuilder().build();
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(toEvacuate);
                will(returnValue(new IndividualProperty(toEvacuate)));
                allowing(es).propertyFor(notToEvacuate);
                will(returnValue(new IndividualProperty(notToEvacuate)));
            }
        });
        exit.getState().setIndividual(toEvacuate);
        assertThat(rule, is(executeableOn(exit)));

        RoomCell other = new RoomCell(1, 1);
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
        EvacuationCellularAutomaton eca = context.mock(EvacuationCellularAutomaton.class);
        Exit exit = new Exit("", Collections.emptyList());
        EvacuationState es = context.mock(EvacuationState.class);
        Individual toEvacuate = new IndividualBuilder().build();
        context.checking(new Expectations() {
            {
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getTimeStep();
                will(returnValue(0));
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));

                allowing(eca).getExits();
                will(returnValue(Collections.singletonList(exit)));
                allowing(eca).getPotentialFor(exit);
                will(returnValue(new FakePotential()));
            }
        });

        EvacuateIndividualsRule rule = new EvacuateIndividualsRule();
        rule.setEvacuationState(es);

        ExitCell exitCell = new ExitCell(0, 0);
        exitCell.getState().setIndividual(toEvacuate);

        ExitAction a = rule.onExecute(exitCell);
        assertThat(a.getIndividual(), is(equalTo(toEvacuate)));
    }
}
