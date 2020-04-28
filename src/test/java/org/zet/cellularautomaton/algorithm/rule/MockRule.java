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
        public void setComputation(Computation c) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
