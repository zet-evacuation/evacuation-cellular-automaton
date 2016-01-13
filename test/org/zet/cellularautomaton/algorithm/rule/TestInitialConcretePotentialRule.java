package org.zet.cellularautomaton.algorithm.rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import static org.jmock.AbstractExpectations.same;
import static org.zet.cellularautomaton.algorithm.rule.RuleTestMatchers.executeableOn;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.EvacuationState;
import org.zet.cellularautomaton.algorithm.IndividualState;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestInitialConcretePotentialRule {
    private final Mockery context = new Mockery();
    InitialConcretePotentialRule rule;
    EvacCell cell;
    Individual i;
    EvacuationCellularAutomaton eca;
    EvacuationState es;
    TestIndividualState is;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();
    private final static IndividualBuilder builder = new IndividualBuilder();
    
    static class TestIndividualState extends IndividualState {
        @Override
        protected void addIndividual(Individual i) {
            super.addIndividual(i);
        }
    }
    
    private void init() {
        init(0);
    }
    
    private void init(double familiarity) {
        rule = new InitialConcretePotentialRule();
        Room room = context.mock(Room.class);
        es = context.mock(EvacuationState.class);
        is = new TestIndividualState();
        eca = new EvacuationCellularAutomaton();
        i = builder.withAge(30).withFamiliarity(familiarity).build();
        is.addIndividual(i);
        context.checking(new Expectations() {
            {
                allowing(es).getCellularAutomaton();
                will(returnValue(eca));
                allowing(room).getID();
                will(returnValue(1));
                allowing(room).addIndividual(with(any(EvacCell.class)), with(i));
                allowing(room).removeIndividual(with(i));
                allowing(es).getStatisticWriter();
                will(returnValue(statisticWriter));
                allowing(es).getIndividualState();
                will(returnValue(is));
            }
        });
        cell = new RoomCell(1, 0, 0, room);
        i.setCell(cell);
        cell.getState().setIndividual(i);

        rule.setEvacuationSimulationProblem(es);
        eca.addIndividual(cell, i);
    }
    
    @Test
    public void testAppliccableIfNotEmpty() {
        init();
        cell = new RoomCell(0, 0);
        assertThat(rule, is(not(executeableOn(cell))));

        cell.getState().setIndividual(i);
        assertThat(rule, is(executeableOn(cell)));
    }
    
    @Test
    public void testNotApplicableIfPotentialSet() {
        init();
        StaticPotential sp = new StaticPotential();
        i.setStaticPotential(sp);
        assertThat(rule, is(not(executeableOn(cell))));
    }
    
    @Test
    public void testDeadIfNoPotentials() {
        init();
        rule.execute(cell);
        assertThat(is.isDead(i), is(true));
        assertThat(is.getDeathCause(i), is(equalTo(DeathCause.EXIT_UNREACHABLE)));
    }
    
    @Test
    public void testDeadIfPotentialsBad() {
        init();
        StaticPotential sp = new StaticPotential();

        eca.addStaticPotential(sp);
        rule.execute(cell);
        assertThat(is.isDead(i), is(true));
        assertThat(is.getDeathCause(i), is(equalTo(DeathCause.EXIT_UNREACHABLE)));        
    }
    
    @Test
    public void testSinglePotentialTaken() {
        init();
        StaticPotential sp = new StaticPotential();
        sp.setPotential(cell, 1);

        eca.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(is.isDead(i), is(false));
        assertThat(i.getStaticPotential(), is(same(sp)));
    }
    
    @Test
    public void testHighFamiliarityChoosesBest() {
        init(1);
        StaticPotential targetPotential = initFamiliarPotential();
        
        rule.execute(cell);
        assertThat(is.isDead(i), is(false));
        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }
    
    @Test
    public void testLowFamiliarityChoosesAttractive() {
        init(0);
        StaticPotential targetPotential = initUnfamiliarPotential();
        
        rule.execute(cell);
        assertThat(is.isDead(i), is(false));
        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }
    
    @Test
    public void testMediumFamiliarity() {
        init(0.5);
        StaticPotential targetPotential = initMediumPotential();
        
        rule.execute(cell);
        assertThat(is.isDead(i), is(false));
        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }
    
    @Test
    public void testAttractivePotentialShort() {
        init(0.5);
        StaticPotential targetPotential = initAttractiveShortPotential();
        
        rule.execute(cell);
        assertThat(is.isDead(i), is(false));
        assertThat(i.getStaticPotential(), is(same(targetPotential)));
    }
    
    private StaticPotential initFamiliarPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
        
        shortDistance.setAttractivity(10);
        mediumDistance.setAttractivity(50);
        longDistance.setAttractivity(100);

        eca.addStaticPotential(longDistance);
        eca.addStaticPotential(shortDistance);
        eca.addStaticPotential(mediumDistance);
        return shortDistance;
    }

    private StaticPotential initUnfamiliarPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
        shortDistance.setAttractivity(10);
        mediumDistance.setAttractivity(50);
        longDistance.setAttractivity(100);

        eca.addStaticPotential(longDistance);
        eca.addStaticPotential(shortDistance);
        eca.addStaticPotential(mediumDistance);
        return longDistance;
    }

    private StaticPotential initMediumPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
        shortDistance.setAttractivity(10);
        mediumDistance.setAttractivity(50);
        longDistance.setAttractivity(100);

        eca.addStaticPotential(longDistance);
        eca.addStaticPotential(shortDistance);
        eca.addStaticPotential(mediumDistance);
        return mediumDistance;
    }

    private StaticPotential initAttractiveShortPotential() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential mediumDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setPotential(cell, 1);
        mediumDistance.setPotential(cell, 2);
        longDistance.setPotential(cell, 3);
        shortDistance.setAttractivity(1000);
        mediumDistance.setAttractivity(50);
        longDistance.setAttractivity(100);

        eca.addStaticPotential(longDistance);
        eca.addStaticPotential(shortDistance);
        eca.addStaticPotential(mediumDistance);
        return shortDistance;
    }
}
