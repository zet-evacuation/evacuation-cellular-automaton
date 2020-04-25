package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.IndividualToExitMapping;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialPotentialExitMappingRuleStrictTest {
    private States test;
    private final Mockery context = new Mockery();
    private InitialPotentialExitMappingRuleStrict rule;
    private EvacCell cell;
    private Individual i;
    private IndividualProperty ip;
    private EvacuationCellularAutomaton eca;
    private EvacuationSimulationProblem p;
    private ExitCell target;
    private EvacuationState es;
    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();
    private List<Exit> exitList;

    private final IndividualToExitMapping exitMapping = (Individual individual) -> {
        if (individual == InitialPotentialExitMappingRuleStrictTest.this.i) {
            return target;
        }
        throw new IllegalStateException("Called with bad individual.");
    };

    @Before
    public void init() {
        rule = new InitialPotentialExitMappingRuleStrict();
        Room room = context.mock(Room.class);
        p = context.mock(EvacuationSimulationProblem.class);
        eca = context.mock(EvacuationCellularAutomaton.class);
        es = context.mock(EvacuationState.class);
        i = INDIVIDUAL_BUILDER.build();
        ip = new IndividualProperty(i);
        test = context.states("normal-test");
        test.become("normal-test");
        exitList = new LinkedList<>();

        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(eca).getExits();
                will(returnValue(exitList));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getFloor();
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(i); when(test.is("normal-test"));
                will(returnValue(ip));
                allowing(es).getIndividualToExitMapping(); when(test.is("normal-test"));
                will(returnValue(exitMapping));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        ip.setCell(cell);
        cell.getState().setIndividual(i);
        rule.setEvacuationState(es);

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

        Individual i = INDIVIDUAL_BUILDER.build();
        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void assignedExitsAreAssigned() {
        StaticPotential sp = new StaticPotential();
        List<ExitCell> spExits = new LinkedList<>();
        spExits.add(target);
        sp.setAssociatedExitCells(spExits);

        addStaticPotential(sp);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(equalTo(sp)));
    }

    @Test(expected = IllegalStateException.class)
    public void noPotentialForTargetFails() {
        rule.execute(cell);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void twoPotentialsForOneCellFails() {
        StaticPotential sp1 = new StaticPotential();
        List<ExitCell> sp1Exits = new LinkedList<>();
        sp1Exits.add(target);
        sp1.setAssociatedExitCells(sp1Exits);

        StaticPotential sp2 = new StaticPotential();
        List<ExitCell> sp2Exits = new LinkedList<>();
        sp2Exits.add(target);
        sp2.setAssociatedExitCells(sp2Exits);

        addStaticPotential(sp1);
        addStaticPotential(sp2);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(equalTo(sp2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsIfNotAssigned() {
        test.become("special-case");
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i);
                will(returnValue(new IndividualProperty(i)));
                IndividualToExitMapping mapping = _unused -> null;
                allowing(es).getIndividualToExitMapping(); when(test.is("special-case"));
                will(returnValue(mapping));
            }});
        rule.execute(cell);
    }

    @Test
    public void deadIndividualWithoutTarget() {
        test.become("special-case");
        IndividualProperty ip = new IndividualProperty(i) {

            @Override
            public boolean isDead() {
                return true;
            }            
        };
        
        context.checking(new Expectations() {{
                allowing(es).propertyFor(i); when(test.is("special-case"));
                will(returnValue(ip));
                IndividualToExitMapping mapping = _unused -> null;
                allowing(es).getIndividualToExitMapping(); when(test.is("special-case"));
                will(returnValue(mapping));
            }});
        rule.execute(cell);
    }

    private void addStaticPotential(Potential p) {
        Exit e = new Exit("", Collections.singletonList(target));
        exitList.add(e);
        context.checking(new Expectations() {
            {
                allowing(eca).getPotentialFor(e);
                will(returnValue(p));
            }
        });
    }

}
