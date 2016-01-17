package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
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
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.rule.TestInitialConcretePotentialRule.TestIndividualState;
import org.zet.cellularautomaton.potential.StaticPotential;
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
        IndividualProperty ip = new IndividualProperty(i);
        cell.getState().setIndividual(i);

        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        TestIndividualState is = new TestIndividualState();
        is.addIndividual(i);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getIndividualState();
                will(returnValue(is));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
            }
        });
        rule.setEvacuationState(es);
        rule.execute(cell);
        assertThat(is.isSafe(i), is(true));
        context.assertIsSatisfied();
    }

    @Test
    public void saveIndividualNotSaved() {
        SaveCell cell = new SaveCell(0, 0);

        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        
        TestIndividualState is = new TestIndividualState() {
            @Override
            public void setSafe(Individual i) {
                throw new AssertionError("setSafe called!");
            }

            @Override
            public boolean isSafe(Individual thisi) {
                return thisi == i;
            }
        };
        is.addIndividual(i);

        cell.getState().setIndividual(i);

        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getIndividualState();
                will(returnValue(is));
            }
        });
        rule.setEvacuationState(es);
        rule.execute(cell);
    }
    
    @Test
    public void exitPotentialSet() {
        SaveCell cell = new SaveCell(0, 0);
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        IndividualProperty ip = new IndividualProperty(i);
        ip.setStaticPotential(new StaticPotential());
        cell.getState().setIndividual(i);
        
        StaticPotential exitPotential = new StaticPotential();
        cell.setExitPotential(exitPotential);
        
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        TestIndividualState is = new TestIndividualState();
        is.addIndividual(i);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getIndividualState();
                will(returnValue(is));
                allowing(es).getTimeStep();
                will(returnValue(3));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
            }
        });
        rule.setEvacuationState(es);
        rule.execute(cell);
        assertThat(is.isSafe(i), is(true));
        assertThat(ip.getStaticPotential(), is(sameInstance(exitPotential)));
    }
    
    @Test
    public void exitPotentialNotSetOnExitCell() {
        ExitCell cell = new ExitCell(0, 0);
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        IndividualProperty ip = new IndividualProperty(i);
        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        cell.getState().setIndividual(i);
        
        EvacuationCellularAutomaton eca = new EvacuationCellularAutomaton();
        EvacuationState es = context.mock(EvacuationState.class);
        TestIndividualState is = new TestIndividualState();
        is.addIndividual(i);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getIndividualState();
                will(returnValue(is));
                allowing(es).propertyFor(i);
                will(returnValue(ip));
            }
        });
        rule.setEvacuationState(es);
        rule.execute(cell);
        assertThat(is.isSafe(i), is(true));
        assertThat(ip.getStaticPotential(), is(sameInstance(sp)));
    }
}
