package sobol.problems.requirements.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a command factory
 * 
 * @author Marcio
 */
public class CommandFactory 
{
	/**
	 * Singleton instance of the command factory
	 */
	private static CommandFactory instance = null;

	/**
	 * List of available commands
	 */
	private List<Command> commands;
	
	/**
	 * Initializes the command factory
	 */
	private CommandFactory()
	{
		this.commands = new ArrayList<Command>();
		this.commands.add(new CommandReleasePlanningLandscape());
		this.commands.add(new CommandCostRiskLandscape());
		this.commands.add(new CommandProfitRiskLandscape());
	}
	
	/**
	 * Gets the singleton instance of the command factory
	 */
	public static CommandFactory getInstance()
	{
		if (instance == null)
			instance = new CommandFactory();
		
		return instance;
	}

	/**
	 * Gets a command with a given identifier
	 */
	public Command getCommand(String code)
	{
		for (Command command : commands)
			if (command.getCode().compareTo(code) == 0)
				return command;
		
		return null;
	}

	/**
	 * Creates a new command of a given type
	 */
	public Command createCommand(String code)
	{
		Command command = getCommand(code);
		
		if (command != null)
			return command.clone();
		
		return null;
	}

	/**
	 * Creates the text for the general help of the program
	 */
	public String generalHelp() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Please, use one of the following commands:\n");

		for (Command command : commands)
			sb.append(command.getCode() + ": " + command.getDescription() + "\n");
			
		return sb.toString();
	}

	/**
	 * Creates the text for the help for a given command of the program
	 */
	public String commandHelp(Command command) 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Please, use the following parameters for the " + command.getCode() + " command:\n");
		sb.append(command.help());
		return sb.toString();
	}
}