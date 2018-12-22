/**************************************************
 *                   JustAnAxis                   *
 *                    05/16/18                    *
 *                     12:00                      *          
 *************************************************/

package genericClasses;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.chart.ValueAxis;

public class JustAnAxis extends ValueAxis<Number> {
    // POJOs
    boolean hasForcedIntegerMajorTicks, hasForcedLowEndOfScale, 
            hasForcedHighEndOfScale, binsCreated;
    int nMajorTix, nMajorIntervals, nBins;
        
    double  originalTUP, binSize, minMajorTick, maxMajorTick, majorTickRange,
            binWidth, lowerBound, upperBound, forcedLowEndOfScaleIs, 
            forcedHighEndOfScaleIs;
    double[] originalScaleRecs,  newScaleRecs;
    
    String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();   
    
    // My classes
    ScaleRecommendations scaleRecs;
    
    // POJOs / FX
    public DoubleProperty theUpperBound = new SimpleDoubleProperty();
    public DoubleProperty theLowerBound = new SimpleDoubleProperty();

    // tickUnitProperty is the distance between major ticks
    public SimpleObjectProperty<Double> tickUnitProperty = new SimpleObjectProperty<>(1d); 

    Side side;
    
    Number[] range;
    List<Number> majorTickMarkPositions;
    
    public JustAnAxis() { }

    public JustAnAxis(double lowerBound, double upperBound) {
        super(lowerBound, upperBound); 
        if (upperBound <= lowerBound) {
            System.out.println("Ack!!!  -->  upperBound <= lowerBound in JustAnAxis");
            System.out.println("Lower / Upper = " + lowerBound + " / " + upperBound);
            int x, y, z;
            x = 0; y = 0; z = y / x;
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        // This is a default initialization, needed for construction apparently
        // The graph should always (re)set this at/near construction 
        setSide(Side.BOTTOM);  
        originalScaleRecs = new double[4];
        newScaleRecs = new double[4];
        setBounds(lowerBound, upperBound);
    }
    
    
    public void setBounds(double thisLowerBound, double thisUpperBound){  
        range = new Number[2];
        range[0] = thisLowerBound;
        range[1] = thisUpperBound;
        setMinorTickCount(10);
        originalScaleRecs[0] = lowerBoundProperty().get();
        originalScaleRecs[1] = upperBoundProperty().get();
        originalScaleRecs[2] = tickUnitProperty.get();
        originalScaleRecs[3] = getMinorTickCount();
        originalTUP = originalScaleRecs[2];
        bindBoundsToDefaultBounds();
        calculateRecommendedScaleInfo();  
    }

    /**
     * Bind our bounds with the super class bounds.
     */
    private void bindBoundsToDefaultBounds() {                                      
        theLowerBound.bind(new DoubleBinding() {
            {
                super.bind(lowerBoundProperty());
            }

            @Override
            protected double computeValue() {
                return (lowerBoundProperty().get());
            }
        });
        theUpperBound.bind(new DoubleBinding() {
            {
                super.bind(upperBoundProperty());
            }

            @Override
            protected double computeValue() {
                return (upperBoundProperty().get());
            }
        });
    }
    
    public void calculateRecommendedScaleInfo() {        
        scaleRecs = new ScaleRecommendations(originalScaleRecs);
        newScaleRecs = scaleRecs.createMyAdvice(); 
        lowerBoundProperty().set(newScaleRecs[0]);
        upperBoundProperty().set(newScaleRecs[1]);
        range[0] = theLowerBound.get();
        range[1] = theUpperBound.get();
        tickUnitProperty.set(newScaleRecs[2]);
        setMinorTickCount((int)newScaleRecs[3]);         
        double length = getDisplayPosition(range[1]) - getDisplayPosition(range[0]);
        calculateTickValues(length, range);
        calculateMinorTickMarks();
    }

    @Override
    protected List<Number> calculateMinorTickMarks() {
        range = getRange();
        List<Number> minorTickMarkPositions = new ArrayList<Number>();
        if (range != null)
        {
            Number lBound = range[0];
            Number uBound = range[1];
            lowerBound = lBound.doubleValue();
            upperBound = uBound.doubleValue();
            double minorTickUnit = tickUnitProperty.get() / getMinorTickCount();
            double start = Math.floor(getLowerBound() / minorTickUnit) * minorTickUnit;
            for (double value = start; value < getUpperBound(); value += minorTickUnit)
            {
                minorTickMarkPositions.add(value);
            }
        }
        return minorTickMarkPositions;
    }

    @Override
    protected List<Number> calculateTickValues(double length, Object range){
        nMajorTix = 0;
        majorTickMarkPositions = new ArrayList<Number>();
        
        if (range != null)
        {
            Number lBound = ((Number[]) range)[0];
            Number uBound = ((Number[]) range)[1];
            lowerBound = lBound.doubleValue();
            upperBound = uBound.doubleValue();
            double tickUnit = tickUnitProperty.get();    
            double startInterval = Math.floor(lowerBound / tickUnit) * tickUnit;
            double stopInterval = upperBound;
            for (double value = startInterval; value < stopInterval; value += tickUnit)
            {
                majorTickMarkPositions.add(value);
                nMajorTix++;
            }
        }
        return majorTickMarkPositions;
    }
    
    public List<Number> getMajorTickMarkPositions() { return majorTickMarkPositions; }

    @Override
    protected String getTickMarkLabel(Number value) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumIntegerDigits(6);
        formatter.setMinimumIntegerDigits(1);
        return formatter.format(value);
    }

