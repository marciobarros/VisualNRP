package sobol.problems.requirements.algorithm.constructor;

/**
 * This interface represents an algorithm that generates an initial solution for the problem.
 * 
 * @author richard
 */
public interface Constructor
{
	boolean[] generateSolution();

	boolean[] generateSolutionWith(int numberOfCustomers);

	boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers);
}