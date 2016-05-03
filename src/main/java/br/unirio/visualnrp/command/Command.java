package br.unirio.visualnrp.command;

import java.util.ArrayList;
import java.util.List;

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
	private String code;
	
	/**
	 * A description for the command
	 */
	private String description;
	
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
	 * Returns the code for the command
	 */
	public String getCode()
	{
		return code;
	}
	
	/**
	 * Returns the description of the command
	 */
	public String getDescription()
	{
		return description;
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
	protected String[] getParameterValues(String[] parameters, String identifier) throws Exception
	{
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			throw new Exception("undefined parameter " + identifier + ". ");
		
		List<String> values = new ArrayList<String>();

		for (int i = index+1; i < parameters.length && !parameters[i].startsWith("-"); i++)
			values.add(parameters[i]);
		
		String[] result = new String[values.size()];
		
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);

		return result;
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
	protected String[] getOptionalParameterValues(String[] parameters, String identifier)
	{
		int index = getParameterIndex(parameters, identifier);
		
		if (index == -1)
			return new String[] {};

		List<String> values = new ArrayList<String>();
		
		for (int i = index+1; i < parameters.length && !parameters[i].startsWith("-"); i++)
			values.add(parameters[i]);
		
		String[] result = new String[values.size()];
		
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);

		return result;
	}
	
	/**
	 * Converte um vetor de strings em um vetor de inteiros
	 */
	protected int[] asIntegerArray(String[] values) throws Exception
	{
		int[] result = new int[values.length];
		
		for (int i = 0; i < values.length; i++)
			result[i] = Integer.parseInt(values[i]);

		return result;
	}

	/**
	 * Adds help for a parameter
	 */
	protected void addParameterHelp(String identifier, String description)
	{
		commandHelp.append("\t" + identifier + "\t" + description + "\n");
	}

	/**
	 * Adds help for a parameter
	 */
	protected void addParameterHelp(String identifier, String[] description)
	{
		commandHelp.append("\t" + identifier + "\t" + description[0] + "\n");
		
		for (int i = 1; i < description.length; i++)
			commandHelp.append("\t" + description[i] + "\n");
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