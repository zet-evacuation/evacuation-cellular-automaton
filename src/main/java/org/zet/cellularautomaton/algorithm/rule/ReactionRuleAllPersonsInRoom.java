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

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.ReactionAction;

/**
 * A rule that activates all individuals in a room if the reaction time of all of them is over. Before that, people stay
 * unalarmed.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionRuleAllPersonsInRoom extends AbstractReactionRule {

    /**
     * Executes the rule. The alarm time for the individual on the cell is reduced by one, if it will remai positive
     * afterwards. If the reaction time would be negative after the reduction (i.e. the reaction time is between 0 and
     * 1), it is checked if all individuals in the room are in the same state. If that is true for all individuals in
     * the same room, they are all alarmed at the same time.
     *
     * @param cell the cell, whose individuals reaction time is reduced
     * @return 
     */
    @Override
    protected ReactionAction onExecute(EvacCellInterface cell) {
        for (Individual individual : cell.getRoom().getIndividuals()) {
            if (es.getTimeStep() < individual.getReactionTime() * sp.getStepsPerSecond()) {
                return null;
            }
        }
        return new ReactionAction(cell.getRoom().getIndividuals());
    }
}
