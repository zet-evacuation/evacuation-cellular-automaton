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
import org.zet.cellularautomaton.algorithm.EvacuationSimulationProblem;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;

/**
 * @author Daniel R. Schmidt
 */
public abstract class AbstractEvacuationRule implements EvacuationRule {

    protected EvacuationSimulationProblem esp;

    /**
     * Returns if the rule is executable on the cell. The default behavior is, that a rule is executable if an
     * {@link Individual} is standing on it.
     *
     * @param cell the cell that is checked
     * @return {@code true} if an individual is standing on the cell, {@code false} otherwise
     */
    @Override
    public boolean executableOn(EvacCell cell) {
        return cell.getIndividual() != null;
    }

    @Override
    public final void execute(EvacCell cell) {
        if (!executableOn(cell)) {
            return;
        }

        onExecute(cell);
    }

    protected abstract void onExecute(EvacCell cell);

    @Override
    public void setEvacuationSimulationProblem(EvacuationSimulationProblem esp) {
        if (this.esp != null) {
            throw new IllegalStateException(CellularAutomatonLocalization.LOC.getString(
                    "algo.ca.rule.RuleAlreadyHaveCAControllerException"));
        }
        this.esp = Objects.requireNonNull(esp, CellularAutomatonLocalization.LOC.getString(
                "algo.ca.rule.CAControllerIsNullException"));
    }
}
