package org.zetool.simulation.cellularautomaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A virtual compund {@link CellMatrix} that consists of multiple, non-overlapping cell matrices
 * creating a larger cell matrix. The composite cell matrix may contain holes.
 * 
 * @param <E>
 * @author Jan-Philipp Kappmeier
 */
public class CompositeCellMatrix<E extends Cell> implements CellMatrix<E> {
    int minx = Integer.MAX_VALUE;
    int miny = Integer.MAX_VALUE;
    int maxx = Integer.MIN_VALUE;
    int maxy = Integer.MIN_VALUE;

    List<GeometricCellMatrix<E>> lists = new LinkedList<>();
    
    public void addMatrix(GeometricCellMatrix<E> matrix) {
        lists.add(matrix);
        minx = Math.min(minx, matrix.getXOffset());
        miny = Math.min(miny, matrix.getYOffset());
        maxx = Math.max(maxx, matrix.getXOffset() + matrix.getWidth() - 1);
        maxy = Math.max(maxy, matrix.getYOffset() + matrix.getHeight() - 1);        
    }
    
    @Override
    public int getWidth() {
        return lists.isEmpty() ? 0 : maxx-minx + 1;
    }

    @Override
    public int getHeight() {
        return lists.isEmpty() ? 0 : maxy-miny + 1;
    }

    @Override
    public Collection<E> getAllCells() {        
        List<E> newList = new ArrayList<>();
        lists.stream().forEach(matrix -> newList.addAll(matrix.getAllCells()));
        return newList;
    }

    @Override
    public E getCell(int x, int y) {
        for( GeometricCellMatrix<E> matrix : lists) {
            if( liesInMatrix(matrix, x, y)) {
                int translatedX = x - matrix.getXOffset();
                int translatedY = y - matrix.getYOffset();
                return matrix.getCell(translatedX, translatedY);
            }
        }
        throw new IllegalArgumentException("No cell at " + x + "," + y);
    }
    
    /**
     * Example: xoffset = 4, yoffset = -2
     * widht = 3, height = 4
     * contained: (4,-2) ... (6, 1)
     * @param matrix
     * @param x
     * @param y
     * @return 
     */
    private boolean liesInMatrix(GeometricCellMatrix matrix, int x, int y) {
        //4,-2 given
        int translatedX = x - matrix.getXOffset();
        int translatedY = y - matrix.getYOffset();
        if ((translatedX < 0) || (translatedX > matrix.getWidth() - 1)) {
            return false;
        }
        if ((translatedY < 0) || (translatedY > matrix.getHeight() - 1)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean existsCellAt(int x, int y) {
        return lists.stream().anyMatch(matrix -> liesInMatrix(matrix, x, y));
    }

}
