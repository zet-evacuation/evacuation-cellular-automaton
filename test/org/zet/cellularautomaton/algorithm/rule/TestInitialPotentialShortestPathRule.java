package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
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
public class TestInitialPotentialShortestPathRule {
    private final Mockery context = new Mockery();
    InitialPotentialShortestPathRule rule;
    EvacCell cell;
    Individual i;
    EvacuationCellularAutomaton eca;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();
    
    @Before
    public void init() {
        rule = new InitialPotentialShortestPathRule();
        Room room = context.mock(Room.class);
        EvacuationSimulationProblem p = context.mock(EvacuationSimulationProblem.class);
        eca = new EvacuationCellularAutomaton();
        i = new Individual();
        context.checking(new Expectations() {
            {
                allowing(p).getCa();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(i));
                allowing(room).removeIndividual(with(i));
                allowing(p).getStatisticWriter();
                will(returnValue(statisticWriter));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        i.setCell(cell);
        cell.getState().setIndividual(i);

        rule.setEvacuationSimulationProblem(p);
        eca.addIndividual(cell, i);
    }
    
    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule.executableOn(cell), is(false));

        i = new Individual();
        cell.getState().setIndividual(i);
        assertThat(rule.executableOn(cell), is(true));
    }
    
    @Test
    public void testNotApplicableIfPotentialSet() {
        StaticPotential sp = new StaticPotential();
        i.setStaticPotential(sp);
        assertThat(rule.executableOn(cell), is(false));
    }
    
    @Test
    public void testDeadIfNoPotentials() {
        rule.execute(cell);
        assertThat(i.isDead(), is(true));
        assertThat(i.getDeathCause(), is(DeathCause.EXIT_UNREACHABLE));
    }
    
    @Test
    public void testDeadIfPotentialsBad() {
        StaticPotential sp = new StaticPotential();
        PotentialManager pm = eca.getPotentialManager();

        pm.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(i.isDead(), is(true));
        assertThat(i.getDeathCause(), is(DeathCause.EXIT_UNREACHABLE));
    }
    
    @Test
    public void testSinglePotentialTaken() {
        StaticPotential sp = new StaticPotential();
        PotentialManager pm = eca.getPotentialManager();
        sp.setDistance(cell, 1);

        pm.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(i.isDead(), is(false));
        assertThat(i.getDeathCause(), is(nullValue()));
        assertThat(i.getStaticPotential(), is(same(sp)));
    }
    
    @Test
    public void testShortestPotentialTaken() {
        StaticPotential targetPotential = initPotential( eca.getPotentialManager());
        
        rule.execute(cell);
        assertThat(i.isDead(), is(false));
        assertThat(i.getDeathCause(), is(nullValue()));
        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }
    
    private StaticPotential initPotential(PotentialManager pm) {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setDistance(cell, 1);
        mediumDistance.setDistance(cell, 2);
        longDistance.setDistance(cell, 3);

        pm.addStaticPotential(longDistance);
        pm.addStaticPotential(shortDistance);
        pm.addStaticPotential(mediumDistance);
        return shortDistance;
    }
}
