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
	private final Map<Integer, Integer> profitLossRatios;

	public GreedyConstructor(Project project)
	{
		this.project = project;
		this.profitLossRatios = calculateProfitLossRatios();
	}

	private Map<Integer, Integer> calculateProfitLossRatios()
	{
		int numberOfCustomers = project.getCustomerCount();
		boolean[] sol = new boolean[numberOfCustomers];
		Map<Integer, Integer> profitLoss = new HashMap<Integer, Integer>(
				project.getCustomerCount());
		Arrays.fill(sol, false);

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

	public boolean[] generateSolution()
	{
		int customerCount = project.getCustomerCount();
		int numberOfCustomers = PseudoRandom.randInt(1, customerCount + 1);
		return generateSolutionWith(numberOfCustomers);
	}

	public boolean[] generateSolutionWith(int numberOfCustomers)
	{
		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Arrays.fill(solution, false);
		List<Integer> customers = new ArrayList<Integer>(
				profitLossRatios.keySet());

		for (int i = 0; i < numberOfCustomers; i++)
		{

			int[] weights = getWeightsFor(customers);
			WeightedSelector ws = new WeightedSelector(weights);
			int selected = ws.getWeightedRandom();
			selected = customers.remove(selected);

			solution[selected] = true;
		}

		return solution;
	}

	public boolean[] generateSolutionInInterval(int minCustomers, int maxCustomers)
	{
		int numberOfCustomers = PseudoRandom.randInt(minCustomers, maxCustomers + 1);
		return generateSolutionWith(numberOfCustomers);
	}

	private int[] getWeightsFor(List<Integer> customers)
	{
		int[] weights = new int[customers.size()];
		int idx = 0;

		for (Integer cust : customers)
		{
			weights[idx] = profitLossRatios.get(cust);
			idx++;
		}
		return weights;
	}
}

class WeightedSelector
{
	private int[] lookup;
	private int totalWeight;

	public WeightedSelector(int[] weights)
	{
		this.lookup = computeLookup(weights);
	}

	private int[] computeLookup(int[] weights)
	{
		int[] lookup = new int[weights.length];
		totalWeight = 0;

		for (int i = 0; i < weights.length; i++)
		{
			totalWeight += weights[i];
			lookup[i] = totalWeight;
		}

		return lookup;
	}

	public int getWeightedRandom()
	{
		int rand = PseudoRandom.randInt(0, totalWeight);
		int pos = Arrays.binarySearch(lookup, rand);
		if (pos < 0)
		{
			pos = Math.abs(pos) - 1;
		}

		return pos;
	}
}