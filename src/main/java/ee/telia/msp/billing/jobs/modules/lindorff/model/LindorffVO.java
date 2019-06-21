package ee.telia.msp.billing.jobs.modules.lindorff.model;


public class LindorffVO{

    public String currency;
    public String amount;
    public String beginning;
    public String description;
    public String end;
    public String period;
    public String rate;
    public String petitionerCosts;
    public String petitionerStateFee;
    public String principalClaim;
    public String representativeCosts;
    public String representativeStateFee;
    public String deadline;
    public String decisionCode;
    public String decisionDate;
    public String reasonCode;
    public String reasonComment;
    public String queryMode;
    public String requestId;
    public String senderCode;


    public String toString() {
        return "[LindorffVO: requestId="               + requestId +              ";" + 
                            "currency="                + currency  +              ";" + 
                            "amount="                  + amount    +              ";" + 
                            "beginning="               + beginning +              ";" + 
                            "description"              + description +            ";" + 
                            "end="                     + end +                    ";" + 
                            "period="                  + period +                 ";" + 
                            "rate="                    + rate +                   ";" + 
                            "petitionerCosts="         + petitionerCosts +        ";" + 
                            "petitionerStateFee="      + petitionerStateFee +     ";" + 
                            "principalClaim="          + principalClaim         + ";" +  
                            "representativeCosts="     + representativeCosts    + ";" +  
                            "representativeStateFee="  + representativeStateFee + ";" +  
                            "deadline="                + deadline +               ";" + 
                            "decisionCode="            + decisionCode +           ";" + 
                            "decisionDate="            + decisionDate +           ";" + 
                            "reasonCode="              + reasonCode +             ";" + 
                            "reasonComment="           + reasonComment +          ";" + 
                            "queryMode="               + queryMode +              ";" + 
                            "senderCode="              + senderCode + 
                            "]";
    }

    //excpetions catched outside of this fn.
    public LindorffVO mapData(String line){
        String[] array = line.split(";");

        currency               = array[0];
        amount                 = array[1];
        beginning              = array[2];
        description            = array[3];
        end                    = array[4];
        period                 = array[5];
        rate                   = array[6];
        petitionerCosts        = array[7];
        petitionerStateFee     = array[8];
        principalClaim         = array[9];
        representativeCosts    = array[10];
        representativeStateFee = array[11];
        deadline               = array[12];
        decisionCode           = array[13];
        decisionDate           = array[14];
        reasonCode             = array[15];
        reasonComment          = array[16];
        queryMode              = array[17];
        requestId              = array[18];
        senderCode             = array[19];

        return this;
    }

    public boolean compare(LindorffVO vo){
        if(!(currency.equals(vo.currency)))
            return false;
        if(!(amount.equals(vo.amount)))
            return false;
        if(!(beginning.equals(vo.beginning)))
            return false;
        if(!(description.equals(vo.description)))
            return false;
        if(!(end.equals(vo.end)))
            return false;
        if(!(period.equals(vo.period)))
            return false;
        if(!(rate.equals(vo.rate)))
            return false;
        if(!(petitionerCosts.equals(vo.petitionerCosts)))
            return false;
        if(!(petitionerStateFee.equals(vo.petitionerStateFee)))
            return false;
        if(!(principalClaim.equals(vo.principalClaim)))
            return false;
        if(!(representativeCosts.equals(vo.representativeCosts)))
            return false;
        if(!(representativeStateFee.equals(vo.representativeStateFee)))
            return false;
        if(!(deadline.equals(vo.deadline)))
            return false;
        if(!(decisionCode.equals(vo.decisionCode)))
            return false;
        if(!(decisionDate.equals(vo.decisionDate)))
            return false;
        if(!(reasonCode.equals(vo.reasonCode)))
            return false;
        if(!(reasonComment.equals(vo.reasonComment)))
            return false;
        if(!(queryMode.equals(vo.queryMode)))
            return false;
        if(!(requestId.equals(vo.requestId)))
            return false;
        if(!(senderCode.equals(vo.senderCode)))
            return false;

        return true;

    }
}

