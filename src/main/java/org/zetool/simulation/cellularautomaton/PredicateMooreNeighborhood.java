package org.zetool.simulation.cellularautomaton;

import java.util.function.Predicate;

/**
 * An implementation of the {@link MooreNeighborhood} that checks if neighboring cells are actually valid. Thus cells
 * having the wrong state can be excluded.
 * 
 * @param <E> the cell type
 * @param <S> the state of the cell
 * @author Jan-Philipp Kappmeier
 */
public class PredicateMooreNeighborhood<E extends SquareCell<S>, S> extends MooreNeighborhood<E> {
    /** The predicate checking if the cell's state is valid for acceptance. */
    private final Predicate<S> acceptCell;
    
    public PredicateMooreNeighborhood(CellMatrix<E> matrix) {
        super(matrix);
        acceptCell = x -> true;
    }

    public PredicateMooreNeighborhood(CellMatrix<E> matrix, Predicate<S> acceptCell) {
        super(matrix);
        this.acceptCell = acceptCell;
    }

    @Override
    protected boolean accept(E cell) {
        return acceptCell.test(cell.getState());
    }
}
