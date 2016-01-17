package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.IndividualToExitMapping;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.rule.TestInitialConcretePotentialRule.TestIndividualState;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestInitialPotentialExitMappingRuleStrict {
    States test;
    private final Mockery context = new Mockery();
    InitialPotentialExitMappingRuleStrict rule;
    EvacCell cell;
    Individual i;
    private IndividualProperty ip;
    EvacuationCellularAutomaton eca;
    EvacuationSimulationProblem p;
    ExitCell target;
    EvacuationState es;
    private final static IndividualBuilder builder = new IndividualBuilder();

    private final IndividualToExitMapping exitMapping = (Individual individual) -> {
        if (individual == TestInitialPotentialExitMappingRuleStrict.this.i) {
            return target;
        }
        throw new IllegalStateException("Called with bad individual.");
    };

    @Before
    public void init() {
        rule = new InitialPotentialExitMappingRuleStrict();
        Room room = context.mock(Room.class);
        p = context.mock(EvacuationSimulationProblem.class);
        eca = new EvacuationCellularAutomaton();
        es = context.mock(EvacuationState.class);
        TestIndividualState is = new TestIndividualState();
        i = builder.build();
        ip = new IndividualProperty(i);
        is.addIndividual(i);
        test = context.states("normal-test");
        test.become("normal-test");

        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(i));
                allowing(room).removeIndividual(with(i));
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).getIndividualState();
                will(returnValue(is));
                allowing(es).propertyFor(i); when(test.is("normal-test"));
                will(returnValue(ip));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        ip.setCell(cell);
        cell.getState().setIndividual(i);
        rule.setEvacuationState(es);
        eca.addIndividual(cell, i);

        target = new ExitCell(1.0, 0, 0, room);

    }

    @Test
    public void executableEvenIfPotentialAssigned() {
        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        Individual i = builder.build();
        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void assignedExitsAreAssigned() {
        eca.setIndividualToExitMapping(exitMapping);

        StaticPotential sp = new StaticPotential();
        List<ExitCell> spExits = new LinkedList<>();
        spExits.add(target);
        sp.setAssociatedExitCells(spExits);

        eca.addStaticPotential(sp);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(equalTo(sp)));
    }

    @Test(expected = IllegalStateException.class)
    public void noPotentialForTargetFails() {
        eca.setIndividualToExitMapping(exitMapping);
        rule.execute(cell);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void twoPotentialsForOneCellFails() {
        eca.setIndividualToExitMapping(exitMapping);

        StaticPotential sp1 = new StaticPotential();
        List<ExitCell> sp1Exits = new LinkedList<>();
        sp1Exits.add(target);
        sp1.setAssociatedExitCells(sp1Exits);

        StaticPotential sp2 = new StaticPotential();
        List<ExitCell> sp2Exits = new LinkedList<>();
        sp2Exits.add(target);
        sp2.setAssociatedExitCells(sp2Exits);

        eca.addStaticPotential(sp1);
        eca.addStaticPotential(sp2);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(equalTo(sp2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsIfNotAssigned() {
        eca.setIndividualToExitMapping(_unused -> null);
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(new IndividualProperty(i)));
            }});
        rule.execute(cell);
    }

    @Test
    public void deadIndividualWithoutTarget() {
        test.become("special-case");
        eca.setIndividualToExitMapping(_unused -> null);
        IndividualProperty ip = new IndividualProperty(i) {

            @Override
            public boolean isDead() {
                return true;
            }            
        };
        
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i); when(test.is("special-case"));
                will(returnValue(ip));
            }});
        rule.execute(cell);
    }

}