    @Override
    protected Number[] getRange() {
        return new Number[] { lowerBoundProperty().get(), upperBoundProperty().get() };
    }
    
    @Override
    protected void setRange(Object range, boolean animate) { //   Animate not used
        if (range != null) {
            Number lowerBound = ((Number[]) range)[0];
            Number upperBound = ((Number[]) range)[1];
            lowerBoundProperty().set(lowerBound.doubleValue());
            upperBoundProperty().set(upperBound.doubleValue());
        }
    }
    
    public SimpleObjectProperty<Double> getTickUnitProperty() {
        return tickUnitProperty;
    }
    
    @Override
    public Number getValueForDisplay(double displayPosition) {
        double delta = theUpperBound.get() - theLowerBound.get();
        if (getSide().isVertical()) {
            return (((displayPosition - getHeight()) / -getHeight()) * delta) + theLowerBound.get();
        } else {
            return (((displayPosition / getWidth()) * delta) + theLowerBound.get());
        }
    }

    @Override
    public double getDisplayPosition(Number value) {  // In display units
        double delta = theUpperBound.get() - theLowerBound.get();
        double deltaV = (value.doubleValue()) - theLowerBound.get();      
        if (getSide().isVertical()) {
            return (1. - ((deltaV) / delta)) * getHeight();
        } else 
        {
            return ((deltaV) / delta) * getWidth();
        }
    }  
        
    private class ScaleRecommendations  {
        double lowerBound, upperBound, tickProperty, minorTickCount, reallyBoolean;
        double recLowerBound, recUpperBound, recTickProperty, recMinorTickCount;
        double range, approxN_MajorTix;
 
        double ordMagLB, ordMagUB, orderOfMag, orderOfMagMinusOne;
        double adjustedLB, adjustedUB, minorTickNearUB, minorTickNearLB;
        double majorTickInterval, minorTickInterval, thisManyMinorTix;      
        double signumLB, signumUB;
        
        double[] scaleRecommendations = new double[4];

        ScaleRecommendations()  { }

        ScaleRecommendations(double[] initRecs){
            lowerBound = initRecs[0];
            upperBound = initRecs[1];
            tickProperty = initRecs[2];
            minorTickCount = initRecs[3];
            range = upperBound - lowerBound;
        }

