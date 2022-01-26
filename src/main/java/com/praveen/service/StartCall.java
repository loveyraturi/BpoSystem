package com.praveen.service;

import java.io.IOException;

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.pbx.Call;
import org.asteriskjava.pbx.CallerID;
import org.asteriskjava.pbx.EndPoint;
import org.asteriskjava.pbx.PBX;
import org.asteriskjava.pbx.PBXException;
import org.asteriskjava.pbx.PBXFactory;
import org.asteriskjava.pbx.TechType;
import org.asteriskjava.pbx.Trunk;
import org.asteriskjava.pbx.activities.DialActivity;
import org.asteriskjava.pbx.internal.core.AsteriskPBX;

import com.praveen.config.AsteriskSettings;

public class StartCall {
public static void main(String[] args) {
	PBXFactory.init(new AsteriskSettings());
    
    /**
     * Activities utilise an agi entry point in your dial plan.
     * You can create your own entry point in dialplan or have
     * asterisk-java add it automatically as we do here.
     */
    AsteriskPBX asteriskPbx = (AsteriskPBX) PBXFactory.getActivePBX();
    try {
		asteriskPbx.createAgiEntryPoint();
		dial();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (AuthenticationFailedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (TimeoutException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
static private void dial()
{
    try
    {
        PBX pbx = PBXFactory.getActivePBX();

        // We are going to dial from extension 100 to 5551234
        
        // The trunk MUST match the section header (e.g. [default]) that appears
        // in your /etc/asterisk/sip.d file (assuming you are using a SIP trunk).
        // The trunk is used to select which SIP trunk to dial through.
        Trunk trunk = pbx.buildTrunk("default");
        EndPoint nullEndpoint = null;
        // We are going to dial from extension 100
        EndPoint from = pbx.buildEndPoint(TechType.SIP, "100");
        // Provide confirmation to the agent which no. we are dialing by
        // showing it on their handset.
        CallerID fromCallerID = pbx.buildCallerID("", "Dialing");

        // The caller ID to display on the called parties phone.
        // On most systems the caller id name part won't display
        CallerID toCallerID = pbx.buildCallerID("5558100", "Asterisk Java is calling");
        // The party we are going to call.
        EndPoint to = pbx.buildEndPoint(TechType.SIP, trunk, "5551234");

 // Now dial. This method won't return until the call is answered
 // or the dial timeout is reached.
        DialActivity dial = pbx.dial(from, fromCallerID, to, toCallerID,null);
//        (null, from, fromCallerID, to, toCallerID);

		 // We should have a live call here.			
        Call call = dial.getNewCall();
			
        Thread.sleep(20000);
			
		 // Bit tired now so time to hangup.
        pbx.hangup(call);
    }
    catch (PBXException | InterruptedException e)
    {
        System.out.println(e);
    }

}
}
