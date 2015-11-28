package org.zetool.algorithm.simulation.cellularautomaton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import org.jmock.Mockery;
import static org.junit.Assert.assertThat;
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
        assertThat(algorithm.getMaxSteps(), is(equalTo(0)));
    }
    
    @Test
    public void testSimple() {
        FakeAbstractCellularAutomatonSimulationAlgorithm algorithm = new FakeAbstractCellularAutomatonSimulationAlgorithm();
        algorithm.setMaxSteps(3);
        assertThat(algorithm.getMaxSteps(), is(equalTo(3)));
    }
}
