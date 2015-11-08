package org.zetool.simulation.cellularautomaton;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FakeCell implements Cell<FakeCell, Void> {

    @Override
    public Collection<FakeCell> getDirectNeighbors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public int getSides() {
        return 4;
    }

    @Override
    public Iterator<FakeCell> iterator() {
        return getDirectNeighbors().iterator();
    }
    
}
