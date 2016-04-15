package sobol.problems.requirements.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents an abstract command
 * 
 * @author Marcio
 */
public abstract class Command 
{
	/**
	 * The codename for the command
	 */
	private @Getter String code;
	
	/**
	 * A description for the command
	 */
	private @Getter String description;
	
	/**
	 * Parameter help strings
	 */
	private StringBuffer commandHelp;
	
	/**
	 * Initializes the command
	 */
	protected Command(String code, String description)
	{
		this.code = code;
		this.description = description;
		this.commandHelp = new StringBuffer();
	}
	
	/**
	 * Returns the index of a given parameter
	 */
	private int getParameterIndex(String[] parameters, String identifier)
	{
		for (int i = 1; i < parameters.length; i++)
			if (parameters[i].compareTo(identifier) == 0)
				return i;
		
		return -1;
	}

	/**
	 * Returns a single value for a parameter
	 */
	protected String getParameterValue(String[] parameters, String identifier) throws Exception
	{
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			throw new Exception("undefined parameter " + identifier);
		
		int valueIndex = index + 1;
		
		if (valueIndex < parameters.length)
			return parameters[valueIndex];
		
		throw new Exception("undefined parameter value " + identifier);
	}

	/**
	 * Returns all values for a given parameter
	 */
	protected Iterable<String> getParameterValues(String[] parameters, String identifier) throws Exception
	{
		List<String> values = new ArrayList<String>();
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			throw new Exception("undefined parameter " + identifier);
		
		for (int i = index+1; i < parameters.length; i++)
			if (parameters[i].startsWith("-"))
				values.add(parameters[i]);

		return values;
	}

	/**
	 * Returns a single value for an optional parameter
	 */
	protected String getOptionalParameterValue(String[] parameters, String identifier)
	{
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			return null;
		
		int valueIndex = index + 1;
		
		if (valueIndex < parameters.length)
			return parameters[valueIndex];
		
		return null;
	}

	/**
	 * Returns all values for an optional parameter
	 */
	protected Iterable<String> getOptionalParameterValues(String[] parameters, String identifier)
	{
		List<String> values = new ArrayList<String>();
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			return values;
		
		for (int i = index+1; i < parameters.length; i++)
			if (parameters[i].startsWith("-"))
				values.add(parameters[i]);

		return values;
	}

	/**
	 * Adds help for a parameter
	 */
	protected void addParameterHelp(String identifier, String description)
	{
		commandHelp.append("\t" + identifier + "\t" + description);
	}

	/**
	 * Adds help for a parameter
	 */
	protected void addParameterHelp(String identifier, String[] description)
	{
		commandHelp.append("\t" + identifier + "\t" + description[0]);
		
		for (int i = 1; i < description.length; i++)
			commandHelp.append("\t" + description[i]);
	}
	
	/**
	 * Shows the help for the command
	 */
	public String help()
	{
		return commandHelp.toString();
	}
	
	/**
	 * Parses the parameters used by the command
	 */
	public abstract void parseParameters(String[] parameters) throws Exception;

	/**
	 * Runs the command
	 */
	public abstract boolean run() throws Exception;

	/**
	 * Creates a new instance for the command
	 */
	public abstract Command clone();
}