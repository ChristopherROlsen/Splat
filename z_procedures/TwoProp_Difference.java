/************************************************************
 *                     TwoPropDifference                    *
 *                          11/04/18                        *
 *                            18:00                         *
 ***********************************************************/
package z_procedures;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import probabilityDistributions.*;
import dialogs.*;

public class TwoProp_Difference {
    //  POJOs
    boolean okToContinue;
    
    int n1, n2, x1, x2;
    
    double hypothesizedDifference;
    double ciDiff_Low, ciDiff_High, pValueDiff, alpha, alphaOverTwo;
    double dbl_x1, dbl_x2, dbl_n1, dbl_n2, p1, p2, diffP1_P2 ,hypDiff;
    double zForTwoTails, zForOneTail;
    double stErrP1, stErrP2, ciLowP1, ciHighP1, ciLowP2, ciHighP2, stErrUnpooled, stErrPooled;
    double z_for_Pooled, z_for_Unpooled;
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt;
    String resultAsString, strAltHypChosen, returnStatus;
   
    Double daNewNullDiff;

    // FX Classes
    
    Button changeNull;
    Label lblNullAndAlt;    
    RadioButton hypNE, hypLT, hypGT, hypNull;
    StandardNormal standNorm;
    TextInputDialog txtDialog;         
    VBox root;

