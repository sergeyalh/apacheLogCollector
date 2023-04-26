package com.serg.main;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * LogsObj
 */
public class LogsObj {

    private static final UserAgentAnalyzer UAA = UserAgentAnalyzer
    .newBuilder()
    .withField("OperatingSystemName")
    .withField("AgentNameVersion")
    .build();
    private String _ip;
    private String _country;
    private String _userAgent;
    private String _browser;
    private String _os;

    // constructor
    LogsObj(String ip, String userAgent) {
        _ip = ip;
        _country = ipToCountry(_ip);
        _userAgent = userAgent;
        agentToBrowserAndOS(_userAgent);
        
    }

    private String ipToCountry(String ip) {
        String country = "Unknown";
        try {
            country = GioReaderSingl.getInstance().getCountryByIP(ip);
        } catch (Exception e) {
            System.out.println("IP not found: " + ip);
        }
        return country;
    }

    private void agentToBrowserAndOS(String userAgent){
        UserAgent agent = UAA.parse(userAgent);
        _browser = agent.getValue("AgentName");
        _os = agent.getValue("OperatingSystemName");
    }

    public String getCountry(){
        return _country;
    }

    public String getBrowser(){
        return _browser;
    }

    public String getOS(){
        return _os;
    }
}