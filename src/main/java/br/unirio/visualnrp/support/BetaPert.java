package br.unirio.visualnrp.support;

/**
 * Class that samples random numbers according to beta distributions
 * 
 * @author marciobarros
 */
public class BetaPert
{
	/**
	 * Samples a gamma distribution
	 */
	private double gamma(double alfa)
	{
        double y, am, s, x, e;
        
	    do
	    {
	        do
	        {
	            y = Math.tan(Math.PI * PseudoRandom.randDouble());
	            am = alfa - 1;
	            s = Math.sqrt(2 * am + 1);
	            x = s * y + am;
	            
	    	} while (x <= 0);
	    
	        e = (1 + y * y) * Math.exp(am * Math.log(x / am) - s * y);
	        
	    } while (PseudoRandom.randDouble() > e);
	    
	    return x;
	}

	/**
	 * Samples a beta distribution
	 */
	public double beta(double v, double w, double min, double max)
	{
	    double y1 = gamma(v);
	    double y2 = gamma(w);
	    double x = y1 / (y1 + y2);
	    return min + x * (max - min);
	}

	/**
	 * Samples a beta-PERT distribution
	 */
	public double betaPert(double min, double med, double max)
	{
	    double mi = (min + 4 * med + max) / 6;
	    
	    double alfa1, alfa2;
	    
	    if (Math.abs(mi - med) > 0.001)
	        alfa1 = (mi - min) * (2 * med - min - max) / ((med - mi) * (max - min));
	    else
	        alfa1 = 3;
	    
	    if (Math.abs(mi - min) > 0.001)
	        alfa2 = alfa1 * (max - mi) / (mi - min);
	    else
	        alfa2 = 3;
	    
	    if (alfa2 == 1.0)
	        alfa2 += 1e-05;

	    double y1 = gamma(alfa1);
	    double y2 = gamma(alfa2);
	    double x = y1 / (y1 + y2);
	    return min + x * (max - min);
	}
}