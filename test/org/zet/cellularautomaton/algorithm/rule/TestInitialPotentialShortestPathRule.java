package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.junit.Assert.assertThat;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationState;
import org.zet.cellularautomaton.algorithm.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.rule.TestInitialConcretePotentialRule.TestIndividualState;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestInitialPotentialShortestPathRule {
    private final Mockery context = new Mockery();
    private InitialPotentialShortestPathRule rule;
    private EvacCell cell;
    private Individual individual;
    private TestIndividualState is;
    private EvacuationStateControllerInterface ec;
    private EvacuationCellularAutomaton eca;
    private EvacuationState es;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();
    private final static IndividualBuilder builder = new IndividualBuilder();
    
    @Before
    public void init() {
        rule = new InitialPotentialShortestPathRule();
        Room room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        is = new TestIndividualState();
        eca = new EvacuationCellularAutomaton();
        individual = builder.build();
        is.addIndividual(individual);
        ec = context.mock(EvacuationStateControllerInterface.class);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(individual));
                allowing(room).removeIndividual(with(individual));
                allowing(es).getStatisticWriter();
                will(returnValue(statisticWriter));
                allowing(es).getIndividualState();
                will(returnValue(is));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        individual.setCell(cell);
        cell.getState().setIndividual(individual);

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);
        eca.addIndividual(cell, individual);
    }
    
    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        individual = builder.build();
        cell.getState().setIndividual(individual);
        assertThat(rule, is(executeableOn(cell)));
    }
    
    @Test
    public void testNotApplicableIfPotentialSet() {
        StaticPotential sp = new StaticPotential();
        individual.setStaticPotential(sp);
        assertThat(rule, is(not(executeableOn(cell))));
    }
    
    @Test
    public void testDeadIfNoPotentials() {
        context.checking(new Expectations() {{
                exactly(1).of(ec).die(with(individual), with(DeathCause.EXIT_UNREACHABLE));
        }});
        rule.execute(cell);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        eca.addStaticPotential(sp);
        
        context.checking(new Expectations() {{
                exactly(1).of(ec).die(with(individual), with(DeathCause.EXIT_UNREACHABLE));
        }});
        rule.execute(cell);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testSinglePotentialTaken() {
        StaticPotential sp = new StaticPotential();
        sp.setPotential(cell, 1);

        eca.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(individual.getStaticPotential(), is(same(sp)));
    }
    
    @Test
    public void testShortestPotentialTaken() {
        StaticPotential targetPotential = initPotential();
        
        rule.execute(cell);
        assertThat(individual.getStaticPotential(), is(same(targetPotential)));
    }
    
    private StaticPotential initPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);

        eca.addStaticPotential(longDistance);
        eca.addStaticPotential(shortDistance);
        eca.addStaticPotential(mediumDistance);
        return shortDistance;
    }
}
