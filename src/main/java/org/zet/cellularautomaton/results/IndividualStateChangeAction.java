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
package org.zet.cellularautomaton.results;

import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;

/**
 * @author Daniel R. Schmidt
 */
public class IndividualStateChangeAction extends Action {

    private Individual individual;
    private double panic;
    private double exhaustion;
    private double currentSpeed;
    private boolean isAlarmed;

    public IndividualStateChangeAction(Individual individual, PropertyAccess es) {
        this(individual,
                es.propertyFor(individual).getPanic(),
                es.propertyFor(individual).getExhaustion(),
                es.propertyFor(individual).getRelativeSpeed(),
                es.propertyFor(individual).isAlarmed());
    }

    protected IndividualStateChangeAction(Individual individual, double panic, double exhaustion, double currentSpeed, boolean isAlarmed) {
        this.individual = individual;
        this.panic = panic;
        this.exhaustion = exhaustion;
        this.currentSpeed = currentSpeed;
        this.isAlarmed = isAlarmed;
    }

    /**
     * {@inheritDoc }
     *
     * @param es
     * @throws InconsistentPlaybackStateException
     * @see ds.ca.results.Action#execute(ds.ca.EvacuationCellularAutomaton)
     */
    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        es.propertyFor(individual).setPanic(panic);
        es.propertyFor(individual).setExhaustion(exhaustion);
        es.propertyFor(individual).setRelativeSpeed(currentSpeed);
        if (isAlarmed && !es.propertyFor(individual).isAlarmed()) {
            es.propertyFor(individual).setAlarmed();
        }
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    /**
     * {@inheritDoc }
     *
     * @see ds.ca.results.Action#toString()
     */
    @Override
    public String toString() {
        return "The state of the individual " + individual.getNumber() + " changes to: " + " Panic: " + panic + ", Exhaustion: " + exhaustion + ", Speed: " + currentSpeed + ", Alarmed: " + (isAlarmed ? "yes" : "no");
    }
}
