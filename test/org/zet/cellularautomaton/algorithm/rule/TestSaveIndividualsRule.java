package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSaveIndividualsRule {

    private final Mockery context = new Mockery();
    private final States test = context.states("normal-test");
    private SaveIndividualsRule rule;
    private EvacuationStateControllerInterface ec;
    private EvacuationState es;
    private Individual i;
    private IndividualProperty ip;
    private SaveCell cell;

    @Before
    public void init() {
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        rule = new SaveIndividualsRule();
        ec = context.mock(EvacuationStateControllerInterface.class);
        rule.setEvacuationStateController(ec);
        es = context.mock(EvacuationState.class);
        rule.setEvacuationState(es);
        i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        ip = new IndividualProperty(i);
        cell = new SaveCell(0, 0);
        cell.getState().setIndividual(i);
        test.become("normal-test");
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(i);
                will(returnValue(ip)); when(test.is("normal-test"));
            }
        });
        
    }

    @Test
    public void applicableIfOccupied() {
        SaveCell saveCell = new SaveCell(0, 0);
        ExitCell exitCell = new ExitCell(0, 1);
        RoomCell other = new RoomCell(1, 1);

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
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(ip));
                exactly(1).of(ec).setSafe(with(i));
            }});
        rule.execute(cell);
        context.assertIsSatisfied();
    }

    @Test
    public void saveIndividualNotSaved() {
        IndividualProperty safeIndividualProperty = new IndividualProperty(i) {

            @Override
            public boolean isSafe() {
                return true;
            }
        };

        test.become("special-property");
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(safeIndividualProperty)); when(test.is("special-property"));
                never(ec).setSafe(with(any(Individual.class)));
            }});
        rule.execute(cell);
        context.assertIsSatisfied();
    }
    
    @Test
    public void exitPotentialSet() {
        ip.setStaticPotential(new StaticPotential());
        
        StaticPotential exitPotential = new StaticPotential();
        cell.setExitPotential(exitPotential);
        
        context.checking(new Expectations() {{
                allowing(es).getTimeStep();
                will(returnValue(3));
                exactly(1).of(ec).setSafe(with(i));
            }});
        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(sameInstance(exitPotential)));
        context.assertIsSatisfied();
    }
    
    @Test
    public void exitPotentialNotSetOnExitCell() {
        ExitCell exitCell = new ExitCell(0, 0);
        exitCell.getState().setIndividual(i);
        
        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        
        context.checking(new Expectations() {{
                exactly(1).of(ec).setSafe(with(i));
            }});
        rule.execute(exitCell);
        context.assertIsSatisfied();
    }
}
