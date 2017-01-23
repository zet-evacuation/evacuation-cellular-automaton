package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
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
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialPotentialAttractivityOfExitRuleTest {
    private final Mockery context = new Mockery();
    private InitialPotentialAttractivityOfExitRule rule;
    private IndividualProperty ip;
    private EvacCell cell;
    private Individual individual;
    private EvacuationCellularAutomaton eca;
    private EvacuationState es;
    private EvacuationStateControllerInterface ec;
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Before
    public void init() {
        rule = new InitialPotentialAttractivityOfExitRule();
        Room room = context.mock(Room.class);
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        es = context.mock(EvacuationState.class);
        eca = new EvacuationCellularAutomaton();
        individual = builder.build();
        ip = new IndividualProperty(individual);
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
                will(returnValue(new CAStatisticWriter(es)));
                allowing(es).propertyFor(individual);
                will(returnValue(ip));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        ip.setCell(cell);
        cell.getState().setIndividual(individual);

        rule.setEvacuationState(es);
        rule.setEvacuationStateController(ec);
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        individual = builder.build();
        ip = new IndividualProperty(individual);
        cell.getState().setIndividual(individual);
        context.checking(new Expectations() {{
                allowing(es).propertyFor(individual);
                will(returnValue(ip));
            }});
        
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
        context.checking(new Expectations() {{
                exactly(1).of(ec).die(individual, DeathCause.EXIT_UNREACHABLE);
            }});
        rule.execute(cell);
        context.assertIsSatisfied();
    }

    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        eca.addStaticPotential(sp);

        context.checking(new Expectations() {{
                exactly(1).of(ec).die(individual, DeathCause.EXIT_UNREACHABLE);
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
        assertThat(ip.isDead(), is(false));
        assertThat(ip.getStaticPotential(), is(same(sp)));
    }

    @Test
    public void mostAttractiveTaken() {
        StaticPotential unattractive1 = new StaticPotential();
        unattractive1.setPotential(cell, 1);
        unattractive1.setAttractivity(100);
        StaticPotential unattractive2 = new StaticPotential();
        unattractive1.setPotential(cell, 1);
        unattractive1.setAttractivity(100);
        StaticPotential mostAttractive = new StaticPotential();
        mostAttractive.setPotential(cell, 1);
        mostAttractive.setAttractivity(200);
        StaticPotential unreachable = new StaticPotential();

        eca.addStaticPotential(unattractive1);
        eca.addStaticPotential(mostAttractive);
        eca.addStaticPotential(unattractive2);
        eca.addStaticPotential(unreachable);

        rule.execute(cell);
        assertThat(ip.getStaticPotential(), is(same(mostAttractive)));
    }
    
}
