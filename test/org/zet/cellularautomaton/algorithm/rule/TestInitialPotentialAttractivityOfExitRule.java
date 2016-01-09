package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import org.zet.cellularautomaton.algorithm.EvacuationState;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.algorithm.IndividualState;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestInitialPotentialAttractivityOfExitRule {
    private final Mockery context = new Mockery();
    private InitialPotentialAttractivityOfExitRule rule;
    private EvacCell cell;
    private Individual individual;
    private EvacuationCellularAutomaton eca;
    private EvacuationState es;
    private IndividualState is;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();
    private final static IndividualBuilder builder = new IndividualBuilder();

    @Before
    public void init() {
        rule = new InitialPotentialAttractivityOfExitRule();
        Room room = context.mock(Room.class);
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        es = context.mock(EvacuationState.class);
        is = new IndividualState();
        eca = new EvacuationCellularAutomaton();
        individual = builder.build();
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

        rule.setEvacuationSimulationProblem(es);
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
        rule.execute(cell);
        assertThat(is.isDead(individual), is(true));
        assertThat(is.getDeathCause(individual), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
    }

    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        eca.addStaticPotential(sp);

        rule.execute(cell);
        assertThat(is.isDead(individual), is(true));
        assertThat(is.getDeathCause(individual), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
    }
    
    @Test
    public void testSinglePotentialTaken() {
        StaticPotential sp = new StaticPotential();
        sp.setPotential(cell, 1);
        eca.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(is.isDead(individual), is(false));
        assertThat(is.getDeathCause(individual), is(nullValue()));
        assertThat(individual.getStaticPotential(), is(same(sp)));
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
        assertThat(individual.getStaticPotential(), is(same(mostAttractive)));
    }
    
}
