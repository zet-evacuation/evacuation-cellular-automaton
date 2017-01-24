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

import java.util.Collection;
import java.util.Objects;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.computation.Computation;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.Action;

/**
 * @author Daniel R. Schmidt
 */
public abstract class AbstractEvacuationRule implements EvacuationRule {

    protected EvacuationState es;
    protected EvacuationStateControllerInterface ec;
    protected Computation c;

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
    public final void execute(EvacCellInterface cell) {
        if (!executableOn(cell)) {
            return;
        }

        onExecute(cell);
    }

    protected abstract void onExecute(EvacCellInterface cell);

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
    public void setEvacuationStateController(EvacuationStateControllerInterface ec) {
        if (this.ec != null) {
            throw new IllegalStateException(CellularAutomatonLocalization.LOC.getString(
                    "algo.ca.rule.RuleAlreadyHaveCAControllerException"));
        }
        this.ec = Objects.requireNonNull(ec, CellularAutomatonLocalization.LOC.getString(
                "algo.ca.rule.CAControllerIsNullException"));
    }

    @Override
    public void setComputation(Computation c) {
        this.c = c;
    }

    protected static <T extends Potential> T getNearestExitStaticPotential(Collection<T> potentials, EvacCellInterface cell) {
        T nearestPot = null;
        int distance = Integer.MAX_VALUE;
        for (T potential : potentials) {
            final int potentialValue = potential.getPotential(cell);
            if (potentialValue != -1 && potentialValue < distance) {
                nearestPot = potential;
                distance = potential.getPotential(cell);
            }
        }
        if (nearestPot == null) {
            throw new IllegalStateException("No potential at cell " + cell.toString());
        }
        return nearestPot;
    }

    protected void recordAction(Action a) {
        // ignore publishing
    }
}
