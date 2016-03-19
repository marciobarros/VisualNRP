package sobol.problems.requirements.algorithm.constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jmetal.util.PseudoRandom;
import sobol.problems.requirements.model.Project;

/**
 * Greedy constructor that uses the relation Profit / Cost for selecting customers
 * to a solution. Customers high higher profit/cost ratio are more likely selected.
 * 
 * @author richard
 */
public class GreedyConstructor implements Constructor
{
	private final Project project;
	private final Map<Integer, Integer> profitLossRatios;		// TODO: might be an array ...

	/**
	 * Initializes the constructor
	 */
	public GreedyConstructor(Project project)
	{
		this.project = project;
		this.profitLossRatios = calculateProfitLossRatios();
	}

	/**
	 * Calculates the profit/loss ratio for each customer
	 */
	private Map<Integer, Integer> calculateProfitLossRatios()
	{
		int numberOfCustomers = project.getCustomerCount();
		boolean[] sol = new boolean[numberOfCustomers];
		Arrays.fill(sol, false);

		Map<Integer, Integer> profitLoss = new HashMap<Integer, Integer>(numberOfCustomers);

		for (int customer = 0; customer < numberOfCustomers; customer++)
		{
			sol[customer] = true;
			double profit = project.getCustomerProfit(customer);
			int cost = project.calculateCost(sol);
			sol[customer] = false;
			profitLoss.put(customer, (int) ((profit / cost) * 1000));
		}

		return profitLoss;
	}

	/**
	 * Generates a unbounded solution
	 */
	public boolean[] generateSolution()
	{
		int customerCount = project.getCustomerCount();
		int numberOfCustomers = PseudoRandom.randInt(1, customerCount);
		return generateSolutionWith(numberOfCustomers);
	}

	/**
	 * Generates a solution with a given number of customers
	 */
	public boolean[] generateSolutionWith(int numberOfCustomers)
	{
		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Arrays.fill(solution, false);
		
		List<Integer> customers = new ArrayList<Integer>(profitLossRatios.keySet());

		for (int i = 0; i < numberOfCustomers; i++)
		{
			int selected = getWeightedRandom(customers);
			selected = customers.remove(selected);
			solution[selected] = true;
		}

		return solution;
	}

	/**
	 * Generates a solution within a given interval of customers
	 */
	public boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers)
	{
		int numberOfCustomers = PseudoRandom.randInt(minCustomers, maxCustomers);
		return generateSolutionWith(numberOfCustomers);
	}

	/**
	 * Randomly selects a customer, weighting the chances for their weights
	 */
	private int getWeightedRandom(List<Integer> customers)
	{
		int[] weights = getWeightsFor(customers);
		int[] cumulative = computeCumulativeWeights(weights);
		int totalWeight = cumulative[cumulative.length-1]; 
				
		int rand = PseudoRandom.randInt(0, totalWeight-1);
		int pos = Arrays.binarySearch(cumulative, rand);
		
		if (pos < 0)
			pos = Math.abs(pos) - 1;

		return pos;
	}

	/**
	 * Returns the weights for a list of customers
	 */
	private int[] getWeightsFor(List<Integer> customers)
	{
		// TODO: might already accumulate the weights
		int[] weights = new int[customers.size()];
		int index = 0;

		for (Integer customer : customers)
		{
			weights[index] = profitLossRatios.get(customer);
			index++;
		}
		
		return weights;
	}

	/**
	 * Computes a list of cumulative weights for the customers
	 */
	private int[] computeCumulativeWeights(int[] weights)
	{
		int[] lookup = new int[weights.length];
		int total = 0;

		for (int i = 0; i < weights.length; i++)
		{
			total += weights[i];
			lookup[i] = total;
		}

		return lookup;
	}
}