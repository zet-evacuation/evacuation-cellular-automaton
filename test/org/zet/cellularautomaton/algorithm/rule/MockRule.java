package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.junit.Ignore;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@Ignore
public class MockRule {

    public static class TestInitRule implements EvacuationRule {

        public TestInitRule() {
        }

        @Override
        public void execute(EvacCellInterface cell) {
        }

        @Override
        public boolean executableOn(EvacCellInterface cell) {
            return false;
        }

        @Override
        public void setEvacuationState(EvacuationState esp) {
        }

        @Override
        public void setEvacuationStateController(EvacuationStateControllerInterface ec) {
        }

        @Override
        public void setComputation(Computation c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static class TestLoopRule implements EvacuationRule {

        public TestLoopRule() {
        }

        @Override
        public void execute(EvacCellInterface cell) {
        }

        @Override
        public boolean executableOn(EvacCellInterface cell) {
            return false;
        }

        @Override
        public void setEvacuationState(EvacuationState esp) {
        }

        @Override
        public void setEvacuationStateController(EvacuationStateControllerInterface ec) {
        }

        @Override
        public void setComputation(Computation c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
