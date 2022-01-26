package com.praveen.config;


import org.asteriskjava.pbx.DefaultAsteriskSettings;

public class AsteriskSettings extends DefaultAsteriskSettings
{

	@Override
	public String getManagerPassword()
	{
		// this password MUST match the password (secret=) in manager.conf
		return "1234#lovey";
	}

	@Override
	public String getManagerUsername()
	{
		// this MUST match the section header '[myconnection]' in manager.conf
        return "praveen";
	}

	@Override
	public String getAsteriskIP()
	{
		// The IP address or FQDN of your Asterisk server.
        return "localhost";
	}

	@Override
	public String getAgiHost()
	{
		// The IP Address or FQDN of you asterisk-java application.
		return "localhost";
	}

}
