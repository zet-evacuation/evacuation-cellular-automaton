package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialPotentialRandomRuleTest {

    private final static IndividualBuilder INDIVIDUAL_BUILDER = new IndividualBuilder();
    private final Mockery context = new Mockery();
    private InitialPotentialRandomRule rule;
    private EvacCell cell;
    private Individual individual;
    private IndividualProperty ip;
    private EvacuationCellularAutomaton eca;
    private EvacuationStateControllerInterface ec;
    private EvacuationState es;
    private List<Exit> exitList;

    @Before
    public void init() {
        rule = new InitialPotentialRandomRule();
        Room room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        eca = context.mock(EvacuationCellularAutomaton.class);
        individual = INDIVIDUAL_BUILDER.build();
        ip = new IndividualProperty(individual);
        ec = context.mock(EvacuationStateControllerInterface.class);
        exitList = new LinkedList<>();
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).getXOffset();
                allowing(room).getYOffset();
                allowing(room).getFloor();
                allowing(es).getStatisticWriter();
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(individual);
                will(returnValue(ip));
                allowing(eca).getExits();
                will(returnValue(exitList));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        ip.setCell(cell);
        cell.getState().setIndividual(individual);

        rule.setEvacuationState(es);
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        individual = INDIVIDUAL_BUILDER.build();
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(ip));
            }
        });
        cell.getState().setIndividual(individual);
        assertThat(rule, is(executeableOn(cell)));
    }

    @Test
    public void testNotApplicableIfPotentialSet() {
        StaticPotential sp = new StaticPotential();
        ip.setStaticPotential(sp);
        assertThat(rule, is(not(executeableOn(cell))));
    }

    @Test
    public void testDeadIfNoPotentials() {
       DieAction a = (DieAction)rule.execute(cell).get();

        assertThat(a.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(a.getIndividual(), is(equalTo(individual)));
     }

    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        addStaticPotential(sp);

       DieAction a = (DieAction)rule.execute(cell).get();

        assertThat(a.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(a.getIndividual(), is(equalTo(individual)));
     }

    @Test
    public void testSinglePotentialTaken() {
        StaticPotential sp = new StaticPotential();
        sp.setPotential(cell, 1);

        addStaticPotential(sp);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(same(sp)));
    }

    @Test
    public void testRandomPotentialTaken() {
        // Need to insert seed into rule to manipulate random decision
//        rule.execute(cell);
//        assertThat(i.isDead(), is(false));
//        assertThat(i.getDeathCause(), is(nullValue()));
//        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }

    private void addStaticPotential(Potential p) {
        Exit e = new Exit("", Collections.emptyList());
        exitList.add(e);
        context.checking(new Expectations() {
            {
                allowing(eca).getPotentialFor(e);
                will(returnValue(p));
            }
        });
    }

}
