package com.rossiter.mike.uktidalstreams;

/**
 * Created by mike on 17/10/17.
 */

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DiamondParserHandler extends DefaultHandler{

    private List<Diamond> diamondList;
    private String tempVal;
    private Diamond tempDiamond;

    MapActivity app;

    public DiamondParserHandler() {
        diamondList = new ArrayList<Diamond>();
    }

    public List<Diamond> getDiamonds() {
        return diamondList;
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("Table1")) {
            // create a new instance of employee
            tempDiamond = new Diamond();
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("Table1")) {
            // add it to the list
            diamondList.add(tempDiamond);
        } else if (qName.equalsIgnoreCase("PORT_NUMBER")) {
            tempDiamond.setNum(tempVal);
        } else if (qName.equalsIgnoreCase("LATITUDE")) {
            tempDiamond.setLat(tempVal);
        } else if (qName.equalsIgnoreCase("LONGITUDE")) {
            tempDiamond.setLon(tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_6")) {
            tempDiamond.setDirRate(0, tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_5")) {
            tempDiamond.setDirRate(1, tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_4")) {
            tempDiamond.setDirRate(2, tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_3")) {
            tempDiamond.setDirRate(3, tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_2")) {
            tempDiamond.setDirRate(4, tempVal);
        } else if (qName.equalsIgnoreCase("MINUS_1")) {
            tempDiamond.setDirRate(5, tempVal);
        } else if (qName.equalsIgnoreCase("ZERO")) {
            tempDiamond.setDirRate(6, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_1")) {
            tempDiamond.setDirRate(7, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_2")) {
            tempDiamond.setDirRate(8, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_3")) {
            tempDiamond.setDirRate(9, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_4")) {
            tempDiamond.setDirRate(10, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_5")) {
            tempDiamond.setDirRate(11, tempVal);
        } else if (qName.equalsIgnoreCase("PLUS_6")) {
            tempDiamond.setDirRate(12, tempVal);
        }
    }
}