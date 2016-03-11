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
	private Project project;

	public RandomConstructor(Project project)
	{
		this.project = project;
	}

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

	public boolean[] generateSolutionWith(int numberOfCustomers)
	{
		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Arrays.fill(solution, false);
		List<Integer> listOfPossibilities = generateListOfSize(customerCount);

		for (int i = 1; i <= numberOfCustomers; i++)
		{
			int rand = PseudoRandom.randInt(0, listOfPossibilities.size());
			int position = listOfPossibilities.remove(rand);
			solution[position] = true;
		}

		return solution;
	}

	public boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers)
	{
		int numberOfCustomers = PseudoRandom.randInt(minCustomers, maxCustomers + 1);
		return generateSolutionWith(numberOfCustomers);
	}

	private List<Integer> generateListOfSize(int size)
	{
		List<Integer> list = new ArrayList<Integer>(size);

		for (int i = 0; i < size; i++)
		{
			list.add(i);
		}

		return list;
	}
}