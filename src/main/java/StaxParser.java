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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StaxParser {

    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private static HashMap<Voter, Integer> voterCounts;
    private static HashMap<Integer, ArrayList<TimePeriod>> workTimeMap = new HashMap<>();
    private Voter voter;

    StaxParser(){
        voterCounts = new HashMap<>();
    }

    public void parseFile(String filePath) throws FileNotFoundException, XMLStreamException, ParseException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(filePath));

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();

                switch (startElement.getName().getLocalPart()) {
                    case "voter":
                        Date birthDay = birthDayFormat.parse(startElement.getAttributeByName(new QName("birthDay")).getValue());
                        voter = new Voter(startElement.getAttributeByName(new QName("name")).getValue(), birthDay);
                        break;
                    case "visit":
                        nextEvent = reader.nextEvent();
                        int count = voterCounts.getOrDefault(voter, 0);
                        voterCounts.put(voter, count + 1);
                        Integer station = Integer.parseInt(startElement.getAttributeByName(new QName("station")).getValue());
                        Date time = visitDateFormat.parse(startElement.getAttributeByName(new QName("time")).getValue());
                        stationCheckWorkTime(station, time);
                        break;
                }
            }
            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("voters")) {
                    System.out.println("end file");
                }
            }

        }
    }

    public static void stationCheckWorkTime(Integer station, Date time){
        ArrayList<TimePeriod> periods = workTimeMap.get(station);

        if(periods == null)
        {
            workTimeMap.put(station, new ArrayList<>());
            periods = workTimeMap.get(station);
        }

        TimePeriod newPeriod = new TimePeriod(time, time);

        boolean perionNotInList = true;

        for(int i = 0; i < periods.size(); i++)
        {
            TimePeriod period = periods.get(i);
            if(period.compareTo(newPeriod) == 0)
            {
                period.appendTime(time);
                perionNotInList = false;
                break;
            }
        }
        if(perionNotInList)
            periods.add(newPeriod);

    }

    public void printResult(){

        System.out.println("Duplicated voters: ");
        for(Voter voter : voterCounts.keySet())
        {
            Integer count = voterCounts.get(voter);
            if(count > 1) {
                System.out.println("\t" + voter + " - " + count);
            }
        }
    }

    public void printWorkTime() {

        for (Map.Entry<Integer, ArrayList<TimePeriod>> entry : workTimeMap.entrySet()) {
            System.out.print(entry.getKey() + ": ");
            for (TimePeriod period : entry.getValue()) {
                System.out.print(period + " ");
            }
            System.out.println();
        }
    }

}
