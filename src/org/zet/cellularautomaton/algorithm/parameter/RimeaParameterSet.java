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
package org.zet.cellularautomaton.algorithm.parameter;

/**
 * A {@link ParameterSet} that sets the parameter of the simulation to values that allow passing the RIMEA tests.
 *
 * @author Jan-Philipp Kappmeier
 */
public class RimeaParameterSet extends DefaultParameterSet {

    public RimeaParameterSet() {
        super();
    }


    /**
     * Sets the reaction time depending from age. This is disabled in rimea profile, the reaction time is set by the
     * default individual reaction time distribution instead.
     *
     * @param age
     * @return
     */
    @Override
    public double getReactionTimeFromAge(double age) {
        throw new IllegalStateException("ReactionTimeFromAge not allowed in Rimea Parameter set.");
    }
}
