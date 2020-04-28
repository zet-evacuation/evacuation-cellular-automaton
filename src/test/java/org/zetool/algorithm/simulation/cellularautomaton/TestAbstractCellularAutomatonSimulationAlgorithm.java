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
package org.zetool.algorithm.simulation.cellularautomaton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jmock.Mockery;
import org.junit.Test;
import org.zetool.simulation.cellularautomaton.Cell;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestAbstractCellularAutomatonSimulationAlgorithm {
    private final Mockery context = new Mockery();

    private static class FakeAbstractCellularAutomatonSimulationAlgorithm
            extends AbstractCellularAutomatonSimulationAlgorithm<CellularAutomaton<Cell<Object>>, Cell<Object>,
            CellularAutomatonSimulationProblem<CellularAutomaton<Cell<Object>>, Cell<Object>>, Integer> {

        @Override
        protected void initialize() {
            
        }

        @Override
        protected void execute(Cell cell) {
            
        }

        @Override
        protected Integer terminate() {
            return 0;
        }

        @Override
        public Iterator iterator() {
            return null;
        }

    }
 
    
    @Test
    public void testPerformStepCallsAllCells() {
        Cell<Object> c1 = context.mock(Cell.class, "c1");
        Cell<Object> c2 = context.mock(Cell.class, "c2");
        Cell<Object> c3 = context.mock(Cell.class, "c3");
        List<Cell<Object>> cells = new LinkedList<>();
        cells.add(c1);
        cells.add(c2);
        cells.add(c3);
        final Map<Cell<Object>,Boolean> executedCells = new HashMap();
        
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm() {

            @Override
            public Iterator iterator() {
                return cells.iterator();
            }

            @Override
            protected void execute(Cell cell) {
                executedCells.put(cell, Boolean.TRUE);
            }
        };
        algorithm.performStep();
        assertThat(executedCells.size(), is(equalTo(3)));
        assertThat(executedCells.get(c1), is(true));
        assertThat(executedCells.get(c2), is(true));
        assertThat(executedCells.get(c3), is(true));
    }
    
    @Test
    public void testPerformUntilFinishedTrue() {
        
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm() {
            int finishCounter = 0;
            int performedSteps = 0;

            @Override
            protected boolean isFinished() {
                return finishCounter++ == 3;
            }

            @Override
            protected void performStep() {
                performedSteps++;
            }

            @Override
            protected Integer terminate() {
                return performedSteps;
            }
        };

        CellularAutomatonSimulationProblem<CellularAutomaton<Cell<Object>>, Cell<Object>> p = context.mock(CellularAutomatonSimulationProblem.class);
        algorithm.setProblem(p);
        algorithm.runAlgorithm();
        assertThat(algorithm.getSolution(), is(equalTo(3)));
    }
    
    @Test
    public void testFinishedAfterTime() {
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm() {

            @Override
            protected void performStep() {
                increaseStep();
            }
            
        };
        algorithm.setMaxSteps(2);
        assertThat(algorithm.isFinished(), is(false));
        algorithm.performStep();
        assertThat(algorithm.isFinished(), is(false));
        algorithm.performStep();
        assertThat(algorithm.isFinished(), is(true));
        
    }
    
    @Test
    public void testProgress() {
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm() {

            @Override
            protected void performStep() {
                increaseStep();
            }
            
        };
        algorithm.setMaxSteps(3);
        assertThat(algorithm.getProgress(), is(closeTo(0.0, 10e-8)));
        algorithm.performStep();
        assertThat(algorithm.getProgress(), is(closeTo(1./3, 10e-8)));
        algorithm.performStep();
        assertThat(algorithm.getProgress(), is(closeTo(2./3, 10e-8)));
        algorithm.performStep();
        assertThat(algorithm.getProgress(), is(closeTo(1.0, 10e-8)));
    }
    
    @Test
    public void testInit() {
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm();
        assertThat(algorithm.getMaxSteps(), is(equalTo(300)));
    }
    
    @Test
    public void testSimple() {
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm();
        algorithm.setMaxSteps(3);
        assertThat(algorithm.getMaxSteps(), is(equalTo(3)));
    }
}
