/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm.computation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.jmock.AbstractExpectations.returnValue;
import java.util.Collections;
import java.util.function.Function;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellState;
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
public class DefaultComputationTest {
    private final Mockery context = new Mockery();
    private PropertyAccess es;
    private ParameterSet ps;
    private IndividualProperty pi;
    private final int INDIVIDUAL_SLACKNESS = 3;
    private final double INDIVIDUAL_MAX_SPEED = 0.75;
    private final double INDIVIDUAL_PANIC_FACTOR = 0.6;
    private final Individual individual = new Individual(0, 0, 0, INDIVIDUAL_PANIC_FACTOR, INDIVIDUAL_SLACKNESS, 2,
            INDIVIDUAL_MAX_SPEED, 0);

    @Before
    public void init() {
        es = context.mock(PropertyAccess.class);
        ps = context.mock(ParameterSet.class);
        pi = new IndividualProperty(individual);
        EvacCellInterface roomCell = context.mock(EvacCellInterface.class, "roomCell");
        pi.setCell(roomCell);
        EvacuationCellState state = new EvacuationCellState(individual);
        context.checking(new Expectations() {
                {
                    allowing(roomCell).getState();
                    will(returnValue(state));
                    //room
                }
        });
        //roomCell.getState().setIndividual(individual);
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
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
                allowing(ps).staticPotentialWeight();
                will(returnValue(2.0));
            }
        });

        // Potential difference is 10, weight is 2
        assertThat(c.effectivePotential(individual, targetCell, null), is(closeTo(-20.0, 10e-7)));
    }
    
    @Test
    public void effectivePotentialWithDynamicPotential() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCell targetCell = new RoomCell(0, 0);

        StaticPotential sp = new StaticPotential();
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        pi.setStaticPotential(sp);
        
        Function<EvacCellInterface,Double> dynamicPotential = context.mock(Function.class);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
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
        assertThat(c.effectivePotential(individual, targetCell, dynamicPotential), is(closeTo(-20.0, 0.02)));

        pi.setPanic(1.0);
        // dynamic potential difference is 4, weight is 3
        assertThat(c.effectivePotential(individual, targetCell, dynamicPotential), is(closeTo(-12.0, 0.02)));

        pi.setPanic(0.5);
        // in between static and dynamic potential difference
        assertThat(c.effectivePotential(individual, targetCell, dynamicPotential), is(closeTo(-16.0, 0.00001)));
    }
    
    @Test
    public void exhaustionLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.3);
        pi.setRelativeSpeed(0.5);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(individual, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.63333333, 10e-7)));
    }

    @Test
    public void exhaustionUp() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.3);
        pi.setRelativeSpeed(0.6);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(individual, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.9, 10e-7)));
    }

    @Test
    public void exhaustionUpLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.9);
        pi.setRelativeSpeed(0.8);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(individual, new RoomCell(0,0));
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
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(individual, new RoomCell(0, 0));
        assertThat(resultingSpeed, is(closeTo(0.3666666, 10e-7)));
    }

    @Test
    public void exhaustionDownLimited() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        pi.setExhaustion(0.1);
        pi.setRelativeSpeed(0.5);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
            }
        });

        double resultingSpeed = c.updateExhaustion(individual, pi.getCell());
        assertThat(resultingSpeed, is(closeTo(0.0, 10e-7)));
    }
    
    @Test
    public void preferredSpeedCorrectInitial() {
        DefaultComputation c = new DefaultComputation(es, ps);

        expectationsForPreferredSpeed();

        assertThat(c.updatePreferredSpeed(individual), is(closeTo(INDIVIDUAL_MAX_SPEED, 10e-7)));
    }
    
    @Test
    public void preferredSpeedWithExhaustionDecrease() {
        DefaultComputation c = new DefaultComputation(es, ps);

        expectationsForPreferredSpeed();
        
        pi.setExhaustion(0.5);
        
        final double expectedDecrease = 0.5 * 0.8;

        assertThat(c.updatePreferredSpeed(individual), is(closeTo(INDIVIDUAL_MAX_SPEED - expectedDecrease, 10e-7)));
    }
    
    @Test
    public void preferredSpeedWithDecreaseAndIncrease() {
        DefaultComputation c = new DefaultComputation(es, ps);

        expectationsForPreferredSpeed();
        
        pi.setExhaustion(0.5);
        pi.setPanic(0.5);
        
        final double expectedDecrease = 0.5 * 0.8;
        final double expectedIncrease = 0.5 * 0.5;

        assertThat(c.updatePreferredSpeed(individual),
                is(closeTo(INDIVIDUAL_MAX_SPEED - expectedDecrease + expectedIncrease, 10e-7)));
    }
    
    @Test
    public void preferredSpeedBoundedBelow() {
        DefaultComputation c = new DefaultComputation(es, ps);

        expectationsForPreferredSpeed();
        
        pi.setExhaustion(1.0);
        
        assertThat(c.updatePreferredSpeed(individual), is(closeTo(0, 10e-5)));
    }
    
    @Test
    public void preferredSpeedBoundedAbove() {
        DefaultComputation c = new DefaultComputation(es, ps);

        expectationsForPreferredSpeed();
        
        pi.setExhaustion(0.1);
        pi.setPanic(1);
        
        assertThat(c.updatePreferredSpeed(individual), is(closeTo(INDIVIDUAL_MAX_SPEED, 10e-5)));
    }
    
    private void expectationsForPreferredSpeed() {
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));
                
                allowing(ps).panicWeightOnSpeed();
                will(returnValue(0.5));
                allowing(ps).exhaustionWeightOnSpeed();
                will(returnValue(0.8));
            }
        });
    }
    
    @Test
    public void panicRemainsIfNothingPossible() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCellInterface roomCell = pi.getCell();
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class);
 
        final double expectedPanic = 0.4;
        pi.setPanic(expectedPanic);
        
        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));

                allowing(roomCell).getNeighbours();
                will(returnValue(Collections.emptyList()));
            }
        });

        assertThat(c.updatePanic(individual, targetCell, Collections.emptyList()), is(closeTo(expectedPanic, 10e-7)));
    }
    
    @Test
    public void panicForOneNeighborMoreThanThresholdFailures() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCellInterface roomCell = pi.getCell();
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "targetCell");
 
        final double expectedPanic = 0.4;
        pi.setPanic(expectedPanic);
        
        StaticPotential sp = new StaticPotential();
        pi.setStaticPotential(sp);
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));

                allowing(roomCell).getNeighbours();
                will(returnValue(Collections.singletonList(targetCell)));
                
                allowing(targetCell).getState();
                will(returnValue(new EvacuationCellState(null)));
                
                allowing(ps).getPanicThreshold();
                will(returnValue(0d));
                allowing(ps).getPanicDecrease();
                will(returnValue(1d));
                allowing(ps).getPanicIncrease();
                will(returnValue(1d));
            }
        });

        assertThat(c.updatePanic(individual, targetCell, Collections.emptyList()), is(closeTo(expectedPanic, 10e-7)));
    }

    @Test
    public void panicForOneNeighborLessThanThresholdFailures() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCellInterface roomCell = pi.getCell();
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "targetCell");
 
        final double startPanic = 0.6;
        pi.setPanic(startPanic);
        
        StaticPotential sp = new StaticPotential();
        pi.setStaticPotential(sp);
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));

                allowing(roomCell).getNeighbours();
                will(returnValue(Collections.singletonList(targetCell)));
                
                allowing(targetCell).getState();
                will(returnValue(new EvacuationCellState(null)));
                
                allowing(ps).getPanicThreshold();
                will(returnValue(0.5d));
                allowing(ps).getPanicDecrease();
                will(returnValue(1.5d));
            }
        });

        final double panicDecrease = INDIVIDUAL_PANIC_FACTOR * 1.5 * (1 - 0.5);
        assertThat(panicDecrease, is(greaterThan(0.0)));

        assertThat(c.updatePanic(individual, targetCell, Collections.emptyList()), is(closeTo(startPanic - panicDecrease, 10e-7)));
    }

    @Test
    public void panicForOneNeighborOccupied() {
        DefaultComputation c = new DefaultComputation(es, ps);
        
        EvacCellInterface roomCell = pi.getCell();
        EvacCellInterface targetCell = context.mock(EvacCellInterface.class, "targetCell");
 
        final double startPanic = 0.6;
        pi.setPanic(startPanic);
        
        StaticPotential sp = new StaticPotential();
        pi.setStaticPotential(sp);
        sp.setPotential(pi.getCell(), 0);
        sp.setPotential(targetCell, 10);

        context.checking(new Expectations() {
            {
                allowing(es).propertyFor(individual);
                will(returnValue(pi));

                allowing(roomCell).getNeighbours();
                will(returnValue(Collections.singletonList(targetCell)));
                
                allowing(targetCell).getState();
                will(returnValue(new EvacuationCellState(individual)));
                
                allowing(ps).getPanicThreshold();
                will(returnValue(0.5d));
                allowing(ps).getPanicDecrease();
                will(returnValue(1.5d));
                allowing(ps).getPanicIncrease();
                will(returnValue(1d));
            }
        });

        final double panicIncrease = INDIVIDUAL_PANIC_FACTOR * 1 * (1 - 0.5);
        assertThat(panicIncrease, is(greaterThan(0.0)));

        assertThat(c.updatePanic(individual, targetCell, Collections.emptyList()), is(closeTo(startPanic + panicIncrease, 10e-7)));
    }
    
    @Test
    public void defaultSlacknessToIdleRatio() {
        // Default idle ratio is multiplicates the individual's slackness with the idle ratio
        DefaultComputation c = new DefaultComputation(es, ps);
        
        context.checking(new Expectations() {
            {
                allowing(ps).slacknessToIdleRatio();
                will(returnValue(0.75));
            }
        });
        
        assertThat(c.idleThreshold(individual), is(closeTo(2.25, 10e-7)));
    }
}
