/**************************************************
 *            MultinomialDistribution             *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class MultinomialDistribution 
{
    int nTrialsOrSampleSize;
    int nCategories;
    int[] counts;
    double[] psOfSuccess;
    double[] cumulativeProbs;
    double cumulativeProb;
    int[] multinomial;
    double[] expectedCounts;
    int[] observedCounts;
    
    RVUtilities rvUtilities;
    
    public MultinomialDistribution(int[] expCounts)
    {
        int i;
        nTrialsOrSampleSize = 0;
        nCategories = expCounts.length;
        doInitializations();
        
        for (i = 0; i < nCategories; i++)
        {
            expectedCounts[i] = (double)expCounts[i];
            nTrialsOrSampleSize += expectedCounts[i];
        }
        double double_nTrials = (double)nTrialsOrSampleSize;
        
        for (i = 0; i < nCategories; i++)
        {
            psOfSuccess[i] = expectedCounts[i] / double_nTrials;
            cumulativeProb += psOfSuccess[i];
            cumulativeProbs[i] = cumulativeProb;
            System.out.println(" cum prob: " + cumulativeProbs[i]);
            nTrialsOrSampleSize += counts[i];
        }  
    }
        
    public MultinomialDistribution(int n_T_or_S, double[] probs)
    {
        int i;
        double totalProb = 0.0;
        nTrialsOrSampleSize = n_T_or_S;
        nCategories = probs.length;
        // counts = new int[nCategories];
        doInitializations();
        
        for (i = 0; i < nCategories; i++)
        {
            psOfSuccess[i] = probs[i];
            totalProb += psOfSuccess[i];
        }
        
        if (Math.abs(totalProb - 1.0) > .000001)
            System.out.println("Total probs not equal to zero -- normalizing");
        
        // Normalize the probabilities
        for (i = 0; i < nCategories; i++)
        {
            psOfSuccess[i] = psOfSuccess[i] / totalProb;
            cumulativeProb += psOfSuccess[i];
            cumulativeProbs[i] = cumulativeProb;
            System.out.println(" cum prob: " + cumulativeProbs[i]);
            expectedCounts[i] = this.nTrialsOrSampleSize * psOfSuccess[i];
        } 
    }
    
    private void doInitializations()
    {
        cumulativeProb = 0.0;
        cumulativeProbs = new double[nCategories];
        expectedCounts = new double[nCategories];
        psOfSuccess = new double[nCategories];
        counts = new int[nCategories];
        rvUtilities = new RVUtilities();
    }
    
    public double getPDF(int[] x)
    {
        int i;
        //  Calculate multinomial coefficient
        double lnMultCoef = RVUtilities.getLnFact(nTrialsOrSampleSize);
        for (i = 0; i < nCategories; i++)
            lnMultCoef -= RVUtilities.getLnFact(x[i]);
        
        //  Calculate the p^k's
        double lnPProds = 0;
        for (i = 0; i < nCategories; i++)
            lnPProds += x[i] * Math.log(psOfSuccess[i]);   
        
        return Math.exp(lnMultCoef + lnPProds);

    }
    
    /*
    public double getCDF(int x) ???????????????????
    {
        double cdf = 0.0;
	for (int i = 0; i <= x; i++)
		cdf += getPDF(i);
	return cdf;
    }
    */
    
    public int[] generateRandom()   // Generate how many?
    {
        int i, j;
        boolean categoryFound;
        double randy;
        rvUtilities = new RVUtilities();
        
        multinomial = new int[nCategories]; // Zero the multinomial

        for (i = 0; i < nTrialsOrSampleSize; i++)   // Start at 1 to avoid neg 
        {
            randy = rvUtilities.getUniformZeroOne();
            categoryFound = false;
            
            if (randy < cumulativeProbs[0])
            {
                categoryFound = true;
                multinomial[0]++;
            }
            
            j = 1;
            while (categoryFound == false)
            {           
                if ((cumulativeProbs[j - 1] < randy) && (randy <= cumulativeProbs[j]))
                {
                    multinomial[j]++;
                    categoryFound = true;
                }
                j++;
            }
            // Generate a single 

        }
        
	return multinomial;
    }
    
    double get_GofF_ChiSquare()
    {
        double x2;
        
        observedCounts = generateRandom();  
        
        x2 = 0;
        for (int i = 0; i < nCategories; i++)
        {
            double temp = observedCounts[i] - expectedCounts[i];
            x2 += temp*temp / expectedCounts[i];
        }
        return x2;
    }
}

