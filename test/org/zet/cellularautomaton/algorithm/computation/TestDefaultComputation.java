package org.zet.cellularautomaton.algorithm.computation;

import java.util.function.Function;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.jmock.AbstractExpectations.returnValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.IndividualProperty;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestDefaultComputation {
    private final Mockery context = new Mockery();
    private PropertyAccess es;
    private ParameterSet ps;
    private IndividualProperty pi;
    private Individual i = new Individual(0, 0, 0, 0, 0, 2, 1, 0);

    @Before
    public void init() {
        es = context.mock(PropertyAccess.class);
        ps = context.mock(ParameterSet.class);
        pi = new IndividualProperty(i);
        EvacCell roomCell = new RoomCell(0, 0);
        pi.setCell(roomCell);
        roomCell.getState().setIndividual(i);
    }

    @Test
    public void effectivePotentialWithoutDynamicPotential() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCell targetCell = new RoomCell(0, 0);

        StaticPotential sp = new StaticPotential();
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        pi.setStaticPotential(sp);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
                allowing(ps).staticPotentialWeight();
                will(returnValue(2.0));
            }
        });

        // Potential difference is 10, weight is 2
        assertThat(c.effectivePotential(i, targetCell, null), is(closeTo(-20.0, 10e-7)));
    }
    
    @Test
    public void effectivePotentialWithDynamicPotential() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCell targetCell = new RoomCell(0, 0);

        StaticPotential sp = new StaticPotential();
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        pi.setStaticPotential(sp);
        
        Function<EvacCell,Double> dynamicPotential = context.mock(Function.class);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
                allowing(ps).staticPotentialWeight();
                will(returnValue(2.0));
                allowing(ps).dynamicPotentialWeight();
                will(returnValue(3.0));
                
                allowing(dynamicPotential).apply(with(pi.getCell()));
                will(returnValue(4.0));
                allowing(dynamicPotential).apply(with(targetCell));
                will(returnValue(8.0));
            }
        });

        pi.setPanic(0.0);
        // static potential difference is 10, weight is 2
        assertThat(c.effectivePotential(i, targetCell, dynamicPotential), is(closeTo(-20.0, 0.02)));

        pi.setPanic(1.0);
        // dynamic potential difference is 4, weight is 3
        assertThat(c.effectivePotential(i, targetCell, dynamicPotential), is(closeTo(-12.0, 0.02)));

        pi.setPanic(0.5);
        // in between static and dynamic potential difference
        assertThat(c.effectivePotential(i, targetCell, dynamicPotential), is(closeTo(-16.0, 0.00001)));
    }
    
    @Test
    public void exhaustionLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.3);
        pi.setRelativeSpeed(0.5);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(i, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.3, 10e-7)));
    }

    @Test
    public void exhaustionUp() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.3);
        pi.setRelativeSpeed(0.7);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(i, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.7, 10e-7)));
    }

    @Test
    public void exhaustionUpLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.9);
        pi.setRelativeSpeed(0.8);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(i, new RoomCell(0,0));
        assertThat(resultingSpeed, is(lessThan(1.0)));
        assertThat(resultingSpeed, is(greaterThan(0.99)));
    }

    @Test
    public void exhaustionDown() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.3);
        pi.setRelativeSpeed(0.4);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(i, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.1, 10e-7)));
    }

    @Test
    public void exhaustionDownLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.1);
        pi.setRelativeSpeed(0.5);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(i);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(i, pi.getCell());
        assertThat(resultingSpeed, is(closeTo(0.0, 10e-7)));
    }
}
