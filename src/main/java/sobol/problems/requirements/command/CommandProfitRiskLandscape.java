package sobol.problems.requirements.command;

import java.util.ArrayList;
import java.util.List;

import sobol.problems.requirements.instance.Instance;
import sobol.problems.requirements.instance.InstanceCategory;

/**
 * Class that represents the command that generates the release-planning report
 * 
 * @author Marcio
 */
class CommandProfitRiskLandscape extends Command
{
	private List<Instance> instances;
	private List<Integer> budgets;
	private List<Integer> riskLevels;
	private String outputFilename;
	
	/**
	 * Initializes the command
	 */
	public CommandProfitRiskLandscape()
	{
		super("LPRR", "Landscape report for the profit risk problem");
		
		this.instances = new ArrayList<Instance>();
		this.budgets = new ArrayList<Integer>();
		this.riskLevels = new ArrayList<Integer>();
		this.outputFilename = "";
		
		addParameterHelp("-i", "List of instances, separated by whitespaces, or category");
		addParameterHelp("-b", "Budget percentiles, separated by whitespaces (0 to 100)");
		addParameterHelp("-r", "Risk levels, separated by whitespaces (0 to 100)");
		addParameterHelp("-o", "Output filename");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		parseInstanceParameter(parameters);

		outputFilename = getParameterValue(parameters, "-o");
	}

	/**
	 * Parse the parameter related to instances
	 */
	private void parseInstanceParameter(String[] parameters) throws Exception
	{
		Iterable<String> instanceNames = getParameterValues(parameters, "-i");
		
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
			
			instances.add(instance);
		}
	}

	/**
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
//		Instance instance = Instance.get(instanceName);
//		
//		if (instance == null)
//			throw new Exception("Instance '" + instanceName + "' not found.");
//		
//		return new LandscapeReleasePlanning().execute(instance, outputFilename);
		return false;
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandProfitRiskLandscape();
	}
}