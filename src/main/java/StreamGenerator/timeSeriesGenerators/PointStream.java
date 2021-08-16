package StreamGenerator.timeSeriesGenerators;

import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.timeSeriesGenerators.TimeSeriesGenerator;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PointStream extends TimeSeriesGenerator {

    public PointStream(String topicName, String bootStrapServers, Boolean isAsync) {
        super(topicName, bootStrapServers, isAsync);
    }

    @Override
    public void random(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int rowCount = 0;

            while (rowCount < nRows) {

                int objID = (int) HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                Coordinate XYRandomTuple = HelperClass.generateRandomXYTuple(seriesBBox);
                String row = HelperClass.generatePointJson(XYRandomTuple.x, XYRandomTuple.y, objID, dateTimeString).toString();

                rowCount++;
                System.out.println(row);
                //kafkaProducer.sendMessage(lineCount + "", row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void gaussian(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat, double minSeriesVar, double maxSeriesVar){

        //KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
        //Pair<Double, Double> point = new Pair<Double, Double>();
        HashMap<Integer, Coordinate> lastObjIDCoordinateMap = new HashMap();

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int rowCount = 0;
            Coordinate c;

            while (rowCount < nRows) {

                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());
                //org.javatuples.Pair<Double, Double> point = objIDMap.get(objID);
                Coordinate lastPoint = lastObjIDCoordinateMap.get(objID);

                if(lastPoint != null) { // if point with given objID exist, generate a new point based on it
                    c = HelperClass.generateRandomGaussianXYTuple(lastPoint, seriesBBox, minSeriesVar, maxSeriesVar);
                    lastObjIDCoordinateMap.put(objID, c);
                }
                else {
                    c = HelperClass.generateRandomXYTuple(seriesBBox);
                    lastObjIDCoordinateMap.put(objID, c);
                }
                String row = HelperClass.generatePointJson(c.x, c.y, objID, dateTimeString).toString();

                rowCount++;
                System.out.println(row);
                //kafkaProducer.sendMessage(lineCount + "", row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
