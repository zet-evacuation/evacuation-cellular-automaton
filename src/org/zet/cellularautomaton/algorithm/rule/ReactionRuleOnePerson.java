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
 * A rule that alarms an individual if its reaction time is over. No other individuals nor the room will be alarmed.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleOnePerson extends AbstractReactionRule {

    /**
     * Executes the rule. The individual is alarmed if the time is over otherwise the remaining time is reduced by one.
     * No other individuals are infected from the alerting of the individual.
     *
     * @param cell the cell on which the rule is executed
     */
    @Override
    protected void onExecute(EvacCell cell) {
        Individual i = cell.getState().getIndividual();
        if (!es.propertyFor(i).isAlarmed() && es.getTimeStep() >= i.getReactionTime() * es.getCellularAutomaton().getStepsPerSecond()) {
            es.propertyFor(i).setAlarmed(true);
        }
    }
}
