package br.unirio.visualnrp.reader;

import java.io.FileInputStream;
import java.util.Scanner;

import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.MaximumValues;
import br.unirio.visualnrp.model.MaximumValuesList;

/**
 * Class that reads the maximum values for the instances under interest
 *  
 * @author marciobarros
 */
public class MaximumValuesReader
{
	private int lineCounter;

	/**
	 * Loads a file containing the maximum values
	 */
	public MaximumValuesList execute()
	{
		MaximumValuesList result = new MaximumValuesList();
		
		try
		{
			Scanner scanner = new Scanner(new FileInputStream("data/requirements/max-values.txt"));
			
			this.lineCounter = 0;
			nextLine(scanner);

			loadMaximumValues(scanner, result);
			scanner.close();
			
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return result;
	}

	/**
	 * Gets the next line from the file
	 */
	private String nextLine(Scanner scanner) throws Exception
	{
		lineCounter++;
		return scanner.nextLine();
	}

	/**
	 * Loads a line from the file containing maximum values for instances
	 */
	private void loadMaximumValues(Scanner scanner, MaximumValuesList result) throws Exception
	{
		while (scanner.hasNext())
		{
			String line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (tokens.length != 4)
				addError("Invalid number of columns");
			
			int position = tokens[0].lastIndexOf("-");
			int budget = Integer.parseInt(tokens[0].substring(position+1));
			
			String instanceName = tokens[0].substring(0, position);
			Instance instance = Instance.get(instanceName);
			
			if (instance == null)
				addError("Instance not found " + instanceName);
			
			int maxProfit = Integer.parseInt(tokens[1]);
			double maxCostRisk = Double.parseDouble(tokens[2]);
			double maxProfitRisk = Double.parseDouble(tokens[3]);
			result.add(new MaximumValues(instance, budget, maxProfit, maxCostRisk, maxProfitRisk));
		}
	}

	/**
	 * Registers an error during file processing
	 */
	private void addError(String message) throws Exception
	{
		throw new Exception("Line #" + lineCounter + ": " + message);
	}
}
