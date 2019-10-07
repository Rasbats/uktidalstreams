package com.rossiter.mike.uktidalstreams;

/**
 * Created by mike on 24/02/18.
 */
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.util.Log;

public class DiamondParser {
    public static List<Diamond> parse(InputStream is) {
        List<Diamond> diamondsList = null;
        try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            DiamondParserHandler saxHandler = new DiamondParserHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `Employee list`
            diamondsList = saxHandler.getDiamonds();

        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed");
        }

        // return Employee list
        return diamondsList;
    }
}