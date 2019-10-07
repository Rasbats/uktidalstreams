package com.rossiter.mike.uktidalstreams;

/**
 * Created by mike on 17/10/17.
 */

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PortParserHandler extends DefaultHandler{

    private List<Port> PortList;
    private String tempVal;
    private Port tempPort;

    public PortParserHandler() {
        PortList = new ArrayList<Port>();
    }

    public List<Port> getPorts() {
        return PortList;
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("Table1")) {
            // create a new instance of employee
            tempPort = new Port();
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
            PortList.add(tempPort);
        } else if (qName.equalsIgnoreCase("PORT_NUMBER")) {
            tempPort.setNumber(tempVal);
        } else if (qName.equalsIgnoreCase("PORT_NAME")) {
            tempPort.setName(tempVal);
        } else if (qName.equalsIgnoreCase("MEAN_SPRING_RANGE")) {
            tempPort.setMeanSR(tempVal);
        } else if (qName.equalsIgnoreCase("MEAN_NEAP_RANGE")) {
            tempPort.setMeanNR(tempVal);
        }
    }
}