package com.praveen;

import java.io.IOException;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;

public class HelloManager {

//	private ManagerConnection managerConnection;
//
//	public HelloManager() throws IOException {
//		ManagerConnectionFactory factory = new ManagerConnectionFactory("103.31.147.252",5038, "5002", "1234");
//		System.out.println("######################1");
//		this.managerConnection = factory.createManagerConnection();
//		System.out.println("######################2");
//		System.out.println(this.managerConnection);
//	}
//
//	public void call() throws IOException, AuthenticationFailedException, TimeoutException {
//		OriginateAction originateAction;
//		ManagerResponse originateResponse;
//		System.out.println("######################3");
//		originateAction = new OriginateAction();
////		originateAction.setChannel("SIP/1164");
////		originateAction.setContext("default");
////		originateAction.setExten("1164");
//		originateAction.setPriority(new Integer(1));
//		
//		//SET1
////		originateAction.setCallerId("5002");
////		originateAction.setActionId("Originate");
////		originateAction.setChannel("Dial/7710002221@AMI");
////		originateAction.setApplication("Swift");
////		 originateAction.setData("\"I'm calling from Cloudvox to wish you a very happy birthday. This message could say anything, interact with the caller, play sounds, MP3s, voices, connect them to another call, or do pretty much anything your heart desires.\"");
//		 
//		 //SET 2
////		originateAction.setApplication("Originate");
////		originateAction.setAccount("Originate");
////		 originateAction.setActionId("Originate");
//		 originateAction.setChannel("SIP/SERVER222");
////		 originateAction.setCallerId("5002");
//		 originateAction.setExten("7710002221");
//		 originateAction.setContext("default");
////		 originateAction.setChannel("SIP/SERVER222/7710002221@default");
////		 originateAction.setApplication("Swift");
////		 originateAction.setData("\"I'm calling from Cloudvox to wish you a very happy birthday. This message could say anything, interact with the caller, play sounds, MP3s, voices, connect them to another call, or do pretty much anything your heart desires.\"");
//			 
//		 originateAction.setAsync(true);
//         
//		 originateAction.setTimeout(new Long(30000));
//
//		System.out.println("######################4");
//		// connect to Asterisk and log in
//		managerConnection.login();
//		System.out.println("######################5");
//
//		// send the originate action and wait for a maximum of 30 seconds for Asterisk
//		// to send a reply
//		originateResponse = managerConnection.sendAction(originateAction, 30000);
//		System.out.println("######################6");
//
//		// print out whether the originate succeeded or not
//		System.out.println(originateResponse.getResponse());
//
//		// and finally log off and disconnect
//		managerConnection.logoff();
//	}
//
//	public static void main(String[] args) throws Exception {
//		HelloManager helloManager = new HelloManager();
//		helloManager.call();
//	}
}