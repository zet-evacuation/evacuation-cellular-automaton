/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import org.zet.cellularautomaton.EvacCell;
import org.zetool.container.mapping.IntegerDoubleMapping;
import org.zetool.statistic.Statistic;
import org.zetool.statistic.Statistics;

/**
 * @author Daniel R. Schmidt
 *
 */
public enum DynamicCellStatistic implements Statistic<EvacCell, IntegerDoubleMapping, CAData> {

    INCOMING_INDIVIDUAL_RATE("Ankommende Individuenrate") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    OUTGOING_INDIVIDUAL_RATE("Ausgehende Individuenrate") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    INCOMING_INDIVUDAL_AMOUNT("Anzahl angekommener Individuen") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    OUTGOING_INDIVIDUAL_AMOUNT("Anzahl ausgegangener Individuen") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    TIME_BLOCKED("Blockadezeit") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    UTILISATION("Auslastung") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            },
    BLOCKED_TIMESTEPS("Blockadezeitpunkte") {
                @Override
                public IntegerDoubleMapping calculate(Statistics<CAData> statistics, EvacCell cell) {
                    throw new UnsupportedOperationException("This feature has not been implemented yet.");
                }
            };

    private final String description;

    private DynamicCellStatistic(String description) {
        this.description = description;
    }

    /**
     * @see statistic.graph.Statistic#range()
     */
    @Override
    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }

    @Override
    public String toString() {
        return description;
    }
}
