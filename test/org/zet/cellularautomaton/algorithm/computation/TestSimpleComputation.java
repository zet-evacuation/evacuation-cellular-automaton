package org.zet.cellularautomaton.algorithm.computation;

import java.util.Collections;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.jmock.AbstractExpectations.returnValue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimpleComputation {

    @Test
    public void effectivePotential() {
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        EvacCell currentCell = new RoomCell(0, 0);
        EvacCell targetCell = new RoomCell(0, 0);

        StaticPotential sp = new StaticPotential();
        sp.setPotential(currentCell, 13);
        sp.setPotential(targetCell, 21);

        Mockery context = new Mockery();
        PropertyAccess es = context.mock(PropertyAccess.class);
        SimpleComputation sc = new SimpleComputation(es);
        IndividualProperty pi = new IndividualProperty(i);
        pi.setStaticPotential(sp);
        pi.setCell(currentCell);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        assertThat(sc.effectivePotential(i, targetCell, null), is(closeTo(-8.0, 10e-7)));
    }

    @Test
    public void defaultParameters() {
        Mockery context = new Mockery();

        SimpleComputation sc = new SimpleComputation(context.mock(PropertyAccess.class));
        Individual i = new Individual(0, 0, 0, 0, 0, 0, 1, 0);
        EvacCell cell = new RoomCell(0, 0);

        assertThat(sc.updatePreferredSpeed(i), is(closeTo(0.0, 10e-7)));
        assertThat(sc.updateExhaustion(i, cell), is(closeTo(0.0, 10e-7)));
        assertThat(sc.updatePanic(i, cell, Collections.emptyList()), is(closeTo(0.0, 10e-7)));
        assertThat(sc.changePotentialThreshold(i), is(closeTo(0.0, 10e-7)));
    }

    @Test
    public void idleThreshold() {
        Individual i = new Individual(0, 0, 0, 0, 8, 0, 1, 0);
        Mockery context = new Mockery();
        SimpleComputation sc = new SimpleComputation(context.mock(PropertyAccess.class));

        assertThat(sc.idleThreshold(i), is(closeTo(3.2, 10e-7)));
    }

}
