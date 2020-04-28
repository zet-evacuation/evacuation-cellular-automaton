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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import static java.util.stream.Collectors.joining;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ReactionAction extends Action {
    private final Collection<Individual> individuals;

    public ReactionAction(Collection<Individual> individuals) {
        this.individuals = Objects.requireNonNull(individuals, "Individuals must be present");
    }
    
    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        individuals.stream().forEach(individual -> es.propertyFor(individual).setAlarmed());
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {

    }

    @Override
    public String toString() {
        return "ReactionAction for Individuals " +  individuals.stream().map(i -> Integer.toString(i.getNumber())).collect(joining(", "));
    }

    public Collection<Individual> getIndividuals() {
        return Collections.unmodifiableCollection(individuals);
    }
    
}
