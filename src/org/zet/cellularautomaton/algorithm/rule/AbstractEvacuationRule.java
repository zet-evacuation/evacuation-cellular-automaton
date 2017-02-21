/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import java.util.Objects;
import java.util.Optional;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.Action;

/**
 * @author Daniel R. Schmidt
 * @param <R>
 */
public abstract class AbstractEvacuationRule<R extends Action> implements EvacuationRule<R> {

    protected EvacuationState es;
    protected Computation c;
    protected EvacuationSimulationSpeed sp;

    /**
     * Returns if the rule is executable on the cell. The default behavior is, that a rule is
     * executable if an {@link Individual} is standing on it.
     *
     * @param cell the cell that is checked
     * @return {@code true} if an individual is standing on the cell, {@code false} otherwise
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !cell.getState().isEmpty();
    }

    @Override
    public final Optional<R> execute(EvacCellInterface cell) {
        if (executableOn(cell)) {
            R a = onExecute(cell);
            return Optional.ofNullable(a);
        }

        return Optional.empty();
    }

    protected abstract R onExecute(EvacCellInterface cell);

    @Override
    public void setEvacuationState(EvacuationState es) {
        if (this.es != null) {
            throw new IllegalStateException(CellularAutomatonLocalization.LOC.getString(
                    "algo.ca.rule.RuleAlreadyHaveCAControllerException"));
        }
        this.es = Objects.requireNonNull(es, CellularAutomatonLocalization.LOC.getString(
                "algo.ca.rule.CAControllerIsNullException"));
    }

    @Override
    public void setComputation(Computation c) {
        this.c = c;
    }

    @Override
    public void setEvacuationSimulationSpeed(EvacuationSimulationSpeed sp) {
        if (this.sp != null) {
            throw new IllegalStateException(CellularAutomatonLocalization.LOC.getString(
                    "algo.ca.rule.RuleAlreadyHasTimingInformation"));
        }
        this.sp = Objects.requireNonNull(sp, CellularAutomatonLocalization.LOC.getString(
                "algo.ca.rule.TimingInformationNullException"));
    }

    protected static Exit getNearestExit(EvacuationCellularAutomaton ca, EvacCellInterface cell) {
        Exit nearestExit = null;
        int distance = Integer.MAX_VALUE;
        for (Exit exit : ca.getExits()) {
            Potential potential = ca.getPotentialFor(exit);
            final int potentialValue = potential.getPotential(cell);
            if (potentialValue != -1 && potentialValue < distance) {
                nearestExit = exit;
                distance = potential.getPotential(cell);
            }
        }
        if (nearestExit == null) {
            throw new IllegalStateException("No potential at cell " + cell.toString());
        }
        return nearestExit;
    }
    
    protected static Potential getNearestExitStaticPotential(EvacuationCellularAutomaton ec, EvacCellInterface cell) {
        return ec.getPotentialFor(getNearestExit(ec, cell));
    }
}
