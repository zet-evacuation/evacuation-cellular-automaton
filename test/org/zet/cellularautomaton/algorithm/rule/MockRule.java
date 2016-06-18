package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.junit.Ignore;
import org.zet.cellularautomaton.EvacCell;
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
        public void execute(EvacCell cell) {
        }

        @Override
        public boolean executableOn(EvacCell cell) {
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public static class TestLoopRule implements EvacuationRule {

        public TestLoopRule() {
        }

        @Override
        public void execute(EvacCell cell) {
        }

        @Override
        public boolean executableOn(EvacCell cell) {
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
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
