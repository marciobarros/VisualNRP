package sobol.problems.requirements.command;

import sobol.problems.requirements.calc.LandscapeReleasePlanning;
import sobol.problems.requirements.instance.Instance;

/**
 * Class that represents the command that generates the release-planning report
 * 
 * @author Marcio
 */
class CommandReleasePlanningLandscape extends Command
{
	private String instanceName = "";
	
	private String outputFilename = "";
	
	/**
	 * Initializes the command
	 */
	public CommandReleasePlanningLandscape()
	{
		super("LRPR", "Landscape report for the release planning problem");
		addParameterHelp("-i", "Instance name");
		addParameterHelp("-o", "Output filename");
	}

	/**
	 * Parses the parameters used by the command
	 */
	@Override
	public void parseParameters(String[] parameters) throws Exception
	{
		instanceName = getParameterValue(parameters, "-i");
		outputFilename = getParameterValue(parameters, "-o");
	}

	/**
	 * Runs the command
	 */
	@Override
	public boolean run() throws Exception
	{
		Instance instance = Instance.get(instanceName);
		
		if (instance == null)
			throw new Exception("Instance '" + instanceName + "' not found.");
		
		return new LandscapeReleasePlanning().execute(instance, outputFilename);
	}

	/**
	 * Creates a new instance for the command
	 */
	@Override
	public Command clone()
	{
		return new CommandReleasePlanningLandscape();
	}
}