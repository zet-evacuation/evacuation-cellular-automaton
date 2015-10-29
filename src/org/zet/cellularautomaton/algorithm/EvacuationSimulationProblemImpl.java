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
package org.zet.cellularautomaton.algorithm;

import org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSet;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.rule.EvacuationRule;
import ds.PropertyContainer;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.statistic.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationProblemImpl implements EvacuationSimulationProblem {

    private EvacuationCellularAutomaton ca;
    public EvacuationRuleSet ruleSet;
    public ParameterSet parameterSet;
    public PotentialController potentialController;
    public CAStatisticWriter caStatisticWriter;

    public EvacuationSimulationProblemImpl(EvacuationCellularAutomaton ca) {
        this.ca = ca;

        PropertyContainer props = PropertyContainer.getGlobal();

        ruleSet = EvacuationRuleSet.createRuleSet(props.getAsString("algo.ca.ruleSet"));
        for (EvacuationRule rule : ruleSet) {
            rule.setEvacuationSimulationProblem(this);
        }

        parameterSet = AbstractParameterSet.createParameterSet(props.getAsString("algo.ca.parameterSet"));

        potentialController = new SPPotentialController(ca);
        //caStatisticWriter = new CAStatisticWriter();
        potentialController.setCA(ca);
        potentialController.setPm(ca.getPotentialManager());
        ca.setAbsoluteMaxSpeed(parameterSet.getAbsoluteMaxSpeed());
    }

    @Override
    public EvacuationCellularAutomaton getCa() {
        return ca;
    }

    @Override
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    @Override
    public CAStatisticWriter getStatisticWriter() {
        return caStatisticWriter;
    }

    @Override
    public PotentialController getPotentialController() {
        return potentialController;
    }

    @Override
    public EvacuationRuleSet getRuleSet() {
        return ruleSet;
    }
}
