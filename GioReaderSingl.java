package com.serg.main;

import java.io.File;
import java.io.IOException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import java.net.InetAddress;

class GioReaderSingl {
    private static GioReaderSingl single_instance = null;
    
    public DatabaseReader reader;

    public String getCountryByIP(String ip) {
        String country = null;
        try {
            InetAddress ipAdd = InetAddress.getByName(ip);
            CityResponse response = GioReaderSingl.getInstance().reader.city(ipAdd);
            country = response.getCountry().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return country;
    }
    
    private GioReaderSingl() throws IOException
    {
        File database = new File("tab/src/main/java/com/serg/main/GeoLite2-City.mmdb");
        reader = new DatabaseReader.Builder(database).build();
    }
    
    public static GioReaderSingl getInstance() throws IOException
    {
        if (single_instance == null)
            single_instance = new GioReaderSingl();
    
        return single_instance;
    }
}
