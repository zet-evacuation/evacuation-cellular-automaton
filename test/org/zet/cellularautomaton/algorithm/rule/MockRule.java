package org.zet.cellularautomaton.algorithm.rule;

import java.util.Optional;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.junit.Ignore;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.results.VoidAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@Ignore
public class MockRule {

    public static class TestInitRule implements EvacuationRule<VoidAction> {

        public TestInitRule() {
        }

        @Override
        public Optional<VoidAction> execute(EvacCellInterface cell) {
            return Optional.of(VoidAction.VOID_ACTION);
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

    public static class TestLoopRule implements EvacuationRule<VoidAction> {

        public TestLoopRule() {
        }

        @Override
        public Optional<VoidAction> execute(EvacCellInterface cell) {
            return Optional.of(VoidAction.VOID_ACTION);
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
