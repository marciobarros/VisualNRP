package br.unirio.visualnrp.algorithm.search;

import java.util.Arrays;

import br.unirio.visualnrp.model.Project;

/**
 * Class the represents a solution for a search algorithm
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public class Solution
{
	private Project project;
	private int[][] customerRequirementsPrecedences;
	private int[] currentRequirementSelection;
	private boolean[] currentCustomerSelection;
	private int cost;
	private int profit;
	private double profitRisk;
	private double costRisk;
	private double worstCost;
	
	/**
	 * Initializes the solution, given a project
	 */
	public Solution(Project project)
	{
		this.project = project;

		prepareCustomersRequirementPrecedences();
		//checkConsistency(project);
		
		int customerCount = project.getCustomerCount();
		this.currentCustomerSelection = new boolean[customerCount];
		
		int requirementCount = project.getRequirementCount();
		this.currentRequirementSelection = new int[requirementCount];
		
		this.cost = 0;
		this.profit = 0;
		this.profitRisk = 0.0;
		this.costRisk = 0.0;
		this.worstCost = 0.0;
	}
	
	/**
	 * Initializes the solution, given a project
	 */
	public Solution(Project project, boolean[] selectedCustomers)
	{
		this(project);
		setAllCustomers(selectedCustomers);
	}
	
	/**
	 * Initializes the solution, given a solution
	 */
	public Solution(Solution source)
	{
		this.project = source.project;
		
		int customerCount = project.getCustomerCount();
		this.currentCustomerSelection = Arrays.copyOf(source.currentCustomerSelection, customerCount);

		int requirementCount = project.getRequirementCount();
		this.currentRequirementSelection = Arrays.copyOf(source.currentRequirementSelection, requirementCount);
		
		this.customerRequirementsPrecedences = source.customerRequirementsPrecedences;
		
		this.cost = source.cost;
		this.profit = source.profit;
		this.profitRisk = source.profitRisk;
		this.costRisk = source.costRisk;
		this.worstCost = source.worstCost;
	}
	
	/**
	 * Prepare the lists of precedent requirements for all customers
	 */
	private void prepareCustomersRequirementPrecedences()
	{
		int customerCount = project.getCustomerCount();
		int requirementCount = project.getRequirementCount();
		this.customerRequirementsPrecedences = new int[customerCount][requirementCount+1];
		
		for (int i = 0; i < customerCount; i++)
			prepareCustomerRequirementPrecedence(project, i);
	}
	
	/**
	 * Prepare the list of precedent requirements for a given customer
	 */
	private void prepareCustomerRequirementPrecedence(Project project, int customerIndex)
	{
		int customerRequirementCount = project.getCustomerRequirementsCount(customerIndex);
		int position = 0;
		
		for (int i = 0; i < customerRequirementCount; i++)
		{
			int requirementIndex = project.getCustomerRequirementIndex(customerIndex, i);

			if (requirementIndex >= 0)
				position = prepareCustomerRequirementAndPrecedents(project, customerIndex, requirementIndex, position);
		}
		
		this.customerRequirementsPrecedences[customerIndex][position] = -1;
	}
	
	/**
	 * Prepare the list of precedent requirements for a given customer from a given requirement
	 */
	private int prepareCustomerRequirementAndPrecedents(Project project, int customerIndex, int requirementIndex, int position)
	{
		if (checkCustomerHasRequirement(customerIndex, requirementIndex, position))
			return position;
		
		this.customerRequirementsPrecedences[customerIndex][position] = requirementIndex;
		position++;

		if (project.requirementHasPrecedents(requirementIndex))
		{
			int dependencies = project.getRequirementPrecedentsCount(requirementIndex);
			
			for (int i = 0; i < dependencies; i++)
			{
				int source = project.getRequirementPrecedentIndex(requirementIndex, i);
				
				if (source >= 0)
					position = prepareCustomerRequirementAndPrecedents(project, customerIndex, source, position);
			}
		}
		
		return position;
	}
	
	/**
	 * Checks whether a customer has a given requirement in the precedence list being built
	 */
	private boolean checkCustomerHasRequirement(int customerIndex, int requirementIndex, int maxPosition)
	{
		int requirement;
		
		for (int i = 0; i < maxPosition && (requirement = this.customerRequirementsPrecedences[customerIndex][i]) >= 0; i++)
			if (requirement == requirementIndex)
				return true;

		return false;
	}
	
	/**
	 * Checks the consistency of the solution
	 */
	private void checkConsistency(Project project)
	{
		int count = project.getCustomerCount();
		
		for (int i = 0; i < count; i++)
			checkCustomerCompleteRequirements(project, i);
	}
	
	/**
	 * Checks the consistency of a given customer
	 */
	private void checkCustomerCompleteRequirements(Project project, int customerIndex)
	{
		int count = project.getCustomerRequirementsCount(customerIndex);
		
		for (int i = 0; i < count; i++)
		{
			int requirementIndex = project.getCustomerRequirementIndex(customerIndex, i);
			
			if (requirementIndex >= 0)
				checkCustomerRequirementAndPrecedents(project, customerIndex, requirementIndex);
		}
	}
	
	/**
	 * Checks the consistency of a given customer and requirement
	 */
	private void checkCustomerRequirementAndPrecedents(Project project, int customerIndex, int requirementIndex)
	{
		int requirementCount = project.getRequirementCount();
		
		if (!checkCustomerHasRequirement(customerIndex, requirementIndex, requirementCount+1))
			System.out.println("Customer #" + customerIndex + " does not have requirement #" + requirementIndex);
		
		if (!project.requirementHasPrecedents(requirementIndex))
			return;

		int source;
				
		for (int i = 0; (source = project.getRequirementPrecedentIndex(requirementIndex, i)) >= 0; i++)
			checkCustomerRequirementAndPrecedents(project, customerIndex, source);
	}
	
	/**
	 * Return an array representing the current solution
	 */
	public boolean[] getSolution()
	{
		return currentCustomerSelection;
	}
	
	/**
	 * Returns the cost of the current solution
	 */
	public int getCost()
	{
		return cost;
	}
	
	/**
	 * Returns the profit of the current solution
	 */
	public int getProfit()
	{
		return profit;
	}
	
	/**
	 * Returns the profit-related risk of the current solution
	 */
	public double getProfitRisk()
	{
		return profitRisk;
	}
	
	/**
	 * Returns the cost-related risk of the current solution
	 */
	public double getCostRisk()
	{
		return costRisk;
	}
	
	/**
	 * Returns the worst-case cost of the current solution
	 */
	public double getWorstCost()
	{
		return worstCost;
	}
	
	/**
	 * Sets the presence of all customers in the solution
	 */
	public void setAllCustomers(boolean[] newSelection)
	{
		int customerCount = project.getCustomerCount();
		
		for (int i = 0; i < customerCount; i++)
			currentCustomerSelection[i] = newSelection[i];
		
		int requirementCount = project.getRequirementCount();
		
		for (int i = 0; i < requirementCount; i++)
			currentRequirementSelection[i] = 0;
		
		this.cost = 0;
		this.costRisk = 0.0;
		this.worstCost = 0.0;
		
		for (int i = 0; i < customerCount; i++)
			if (currentCustomerSelection[i])
				addCustomerRequirementsCostAndRisk(i);
		
		this.profit = 0;
		this.profitRisk = 0;
		
		for (int i = 0; i < customerCount; i++)
			if (currentCustomerSelection[i])
			{
				this.profit += project.getCustomerProfit(i);
				this.profitRisk += project.getCustomerProfitRisk(i);
			}
		
		this.profitRisk = round(profitRisk);
	}
	
	/**
	 * Flips the presence of a customer in the solution
	 */
	public void flipCustomer(int customerIndex)
	{
		if (this.currentCustomerSelection[customerIndex])
		{
			this.currentCustomerSelection[customerIndex] = false;
			removeCustomerRequirementsCostAndRisk(customerIndex);
			this.profit -= project.getCustomerProfit(customerIndex);
			this.profitRisk -= project.getCustomerProfitRisk(customerIndex);
		}
		else
		{
			this.currentCustomerSelection[customerIndex] = true;
			addCustomerRequirementsCostAndRisk(customerIndex);
			this.profit += project.getCustomerProfit(customerIndex);
			this.profitRisk += project.getCustomerProfitRisk(customerIndex);
		}

		this.profitRisk = round(profitRisk);
	}

	/**
	 * Adds the effects of a customer's requirements over cost
	 */
	private void addCustomerRequirementsCostAndRisk(int customerIndex)
	{
		int requirementIndex;
		int[] requirementsCustomer = customerRequirementsPrecedences[customerIndex];

		for (int i = 0; (requirementIndex = requirementsCustomer[i]) >= 0; i++)
		{
			if (currentRequirementSelection[requirementIndex] == 0)
			{
				cost += project.getRequirementCost(requirementIndex);
				costRisk += project.getRequirementCostRisk(requirementIndex);
				worstCost += project.getRequirementWorstCost(requirementIndex);
			}
			
			currentRequirementSelection[requirementIndex]++;
		}

		costRisk = round(costRisk);
		worstCost = round(worstCost);
	}

	/**
	 * Removes the effects of a customer's requirements over cost
	 */
	private void removeCustomerRequirementsCostAndRisk(int customerIndex)
	{
		int requirementIndex;
		int[] requirementsCustomer = customerRequirementsPrecedences[customerIndex];

		for (int i = 0; (requirementIndex = requirementsCustomer[i]) >= 0; i++)
		{
			currentRequirementSelection[requirementIndex]--;

			if (currentRequirementSelection[requirementIndex] == 0)
			{
				cost -= project.getRequirementCost(requirementIndex);
				costRisk -= project.getRequirementCostRisk(requirementIndex);
				worstCost -= project.getRequirementWorstCost(requirementIndex);
			}
		}
		
		costRisk = round(costRisk);
		worstCost = round(worstCost);
	}

	/**
	 * Prints a solution into a string
	 */
	public static String printSolution(boolean[] solution)
	{
		String s = "[" + (solution[0] ? "S" : "-");

		for (int i = 1; i < solution.length; i++)
			s += (solution[i] ? "S" : "-");

		return s + "]";
	}
	
	/**
	 * Loads a solution from a string
	 */
	public boolean fromString(String s)
	{
		if (s.length() != currentCustomerSelection.length+2)
			return false;
		
		boolean[] customers = new boolean[currentCustomerSelection.length];
		
		for (int i = 0; i < currentCustomerSelection.length; i++)
			customers[i] = (s.charAt(i+1) == 'S');
		
		setAllCustomers(customers);
		return true;
	}

	/**
	 * Copies a source solution to a target one
	 */
	public static void copySolution(boolean[] source, boolean[] target)
	{
		int len = source.length;

		for (int i = 0; i < len; i++)
			target[i] = source[i];
	}

	/**
	 * Creates a copy of the current solution
	 */
	public Solution clone()
	{
		return new Solution(this);
	}

	/**
	 * Returns the number of customers attended by a solution
	 */
	public int countAttendedCustomers()
	{
		int customerCount = currentCustomerSelection.length;
		int count = 0;
		
		for (int i = 0; i < customerCount; i++)
		{
			if (currentCustomerSelection[i])
			{
				count++;
			}
		}

		return count;
	}

	/**
	 * Checks whether a customer is attended
	 */
	public boolean isCustomerAttended(int customerIndex)
	{
		return currentCustomerSelection[customerIndex];
	}
	
	/**
	 * Rounds a negative number due to a large amount of sums and substractions
	 */
	private double round(double value)
	{
		if (value < -1e-6)
		{
			System.out.println("Rounding ...... " + value);
			return 0;
		}
		
		if (value < 0)
			return 0;
		
		return value;
	}
}