/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.statistic;

import org.zetool.statistic.Statistic;
import org.zetool.statistic.Statistics;
import org.zet.cellularautomaton.Individual;

/**
 * @author Daniel R. Schmidt
 *
 */
public enum IndividualDoubleStatistic implements Statistic<Individual, Double, CAData> {

    TIME_TO_SAFETY("Zeit bis Sicherheit") {
        @Override
        public Double calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    TIME_TO_EXIT("Zeit bis zur Evakuierung") {
        @Override
        public Double calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    };

    private String description;

    private IndividualDoubleStatistic(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * @see statistic.graph.Statistic#range()
     */
    @Override
    public Class<Double> range() {
        return Double.class;
    }

}