    public TwoProp_Difference() {
        System.out.println("48 TwoPropDiff");
        TwoProp_SummaryStats_Dialog diffPropDialog = new TwoProp_SummaryStats_Dialog();
                standNorm = new StandardNormal();
        okToContinue = diffPropDialog.getDataPresent();
        
        
        if (okToContinue == true) {
         n1 = diffPropDialog.getN1();
         n2 = diffPropDialog.getN2();
         x1 = diffPropDialog.getX1();
         x2 = diffPropDialog.getX2();
         
         dbl_x1 = x1;
         dbl_x2 = x2;
         dbl_n1 = n1;
         dbl_n2 = n2;
         p1 = diffPropDialog.getP1();
         p2 = diffPropDialog.getP2();
         diffP1_P2 = p1 - p2;
         hypDiff = diffPropDialog.getHypothesizedDiff();
        
        int nSuccesses = x1 + 2;
        int nFailures = n1 + n2 - nSuccesses;
 
        alpha = diffPropDialog.getLevelOfSignificance();
        alphaOverTwo = alpha / 2.0;
        
        // These functions deliver the positive z's
        
        zForOneTail = -standNorm.getInvLeftTailArea(alpha);
        zForTwoTails =  -standNorm.getInvLeftTailArea(alphaOverTwo);     
        
        double temp1 = p1 * (1.0 - p1) / dbl_n1 + p2 * (1.0 - p2) / dbl_n2;

        double p_sub_c = (dbl_x1 + dbl_x2) / (dbl_n1 + dbl_n2);

        stErrP1 = Math.sqrt(p1 * (1.0 - p1) / dbl_n1);
        stErrP2 = Math.sqrt(p2 * (1.0 - p2) / dbl_n2);
           
        ciLowP1 = p1 - zForTwoTails * stErrP1;
        ciHighP1 = p1 + zForTwoTails * stErrP1;
        ciLowP2 = p2 - zForTwoTails * stErrP2;
        ciHighP2 = p2 + zForTwoTails * stErrP2;
        
        stErrUnpooled = Math.sqrt(temp1);

        stErrPooled = Math.sqrt((p_sub_c) * (1.0  - p_sub_c) * (1 / dbl_n1 + 1.0 / dbl_n2));
        
        z_for_Pooled = (p1 - p2) / stErrPooled;
        
        z_for_Unpooled = ((p1 - p2) - hypDiff) / stErrUnpooled;

        switch (diffPropDialog.getAltHypothesis()) {
            case "NotEqual":        
                ciDiff_Low = (p1 - p2) - zForTwoTails * stErrUnpooled;
                ciDiff_High = (p1 - p2) + zForTwoTails * stErrUnpooled;
                double zLo = -Math.abs(z_for_Pooled);
                double zHi = Math.abs(z_for_Pooled);
                pValueDiff = 1.0 - standNorm.getMiddleArea(zLo, zHi);

                System.out.printf("       Prop        NSize     NSucc     prop     95Low     95High\n");
                System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #1",
                                                                                           n1,
                                                                                           x1,
                                                                                           p1,
                                                                                           ciLowP1,
                                                                                           ciHighP1);

                System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #2",
                                                                                           n2,
                                                                                           x2,
                                                                                           p2,
                                                                                           ciLowP2,
                                                                                           ciHighP2);

                System.out.printf("\n\n   p1 - p2     SE_Pooled    SE_Unpooled    z_Pooled   z_Unpooled   pValDiff   ciLow   ciHighHigh\n");
                System.out.printf("    %5.3f     %5.3f         %5.3f        %5.3f       %5.3f      %5.3f      %5.3f     %5.3f\n",
                                                                                                  diffP1_P2,
                                                                                                  stErrPooled,
                                                                                                  stErrUnpooled,
                                                                                                  z_for_Pooled,
                                                                                                  z_for_Unpooled,
                                                                                                  pValueDiff,
                                                                                                  ciDiff_Low,
                                                                                                  ciDiff_High);

            break;
        
        case "LessThan":
            ciDiff_Low = -1.0;

            ciDiff_High = (p1 - p2) + zForOneTail * stErrUnpooled;
            pValueDiff = standNorm.getLeftTailArea(z_for_Pooled);

            System.out.printf("       Prop        NSize     NSucc     prop     95Low     95High\n");
            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #1",
                                                                                       n1,
                                                                                       x1,
                                                                                       p1,
                                                                                       ciLowP1,
                                                                                       ciHighP1);

            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #2",
                                                                                       n2,
                                                                                       x2,
                                                                                       p2,
                                                                                       ciLowP2,
                                                                                       ciHighP2);

            System.out.printf("\n\n   p1 - p2     SE_Pooled    SE_Unpooled    z_Pooled   z_Unpooled   pValDiff   ciLow   ciHighHigh\n");
            System.out.printf("    %5.3f     %5.3f         %5.3f        %5.3f       %5.3f      %5.3f      %5.3f     %5.3f\n",
                                                                                              diffP1_P2,
                                                                                              stErrPooled,
                                                                                              stErrUnpooled,
                                                                                              z_for_Pooled,
                                                                                              z_for_Unpooled,
                                                                                              pValueDiff,
                                                                                              ciDiff_Low,
                                                                                              ciDiff_High);
            
            break;
            
        case "GreaterThan":
            ciDiff_Low = (p1 - p2) - zForOneTail * stErrUnpooled;
            ciDiff_High = 1.0;
            pValueDiff = standNorm.getRightTailArea(z_for_Pooled);

            System.out.printf("       Prop        NSize     NSucc     prop     95Low     95High\n");
            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #1",
                                                                                       n1,
                                                                                       x1,
                                                                                       p1,
                                                                                       ciLowP1,
                                                                                       ciHighP1);

            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #2",
                                                                                       n2,
                                                                                       x2,
                                                                                       p2,
                                                                                       ciLowP2,
                                                                                       ciHighP2);

            System.out.printf("\n\n   p1 - p2     SE_Pooled    SE_Unpooled    z_Pooled   z_Unpooled   pValDiff   ciLow   ciHighHigh\n");
            System.out.printf("    %5.3f     %5.3f         %5.3f        %5.3f       %5.3f      %5.3f      %5.3f     %5.3f\n",
                                                                                              diffP1_P2,
                                                                                              stErrPooled,
                                                                                              stErrUnpooled,
                                                                                              z_for_Pooled,
                                                                                              z_for_Unpooled,
                                                                                              pValueDiff,
                                                                                              ciDiff_Low,
                                                                                              ciDiff_High);
           
            break;
            
        default:
            break;
            }   
        }   //  end ok to continue
        
        else {    
            System.out.println(" 188 Ack!  Can't go on....");
        }
    }

    public String getReturnStatus() { return returnStatus; }
}

