package sobol.problems.requirements.algorithm.constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmetal.util.PseudoRandom;
import sobol.problems.requirements.model.Project;

/**
 * Constructor that generates a random solution.
 * 
 * @author richard
 */
public class RandomConstructor implements Constructor
{
	/**
	 * Set of requirements to be analyzed
	 */
	private Project project;

	/**
	 * Initializes the random constructor
	 */
	public RandomConstructor(Project project)
	{
		this.project = project;
	}

	/**
	 * Generates a random solution
	 */
	public boolean[] generateSolution()
	{
		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			solution[i] = (PseudoRandom.randDouble() >= 0.5);
		}

		return solution;
	}

	/**
	 * Generates a random solution, given a number of customers
	 */
	public boolean[] generateSolutionWith(int numberOfCustomers)
	{
		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Arrays.fill(solution, false);
		List<Integer> listOfPossibilities = generateListOfSize(customerCount);

		for (int i = 1; i <= numberOfCustomers; i++)
		{
			int position = PseudoRandom.randInt(0, listOfPossibilities.size());
			int value = listOfPossibilities.remove(position);
			solution[value] = true;
		}

		return solution;
	}

	/**
	 * Generates a random solution, given an interval for the number of customers
	 */
	public boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers)
	{
		int numberOfCustomers = PseudoRandom.randInt(minCustomers, maxCustomers + 1);
		return generateSolutionWith(numberOfCustomers);
	}

	/**
	 * Creates a list of sequential values
	 */
	private List<Integer> generateListOfSize(int size)
	{
		List<Integer> list = new ArrayList<Integer>(size);

		for (int i = 0; i < size; i++)
			list.add(i);

		return list;
	}
}