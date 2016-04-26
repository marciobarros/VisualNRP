package br.unirio.visualnrp.algorithm.search;

import br.unirio.visualnrp.calc.IFitnessCalculator;

/**
 * Abstract class that represents a search algorithm
 * 
 * @author Marcio
 */
public interface SearchAlgorithm 
{
	/**
	 * Executes the algorithm
	 */
	boolean[] execute(IFitnessCalculator calculator) throws Exception;

	/**
	 * Returns the fitness of the best solution
	 */
	double getFitness();

	/**
	 * Returns the number of iterations executed
	 */
	int getIterations();
	
	/**
	 * Returns the iteration on which the best solution was found
	 */
	int getIterationBestFound();
}