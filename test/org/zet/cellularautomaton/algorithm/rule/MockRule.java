package org.zet.cellularautomaton.algorithm.rule;

import org.junit.Ignore;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;

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
        public void setEvacuationSimulationProblem(EvacuationSimulationProblem esp) {
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
        public void setEvacuationSimulationProblem(EvacuationSimulationProblem esp) {
        }

    }
}
