import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XMLHandler extends DefaultHandler {

    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private static HashMap<Voter, Integer> voterCounts;
    private static HashMap<Integer, ArrayList<TimePeriod>> workTimeMap = new HashMap<>();
    private Voter voter;

    XMLHandler(){
        voterCounts = new HashMap<>();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        try {

            if (qName.equals("voter") && voter == null) {
                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthDay);

            } else if (qName.equals("visit") && voter != null) {

                int count = voterCounts.getOrDefault(voter, 0);
                voterCounts.put(voter, count + 1);

                Integer station = Integer.parseInt(attributes.getValue("station"));
                Date time = visitDateFormat.parse(attributes.getValue("time"));

                stationCheckWorkTime(station, time);
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (qName.equals("voter"))
            voter = null;
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
