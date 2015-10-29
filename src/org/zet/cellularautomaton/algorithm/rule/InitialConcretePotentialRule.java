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

import org.zet.cellularautomaton.PotentialValueTuple;
import org.zet.cellularautomaton.DeathCause;
import java.util.ArrayList;
import java.util.Collections;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.StaticPotential;
import org.zet.cellularautomaton.Individual;

/**
 * This rule chooses an {@link Individual}'s initial {@link StaticPotential}
 * according to the attractivity value of those {@link ExitCells}, whose
 * distances to the individual are acceptable according to its familarity value.
 * If the Individual standing on a cell is caged (it cannot leave the building,
 * because there is now passable way to an exit), this individual has to die
 * because it cannot be evacuated.
 * @author marcel
 *
 */
public class InitialConcretePotentialRule extends AbstractInitialRule {

	/**
	 * Checks, whether the rule is executable or not.
	 * @param cell the cell on which the rule should be executed
	 * @return Returns true, if an Individual is standing
	 * on this cell, and moreover this Individual does
	 * not already have a StaticPotential.
	 */
	@Override
	public boolean executableOn( EvacCell cell ) {
		return cell.getIndividual() == null || cell.getIndividual().getStaticPotential() != null ? false : true;
	}

	/**
	 * This rule chooses an Individual's (the one standing on "cell") initial
	 * StaticPotential according to the attractivity value of those Exit-Cells, whose
	 * distances to the Individual are acceptable according to the Individual's
	 * familarity value.
	 * If the Individual standing on "cell" is caged (it cannot leave the building,
	 * because there is now passable way to an ExitCell), this Individual has to die
	 * because it cannot be evacuated.
	 * @param cell the cell
	 */
	@Override
	protected void onExecute( EvacCell cell ) {
		Individual individual = cell.getIndividual();
		ArrayList<PotentialValueTuple> potentialToLengthOfWayMapper = new ArrayList<>();
		ArrayList<StaticPotential> staticPotentials = new ArrayList<>();
		staticPotentials.addAll( esp.getCa().getPotentialManager().getStaticPotentials() );
		double minDistanceToEvacArea = Double.MAX_VALUE;
		double distanceToEvacArea;
		for( StaticPotential sp : staticPotentials ) {
			distanceToEvacArea = sp.getDistance( individual.getCell() );
			if( distanceToEvacArea >= 0 && distanceToEvacArea <= minDistanceToEvacArea ) {
				minDistanceToEvacArea = sp.getDistance( individual.getCell() );
			}
			int lengthOfWayValue = sp.getPotential( individual.getCell() );
			if( lengthOfWayValue >= 0 ) {// if this StaticPotential can lead the individual to an ExitCell
				potentialToLengthOfWayMapper.add( new PotentialValueTuple( lengthOfWayValue, sp ) );
			}
		}
		// Sort the Individual's StaticPotentials according to their familarity value
		Collections.sort( potentialToLengthOfWayMapper );
		// Check whether the individual is caged and cannot leave the building -> it has to die
		if( potentialToLengthOfWayMapper.size() == 0 ) {
			esp.getCa().setIndividualDead( individual, DeathCause.ExitUnreachable );
		//esp.getCa().decreaseNrOfLivingAndNotSafeIndividuals();
		} else {
			int nrOfPossiblePotentials = (int) (Math.round( (1 - individual.getFamiliarity()) * potentialToLengthOfWayMapper.size() ));
			if( nrOfPossiblePotentials < 1 ) {
				nrOfPossiblePotentials = 1;
			// select the potential with the highest attractivity from one of the nrOfPossiblePotentials
			}
			int best = 0;
			for( int i = 1; i < nrOfPossiblePotentials; i++ ) {
				if( potentialToLengthOfWayMapper.get( best ).getStaticPotential().getAttractivity() < potentialToLengthOfWayMapper.get( i ).getStaticPotential().getAttractivity() ) {
					best = i;
				}
			}
			individual.setStaticPotential( potentialToLengthOfWayMapper.get( best ).getStaticPotential() );
			esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addMinDistancesToStatistic( individual, minDistanceToEvacArea, potentialToLengthOfWayMapper.get( best ).getStaticPotential().getDistance( cell ) );
			esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( individual, 0 );
			esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic( individual, 0, individual.getExhaustion() );
			esp.getStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic( individual, 0, individual.getPanic() );
		// ToDo: Potential des Individuums im VisualResultsRecorder speichern
		}
	}
}