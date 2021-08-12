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
    public void random(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {

                int objID = (int) HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                Coordinate XYRandomTuple = HelperClass.generateRandomXYTuple(env);
                String line = HelperClass.generatePointJson(XYRandomTuple.x, XYRandomTuple.y, objID, dateTimeString).toString();

                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void gaussian(int nRows, int minObjID, int maxObjID, Double minVariance, Double maxVariance, Envelope env, String dateFormat){

        //KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
        //Pair<Double, Double> point = new Pair<Double, Double>();
        HashMap<Integer, Coordinate> lastObjIDCoordinateMap = new HashMap();

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;
            Coordinate c;

            while (lineCount < nRows) {

                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());
                //org.javatuples.Pair<Double, Double> point = objIDMap.get(objID);
                Coordinate lastPoint = lastObjIDCoordinateMap.get(objID);

                if(lastPoint != null) { // if point with given objID exist, generate a new point based on it
                    c = HelperClass.generateRandomGaussianXYTuple(lastPoint, env, minVariance, maxVariance);
                    lastObjIDCoordinateMap.put(objID, c);
                }
                else {
                    c = HelperClass.generateRandomXYTuple(env);
                    lastObjIDCoordinateMap.put(objID, c);
                }
                String line = HelperClass.generatePointJson(c.x, c.y, objID, dateTimeString).toString();

                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
