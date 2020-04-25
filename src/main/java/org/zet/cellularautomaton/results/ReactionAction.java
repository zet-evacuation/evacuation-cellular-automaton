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
