package br.unirio.visualnrp.support;

/**
 * Utility class for type conversion
 *  
 * @author Marcio Barros
 */
public class Conversion 
{
	/**
	 * Converts a string to a long integer value, using a default in case of exceptions
	 */
	public static long safeParseLong(String s, long _default)
	{
		try 
		{
			return Long.parseLong(s);
		}
		catch (Exception e)
		{
			return _default;
		}
	}

	/**
	 * Converts a string to a floating-point value, using a default in case of exceptions
	 */
	public static double safeParseDouble(String s, double _default)
	{
		try 
		{
			return Double.parseDouble(s);
		}
		catch (Exception e)
		{
			return _default;
		}
	}

	/**
	 * Converts a string to an integer value, using a default in case of exceptions
	 */
	public static int safeParseInteger(String s, int _default)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (Exception e)
		{
			return _default;
		}
	}
	
	/**
	 * Converts a string to a boolean value, using a default in case of exceptions
	 */
	public static boolean safeParseBoolean(String s)
	{
		if (s == null)
			return false;

		if (s.compareToIgnoreCase("ON") == 0 || s.compareToIgnoreCase("true") == 0)
			return true;
		else
			return false;
	}
}