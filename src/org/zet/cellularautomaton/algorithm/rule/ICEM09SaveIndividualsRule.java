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
package org.zet.cellularautomaton.algorithm.rule;

/**
 * Sets an individual save. The time is stored in the cellular automaton
 * statistic. This rule is supposed to be called bevore the
 * {@link ICEM09EvacuateIndividualsRule}.
*/
public class ICEM09SaveIndividualsRule extends AbstractSaveRule {
	/**
	 * Public constructor.
	 */
	public ICEM09SaveIndividualsRule() {}

	@Override
	protected void onExecute( org.zet.cellularautomaton.EvacCell cell ) {
		org.zet.cellularautomaton.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
			esp.getCa().setIndividualSave( savedIndividual );
			esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic( savedIndividual );
		}
	}

	/**
	 * The rule is applicable if it is an exit or save cell and is occupied by
	 * an individual.
	 * @param cell the cell that is checked
	 * @return {@code true} if the rule is applicable to the given cell, {@code false} otherwise
	 */
	@Override
	public boolean executableOn( org.zet.cellularautomaton.EvacCell cell ) {
		return (cell.getIndividual() != null) && ((cell instanceof org.zet.cellularautomaton.ExitCell) || (cell instanceof org.zet.cellularautomaton.SaveCell));
	}
}