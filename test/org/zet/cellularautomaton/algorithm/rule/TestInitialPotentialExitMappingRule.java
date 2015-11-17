package org.zet.cellularautomaton.algorithm.rule;

import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.jmock.AbstractExpectations.any;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.IndividualToExitMapping;
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
public class TestInitialPotentialExitMappingRule {

    private final Mockery context = new Mockery();
    InitialPotentialExitMappingRule rule;
    EvacCell cell;
    Individual i;
    EvacuationCellularAutomaton eca;
    private final static CAStatisticWriter statisticWriter = new CAStatisticWriter();
    EvacuationSimulationProblem p;
    ExitCell target;
    
    private final IndividualToExitMapping exitMapping = (Individual individual) -> {
        if (individual == TestInitialPotentialExitMappingRule.this.i) {
            return target;
        }
        throw new IllegalStateException("Called with bad individual.");
    };

    @Before
    public void init() {
        rule = new InitialPotentialExitMappingRule();
        Room room = context.mock(Room.class);
        p = context.mock(EvacuationSimulationProblem.class);
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

        target = new ExitCell(1.0, 0, 0, room);
    
    }

    @Test
    public void executableEvenIfPotentialAssigned() {
        StaticPotential sp = new StaticPotential();
        i.setStaticPotential(sp);
        assertThat(rule.executableOn(cell), is(true));
    }

    @Test
    public void testAppliccableIfNotEmpty() {
        cell = new RoomCell(0, 0);
        assertThat(rule.executableOn(cell), is(false));

        Individual i = new Individual();
        cell.getState().setIndividual(i);
        assertThat(rule.executableOn(cell), is(true));
    }

    @Test
    public void assignedExitsAreAssigned() {
        p.getCa().setIndividualToExitMapping(exitMapping);
        
        StaticPotential sp = new StaticPotential();
        List<ExitCell> spExits = new LinkedList<>();
        spExits.add(target);
        sp.setAssociatedExitCells(spExits);

        PotentialManager pm = eca.getPotentialManager();
        pm.addStaticPotential(sp);
        
        rule.execute(cell);
        assertThat(i.getStaticPotential(), is(equalTo(sp)));
    }


    @Test(expected = IllegalStateException.class)
    public void noPotentialForTargetFails() {
        p.getCa().setIndividualToExitMapping(exitMapping);
        rule.execute(cell);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void twoPotentialsForOneCellFails() {
        p.getCa().setIndividualToExitMapping(exitMapping);
        
        StaticPotential sp1 = new StaticPotential();
        List<ExitCell> sp1Exits = new LinkedList<>();
        sp1Exits.add(target);
        sp1.setAssociatedExitCells(sp1Exits);
        
        StaticPotential sp2 = new StaticPotential();
        List<ExitCell> sp2Exits = new LinkedList<>();
        sp2Exits.add(target);
        sp2.setAssociatedExitCells(sp2Exits);
        
        PotentialManager pm = eca.getPotentialManager();
        pm.addStaticPotential(sp1);
        pm.addStaticPotential(sp2);
        
        rule.execute(cell);
        assertThat(i.getStaticPotential(), is(equalTo(sp2)));
    }
    
    @Test
    public void fallbackShortest() {
        StaticPotential shortDistance = new StaticPotential();
        StaticPotential longDistance = new StaticPotential();
        shortDistance.setDistance(cell, 1);
        longDistance.setDistance(cell, 2);
        
        IndividualToExitMapping mapping = _unused -> null;
        p.getCa().setIndividualToExitMapping(mapping);
        
        PotentialManager pm = eca.getPotentialManager();
        pm.addStaticPotential(longDistance);
        pm.addStaticPotential(shortDistance);
        
        rule.execute(cell);
        assertThat(i.getStaticPotential(), is(equalTo(shortDistance)));
    }

}
