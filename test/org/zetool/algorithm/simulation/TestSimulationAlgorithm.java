package org.zetool.algorithm.simulation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestSimulationAlgorithm {
    private static class FakeSimulationAlgorithm extends SimulationAlgorithm<Object, Object> {
        private boolean initialized;
        private boolean performedSimulation;
        private boolean terminated;
        Object returnValue = new Object();
        
        @Override
        protected double getProgress() {
            return 1.0;
        }

        @Override
        protected void performSimulation() {
            assertThat(initialized, is(true));
            assertThat(performedSimulation, is(false));
            assertThat(terminated, is(false));
            performedSimulation = true;
        }

        @Override
        protected void initialize() {
            if( getStep() != 0 ) {
                throw new AssertionError("When initialize is called step count should be 0");
            }
            assertThat(initialized, is(false));
            assertThat(performedSimulation, is(false));
            assertThat(terminated, is(false));
            initialized = true;
            
        }

        @Override
        protected void performStep() {
            throw new AssertionError();
        }

        @Override
        protected Object terminate() {
            assertThat(initialized, is(true));
            assertThat(performedSimulation, is(true));
            assertThat(terminated, is(false));
            return returnValue;
        }
        
    }
    
    @Test
    public void testInitialization() {
        FakeSimulationAlgorithm simulationAlgorithm = new FakeSimulationAlgorithm();
        assertThat(simulationAlgorithm.getStep(), is(equalTo(0)));
    }
    
    @Test
    public void testStepIncrease() {
        FakeSimulationAlgorithm simulationAlgorithm = new FakeSimulationAlgorithm();
        simulationAlgorithm.increaseStep();
        assertThat(simulationAlgorithm.getStep(), is(equalTo(1)));
    }
    
    @Test
    public void testAlgorithmExecution() {
        FakeSimulationAlgorithm simulationAlgorithm = new FakeSimulationAlgorithm();
        simulationAlgorithm.increaseStep();
        simulationAlgorithm.setProblem(new Object());
        simulationAlgorithm.runAlgorithm();
        Object result = simulationAlgorithm.getSolution();
        
        assertThat(result, is(equalTo(simulationAlgorithm.returnValue)));
    }
}
