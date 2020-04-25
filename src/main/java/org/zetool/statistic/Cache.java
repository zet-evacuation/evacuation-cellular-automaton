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
package org.zetool.statistic;

import java.util.HashMap;

/**
 *
 * @author Martin Groß
 */
@SuppressWarnings("unchecked")
public class Cache {

    private HashMap<Statistic, HashMap> cache;

    public Cache() {
        cache = new HashMap<>();
    }

    public <O, R, D> boolean contains(Statistic<O, R, D> statistic, O object) {
        return cache.containsKey(statistic) && cache.get(statistic).containsKey(object);
    }

    public <O, R, D> R get(Statistic<O, R, D> statistic, O object) {
        return (R) cache.get(statistic).get(object);
    }

    public <O, R, D> void put(Statistic<O, R, D> statistic, O object, R value) {
        if (!cache.containsKey(statistic)) {
            cache.put(statistic, new HashMap<>());
        }
        cache.get(statistic).put(object, value);
    }
}
