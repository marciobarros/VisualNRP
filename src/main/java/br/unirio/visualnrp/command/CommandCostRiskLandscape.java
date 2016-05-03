package br.unirio.visualnrp.command;

import java.util.ArrayList;
import java.util.List;

import br.unirio.visualnrp.calc.CostRiskLandscapeReport;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.InstanceCategory;
import br.unirio.visualnrp.support.Conversion;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Class that represents the command that generates the cost risk landscape report
 * 
 * @author Marcio
 */
class CommandCostRiskLandscape extends Command
{
	private List<Instance> instances;
	private int[] budgets;
	private int[] riskLevels;
	private int[][] maximumProfits;
	private String outputFilename;
	private long seed;
	
	/**
	 * Initializes the command
	 */
	public CommandCostRiskLandscape()
	{
		super("LCRR", "Landscape report for the cost risk problem");
		
		this.instances = new ArrayList<Instance>();
		this.budgets = null;
		this.riskLevels = null;
		this.outputFilename = "";
		
		addParameterHelp("-i", "List of instances, separated by whitespaces, or category");
		addParameterHelp("-b", "Budget percentiles, separated by whitespaces (0 to 100)");
		addParameterHelp("-m", "Maximum budget for the instances and budgets (instances first)");
		addParameterHelp("-r", "Risk levels, separated by whitespaces (0 to 100)");
		addParameterHelp("-o", "Output filename");
		addParameterHelp("-s", "Fixed random number generator seed (optional)");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		parseInstanceParameter(parameters);
		parseBudgetParameter(parameters);
		parseRiskLevelParameter(parameters);
		parseMaximumProfitParameter(parameters);
		
		outputFilename = getParameterValue(parameters, "-o");
		
		String sSeedValue = getOptionalParameterValue(parameters, "-s");
		this.seed = Conversion.safeParseLong(sSeedValue, System.nanoTime());
	}

	/**
	 * Parse the parameter related to instances
	 */
	private void parseInstanceParameter(String[] parameters) throws Exception
	{
		String[] instanceNames = getParameterValues(parameters, "-i");
		
		for (String instanceName : instanceNames)
		{
			Instance instance = Instance.get(instanceName);
			
			if (instance == null)
			{
				InstanceCategory category = InstanceCategory.get(instanceName);
				
				if (category == null)
					throw new Exception("Instance '" + instanceName + "' not found.");
				
				Instance.addInstancesFromCategory(category, instances);
			}
			else
			{
				instances.add(instance);
			}
		}
	}

	/**
	 * Parse the parameter related to budget
	 */
	private void parseBudgetParameter(String[] parameters) throws Exception
	{
		String[] values = getParameterValues(parameters, "-b");
		this.budgets = asIntegerArray(values);
		
		if (this.budgets.length == 0)
			throw new Exception("No budget percentile was found.");
	}

	/**
	 * Parse the parameter related to risk levels
	 */
	private void parseRiskLevelParameter(String[] parameters) throws Exception
	{
		String[] values = getParameterValues(parameters, "-r");
		this.riskLevels = asIntegerArray(values);
		
		if (this.riskLevels.length == 0)
			throw new Exception("No risk level was found.");
	}

	/**
	 * Parse the parameter related to maximum profit
	 */
	private void parseMaximumProfitParameter(String[] parameters) throws Exception
	{
		String[] sValues = getParameterValues(parameters, "-m");
		int[] values = asIntegerArray(sValues);
		
		int instanceCount = instances.size();
		int budgetCount = budgets.length;
		
		int expectedMaximumProfits = instanceCount * budgetCount;
		int receivedMaximumProfits = values.length;
		
		if (receivedMaximumProfits != expectedMaximumProfits)
			throw new Exception("The number of maximum profit should be " + expectedMaximumProfits + ", but was " + receivedMaximumProfits + ".");
		
		this.maximumProfits = new int[instances.size()][];
		
		for (int i = 0; i < instanceCount; i++)
		{
			this.maximumProfits[i] = new int[budgetCount];
			
			for (int j = 0; j < budgetCount; j++)
				this.maximumProfits[i][j] = values[i * budgetCount + j];
		}
	}

	/**
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		PseudoRandom.init(seed);
		new CostRiskLandscapeReport().execute(instances, budgets, riskLevels, maximumProfits, outputFilename);
		return false;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandCostRiskLandscape();
	}
}