package ds.ca.evac;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@FunctionalInterface
public interface CellularAutomatonDirectionChecker {
    public boolean canPass(Individual i, EvacCell from, EvacCell to);
}
