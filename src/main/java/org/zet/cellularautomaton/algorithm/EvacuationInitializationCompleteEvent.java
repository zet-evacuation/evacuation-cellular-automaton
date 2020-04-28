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
package org.zet.cellularautomaton.algorithm;

import java.util.Collections;
import java.util.List;
import org.zet.cellularautomaton.results.Action;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationInitializationCompleteEvent extends AlgorithmProgressEvent<EvacuationSimulationProblem, EvacuationSimulationResult> {

    private final List<Action> initializationActions;

    public EvacuationInitializationCompleteEvent(Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> algorithm, List<Action> initializationActions) {
        super(algorithm, 0);
        this.initializationActions = initializationActions;
    }

    public List<Action> getInitializationActions() {
        return Collections.unmodifiableList(initializationActions);
    }

}
