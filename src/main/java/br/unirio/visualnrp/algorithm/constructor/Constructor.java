package br.unirio.visualnrp.algorithm.constructor;

/**
 * This interface represents an algorithm that generates an initial solution for the problem.
 * 
 * @author richard
 */
public interface Constructor
{
	/**
	 * Generates a unbounded solution
	 */
	boolean[] generateSolution();

	/**
	 * Generates a solution with a given number of customers
	 */
	boolean[] generateSolutionWith(int numberOfCustomers);

	/**
	 * Generates a solution within a given interval of customers
	 */
	boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers);
}