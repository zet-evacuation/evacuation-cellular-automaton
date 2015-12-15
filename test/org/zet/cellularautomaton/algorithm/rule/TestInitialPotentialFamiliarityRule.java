package org.zet.cellularautomaton.algorithm.rule;

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
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.potential.PotentialManager;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestInitialPotentialFamiliarityRule {
    private final Mockery context = new Mockery();
    private InitialPotentialFamiliarityRule rule;
    private EvacCell cell;
    private Individual individual;
    private EvacuationCellularAutomaton eca;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();

    @Before
    public void init() {
        rule = new InitialPotentialFamiliarityRule();
        Room room = context.mock(Room.class);
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        eca = new EvacuationCellularAutomaton();
        individual = new Individual();
        context.checking(new Expectations() {
            {
                allowing(p).getCellularAutomaton();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(individual));
                allowing(room).removeIndividual(with(individual));
                allowing(p).getStatisticWriter();
                will(returnValue(statisticWriter));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        individual.setCell(cell);
        cell.getState().setIndividual(individual);

        rule.setEvacuationSimulationProblem(p);
        eca.addIndividual(cell, individual);
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        individual = new Individual();
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
        assertThat(individual.isDead(), is(true));
        assertThat(individual.getDeathCause(), is(DeathCause.EXIT_UNREACHABLE));
    }

    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        PotentialManager pm = eca.getPotentialManager();

        pm.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(individual.isDead(), is(true));
        assertThat(individual.getDeathCause(), is(DeathCause.EXIT_UNREACHABLE));
    }

    @Test
    public void testSinglePotentialTaken() {
        StaticPotential sp = new StaticPotential();
        PotentialManager pm = eca.getPotentialManager();
        sp.setPotential(cell, 1);

        pm.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(individual.isDead(), is(false));
        assertThat(individual.getDeathCause(), is(nullValue()));
        assertThat(individual.getStaticPotential(), is(same(sp)));
    }

    @Test
    public void testFirstTaken() {
        StaticPotential longPotential1 = new StaticPotential();
        longPotential1.setPotential(cell, 3);
        StaticPotential longPotential2 = new StaticPotential();
        longPotential2.setPotential(cell, 4);
        StaticPotential shortestPotential = new StaticPotential();
        shortestPotential.setPotential(cell, 2);
        PotentialManager pm = eca.getPotentialManager();

        pm.addStaticPotential(longPotential1);
        pm.addStaticPotential(longPotential2);
        pm.addStaticPotential(shortestPotential);

        individual.setFamiliarity(0.667);
        
        rule.execute(cell);
        assertThat(individual.getStaticPotential(), is(same(shortestPotential)));
    }
    
}
