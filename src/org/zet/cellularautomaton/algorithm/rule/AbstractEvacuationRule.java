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
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.localization.CellularAutomatonLocalization;
import org.zet.cellularautomaton.potential.StaticPotential;

/**
 * @author Daniel R. Schmidt
 */
public abstract class AbstractEvacuationRule implements EvacuationRule {

    //protected EvacuationSimulationProblem esp;
    protected EvacuationState es;

    /**
     * Returns if the rule is executable on the cell. The default behavior is, that a rule is executable if an
     * {@link Individual} is standing on it.
     *
     * @param cell the cell that is checked
     * @return {@code true} if an individual is standing on the cell, {@code false} otherwise
     */
    @Override
    public boolean executableOn(EvacCell cell) {
        return !cell.getState().isEmpty();
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
    public void setEvacuationSimulationProblem(EvacuationState es) {
        if (this.es != null) {
            throw new IllegalStateException(CellularAutomatonLocalization.LOC.getString(
                    "algo.ca.rule.RuleAlreadyHaveCAControllerException"));
        }
        this.es = Objects.requireNonNull(es, CellularAutomatonLocalization.LOC.getString(
                "algo.ca.rule.CAControllerIsNullException"));
    }
    
    protected static StaticPotential getNearestExitStaticPotential(Collection<StaticPotential> potentials, EvacCell c) {
        StaticPotential nearestPot = new StaticPotential();
        int distance = Integer.MAX_VALUE;
        int numberOfDisjunctStaticPotentials = 0;
        for (StaticPotential sP : potentials) {
            if (sP.getPotential(c) == -1) {
                numberOfDisjunctStaticPotentials++;
            } else {
                if (sP.getPotential(c) < distance) {
                    nearestPot = sP;
                    distance = sP.getPotential(c);
                }
            }
        }
        return numberOfDisjunctStaticPotentials == potentials.size() ? null : nearestPot;
    }
    
}
