package org.zetool.simulation.cellularautomaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple wrapper to create and access a two dimensional array list.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ArrayList2D<E> {

    final int width;
    final int height;
    List<List<E>> array;

    public ArrayList2D(int width, int height) {
        this(width, height, null);
        init(null);
    }

    public ArrayList2D(int width, int height, E init) {
        this.width = width;
        this.height = height;
    }

    private void init(E init) {
        array = new ArrayList<>(width);
        for (int i = 0; i < width; ++i) {
            array.add(new ArrayList<>(Collections.nCopies(height, init)));
        }
    }

    public E get(int x, int y) {
        return array.get(x).get(y);
    }

    public void set(int x, int y, E value) {
        array.get(x).set(y, value);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
