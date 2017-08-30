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

import ds.PropertyContainer;
import java.util.List;
import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSet;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationProblemImpl implements EvacuationSimulationProblem {
    int seconds = 300;

    private final EvacuationCellularAutomaton ca;
    public EvacuationRuleSet ruleSet;
    public ParameterSet parameterSet;
    private final List<Individual> individuals;
    private final Map<Individual, EvacCellInterface> individualStartPositions;
    private final InitialConfiguration initialConfiguration;

    public EvacuationSimulationProblemImpl(InitialConfiguration initialConfiguration) {
        this.initialConfiguration = initialConfiguration;
        this.ca = initialConfiguration.getCellularAutomaton();
        this.individuals = initialConfiguration.getIndividuals();
        this.individualStartPositions = initialConfiguration.getIndividualStartPositions();

        PropertyContainer props = PropertyContainer.getGlobal();

        ruleSet = EvacuationRuleSet.createRuleSet(props.getAsString("algo.ca.ruleSet"));

        parameterSet = AbstractParameterSet.createParameterSet(props.getAsString("algo.ca.parameterSet"));

        //ca.setAbsoluteMaxSpeed(parameterSet.getAbsoluteMaxSpeed());
    }

    @Override
    public EvacuationCellularAutomaton getCellularAutomaton() {
        return ca;
    }

    @Override
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    @Override
    public List<Individual> getIndividuals() {
        return individuals;
    }
    
    @Override
    public EvacuationRuleSet getRuleSet() {
        return ruleSet;
    }

    @Override
    public int getEvacuationStepLimit() {
        return seconds;
    }

    public void setEvacuationTimeLimit(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public Map<Individual, EvacCellInterface> individualStartPositions() {
        return this.individualStartPositions;
    }

    @Override
    public InitialConfiguration getInitialConfiguration() {
        return this.initialConfiguration;
    }
    
    
}
