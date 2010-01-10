package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.model.Project;

/**
 * Enumeration of the available search algorithms
 * 
 * @author marciobarros
 */
public enum Algorithm
{
	VISILS, 
	ILS, 
	HC;

	/**
	 * Maximum number of evaluation rounds for the algorithms
	 */
	private static int MAXEVALUATIONS = 10000000;
	
	/**
	 * Sample size for the VISILS algorithm
	 */
	private static int SAMPLE_SIZE = 10;
	
	/**
	 * Creates a search algorithm for the problem at hand
	 */
	public static SearchAlgorithm createAlgorithm(Algorithm type, PrintWriter detailsWriter, Project project, Constructor constructor) throws Exception
	{
		if (type == Algorithm.VISILS)
			return new VisIteratedLocalSearch(detailsWriter, project, MAXEVALUATIONS, SAMPLE_SIZE, constructor);
		
		if (type == Algorithm.ILS)
			return new IteratedLocalSearch(detailsWriter, project, MAXEVALUATIONS, constructor);

		if (type == Algorithm.HC)
			return new HillClimbing(detailsWriter, project, MAXEVALUATIONS, constructor);
		
		return null;
	}

}