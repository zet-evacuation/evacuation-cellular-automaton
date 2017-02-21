package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.junit.Assert.assertThat;
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
public class InitialPotentialShortestPathRuleTest {
    private final Mockery context = new Mockery();
    private InitialPotentialShortestPathRule rule;
    private EvacCell cell;
    private Individual individual;
    private IndividualProperty ip;
    private EvacuationStateControllerInterface ec;
    private EvacuationCellularAutomaton eca;
    private EvacuationState es;
    private final static IndividualBuilder INDIVIDUAL = new IndividualBuilder();
    private final Exit exit = new Exit("", Collections.emptyList());
    private List<Exit> exitList;
    
    @Before
    public void init() {
        rule = new InitialPotentialShortestPathRule();
        Room room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        eca = context.mock(EvacuationCellularAutomaton.class);
        individual = INDIVIDUAL.build();
        ip = new IndividualProperty(individual);                
        ec = context.mock(EvacuationStateControllerInterface.class);
        exitList = new LinkedList<>();
        exitList.add(exit);
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

        individual = INDIVIDUAL.build();
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
        exitList.clear();
        DieAction a = (DieAction) rule.execute(cell).get();

        assertThat(a.getDeathCause(), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
        assertThat(a.getIndividual(), is(equalTo(individual)));

    }

    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();

        addStaticPotential(sp);

        DieAction a = (DieAction) rule.execute(cell).get();

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
    public void testShortestPotentialTaken() {
        StaticPotential targetPotential = initPotential();
        
        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(same(targetPotential)));
    }
    
    private StaticPotential initPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);

        addStaticPotential(longDistance);
        addStaticPotential(new Exit("", Collections.emptyList()), shortDistance);
        addStaticPotential(new Exit("", Collections.emptyList()), mediumDistance);
        return shortDistance;
    }
    
    private void addStaticPotential(Potential p) {
        context.checking(new Expectations() {{
            allowing(eca).getPotentialFor(exit);
            will(returnValue(p));
        }});
    }
    
    private void addStaticPotential(Exit e, Potential p) {
        exitList.add(e);
        context.checking(new Expectations() {{
            allowing(eca).getPotentialFor(e);
            will(returnValue(p));
        }});
    }
    
}
