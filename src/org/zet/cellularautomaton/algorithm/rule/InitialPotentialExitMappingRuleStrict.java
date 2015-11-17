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

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;

/**
 * This rule applies the exit mapping to the cellular automaton. It is not allowed that one individual in the mapping
 * has no exit assigned.
 *
 * @author Jan-Philipp Kappmeier
 */
public class InitialPotentialExitMappingRuleStrict extends InitialPotentialExitMappingRule {

    @Override
    protected void handleWithoutTarget(Individual individual, EvacCell unused) {
        if (!individual.isDead()) {
            throw new IllegalArgumentException("The individual " + individual.getNumber()
                    + " lives, but has not been mapped to an exit." + " Therefore, I cannot map it to a potential.");
        }
    }
}
