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
