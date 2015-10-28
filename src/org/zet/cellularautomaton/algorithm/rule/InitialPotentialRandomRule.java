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

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.StaticPotential;
import java.util.ArrayList;
import org.zetool.rndutils.RandomUtils;

/**
 * Sets a random exit to an individual.
 */
public class InitialPotentialRandomRule extends AbstractInitialRule {
	/**
	 * @param cell the cell
	 */
	@Override
	protected void onExecute( EvacCell cell ) {
		ArrayList<StaticPotential> exits = new ArrayList<StaticPotential>();
		exits.addAll( esp.eca.getPotentialManager().getStaticPotentials() );
		int numberOfExits = exits.size();
		RandomUtils random = RandomUtils.getInstance();
		int randomExitNumber = random.getRandomGenerator().nextInt( numberOfExits );

		boolean exitFound = false;
		if( exits.get( randomExitNumber ).getPotential( cell ) < 0 ) {
			for( StaticPotential exit : exits )
				if( exit.getPotential( cell ) >= 0 ) {
					cell.getIndividual().setStaticPotential( exit );
					exitFound = true;
					break;
				}
			if( !exitFound )
				esp.eca.setIndividualDead( cell.getIndividual(), DeathCause.ExitUnreachable );
		} else
			cell.getIndividual().setStaticPotential( exits.get( randomExitNumber ) );
	}
}