        public double[] createMyAdvice() {
            ordMagLB = ordMagUB = orderOfMag = orderOfMagMinusOne = 0.0;
            adjustedLB = adjustedUB = minorTickNearUB = minorTickNearLB = 0;
            recLowerBound = recUpperBound = 0.0;
            majorTickInterval = minorTickInterval = 0.0;
            thisManyMinorTix = 10.0;
            
            boolean intervalCrossesZero = false;

            double doubleTUP = tickUnitProperty.get();
            
            // Worry not about logs of 0
            if (lowerBound == 0.0)
                 lowerBound = -0.0001;

            if (upperBound == 0.0)
                upperBound = +0.0001;            
            // Determine location of the interval in re zero
            signumLB = Math.signum(lowerBound); signumUB = Math.signum(upperBound);                
            // Move adjusted interval so that both ends are positive
            if ((signumLB < 0) && (signumUB < 0)) {
                adjustedLB = Math.abs(upperBound);
                adjustedUB = Math.abs(lowerBound);
            }
            else if ((signumLB > 0) && (signumUB > 0)) {
                adjustedLB = Math.abs(lowerBound);
                adjustedUB = Math.abs(upperBound);
            }
            else if ((signumLB < 0) && (signumUB > 0))
            {
                intervalCrossesZero = true; //  Needed for scale maybe
                if (Math.abs(lowerBound) <= Math.abs(upperBound)) {
                    adjustedLB = Math.abs(lowerBound);
                    adjustedUB = upperBound;
                } 
                else
                 // Upper bound closer to zero
                if (Math.abs(lowerBound) > Math.abs(upperBound)) {
                    adjustedLB = upperBound;
                    adjustedUB = Math.abs(lowerBound);
                }   
                else { System.out.println("In ContinAxis: Uh-oh, trouble with LB/UB"); }    
            }                         
            ordMagLB = ordMagUB = 0;
            
            if (signumLB != 0.0)
                ordMagLB = (int)Math.floor(Math.log10(Math.abs(adjustedLB)));
                        
            if (signumUB != 0.0) {
                ordMagUB = (int)Math.floor(Math.log10(Math.abs(adjustedUB)));
            }           
            orderOfMag = Math.max(ordMagLB, ordMagUB);
            orderOfMagMinusOne = orderOfMag - 1.0;
            majorTickInterval = Math.pow(10.0, orderOfMag); 

            approxN_MajorTix = Math.floor(range/majorTickInterval);            
            double multiplier = 5.0;
            while (approxN_MajorTix > .5) {
                majorTickInterval *= multiplier;
                //  Oscillate between halfs and tenths
                if (multiplier == 5.0)
                    multiplier = 2.0;
                else
                    multiplier = 5.0;
                
                approxN_MajorTix = Math.floor(range/majorTickInterval);
            }
            if ((ordMagLB == ordMagUB) && (intervalCrossesZero == true)) {
                majorTickInterval /= 10.0;
            }  else {
                double divider = 5.0;
                while (range < 2.0 * majorTickInterval) {
                    majorTickInterval /= divider;
                    //  Oscillate between halfs and tenths
                    if (divider == 5.0) {
                        divider = 2.0;
                    } else {
                        divider = 5.0;
                    }
                    approxN_MajorTix = Math.floor(range/majorTickInterval);
                }   // end while
            }   //  end else
            minorTickInterval = majorTickInterval / 10.0;            
            setMinorTickCount(5);
            if (binSize <= 0.0)
            {
                if (orderOfMag != 1.0) {
                    minorTickNearUB = (Math.floor(upperBound / orderOfMagMinusOne)) * orderOfMagMinusOne;
                    if (minorTickNearUB <= upperBound)
                        recUpperBound = upperBound + 2.0 * minorTickInterval;
                    else
                        recUpperBound = upperBound + minorTickInterval;
                    minorTickNearLB = (Math.floor(lowerBound / orderOfMagMinusOne)) * orderOfMagMinusOne;
                    if (minorTickNearLB >= lowerBound)
                        recLowerBound = lowerBound - 2.0 * minorTickInterval;
                    else
                        recLowerBound = lowerBound - minorTickInterval;
                }   else {
                    recLowerBound = lowerBound - 2. * minorTickInterval;
                    recUpperBound = upperBound + 2. * minorTickInterval;
                }
            } else {
                if (orderOfMag != 1.0) {
                    minorTickNearUB = (Math.floor(upperBound / orderOfMagMinusOne)) * orderOfMagMinusOne;
                    if (minorTickNearUB <= upperBound)
                        recUpperBound = upperBound + 2.0 * binSize;
                    else
                        recUpperBound = upperBound + binSize;
                    minorTickNearLB = (Math.floor(lowerBound / orderOfMagMinusOne)) * orderOfMagMinusOne;
                    if (minorTickNearLB >= lowerBound)
                        recLowerBound = lowerBound - 2.0 * binSize;
                    else
                        recLowerBound = lowerBound - binSize;
                }   else {
                    recLowerBound = lowerBound - 2. * binSize;
                    recUpperBound = upperBound + 2. * binSize;
                }                
            }                
            if(getHasForcedIntTix() && (majorTickInterval <= 1.0)) {
                majorTickInterval = 1.0;
                setMinorTickVisible(false);
                thisManyMinorTix = 0.;
            } 
            if (getHasForcedLowScaleEnd())
                scaleRecommendations[0] = getForcedLowScaleEnd();
            else
                scaleRecommendations[0] = recLowerBound;
            
            if (getHasForcedHighScaleEnd())
                scaleRecommendations[1] = getForcedHighScaleEnd();
            else
                scaleRecommendations[1] = recUpperBound;

            scaleRecommendations[2] = majorTickInterval;
            scaleRecommendations[3] = thisManyMinorTix;
            
            return scaleRecommendations;
        }
    }
    
    public int getNMajorTix()   { return nMajorTix; }
    
        public boolean getHasForcedIntTix() { return hasForcedIntegerMajorTicks; }
    
    public void setForceIntTix(boolean forceIntTix) { 
        hasForcedIntegerMajorTicks = forceIntTix;
    }    
    
    public void forceLowScaleEndToBe( double thisLowEnd)  {
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
        setBounds(thisLowEnd, upperBound);
        calculateRecommendedScaleInfo();  
    }
    
    public void forceHighScaleEndToBe( double thisHighEnd)  { 
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;  
        setBounds(lowerBound, thisHighEnd);
        calculateRecommendedScaleInfo();
    }
    
    public void forceScaleLowAndHighEndsToBe(double thisLowEnd, double thisHighEnd)
    {
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;
        setBounds(thisLowEnd, thisHighEnd);
        calculateRecommendedScaleInfo();   
        setBounds(lowerBound, thisHighEnd);
        calculateRecommendedScaleInfo();       
    }
    
    public boolean getHasForcedLowScaleEnd() {return hasForcedLowEndOfScale; }
    public boolean getHasForcedHighScaleEnd() {return hasForcedHighEndOfScale; }
    
    public double getForcedLowScaleEnd() {return forcedLowEndOfScaleIs; }
    public double getForcedHighScaleEnd() {return forcedHighEndOfScaleIs; }

    public void setForcedAxisEndsFalse()  { //  i.e. return to unforced state
        hasForcedLowEndOfScale = false;
        hasForcedHighEndOfScale = false;
    }
}


