import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StaxParser {

    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public void parseFile(String filePath) throws FileNotFoundException, XMLStreamException, ParseException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(filePath));

        while (reader.hasNext()){
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()){
                StartElement startElement = nextEvent.asStartElement();

                switch (startElement.getName().getLocalPart()){
                    case "voter":
                        Date birthDay = birthDayFormat.parse(startElement.getAttributeByName(new QName("birthDay")).getValue());
                        String name = startElement.getAttributeByName(new QName("name")).getValue();
                        System.out.println(name + " " + birthDay);
                        break;
                    case "visit":
                        nextEvent = reader.nextEvent();
                        Date visitTime = visitDateFormat.parse(startElement.getAttributeByName(new QName("time")).getValue());
                        if (visitTime != null) {
                            System.out.println("Visit time " + visitTime);
                        }
                        break;
                }
            }
            if (nextEvent.isEndElement()){
                EndElement endElement = nextEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("voters")) {
                    System.out.println("end file");
                }
            }

        }


    }
}
