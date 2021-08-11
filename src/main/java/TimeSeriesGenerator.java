import org.apache.flink.api.java.tuple.Tuple2;
import org.locationtech.jts.geom.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeSeriesGenerator {

    private final String topicName;
    private final String bootStrapServers;
    private final Boolean isAsync;

    public TimeSeriesGenerator(String topicName, String bootStrapServers, Boolean isAsync){
        this.topicName = topicName;
        this.bootStrapServers = bootStrapServers;
        this.isAsync = isAsync;
    }

    // Point Series Generators
    public void random2DPoints(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat){

        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {

                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
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


    public void gaussian2DPoints(int nRows, int minObjID, int maxObjID, Double minVariance, Double maxVariance, Envelope env, String dateFormat){

        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
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

    //LineString Stream Generators
    public void randomLineStrings(int nRows, int minObjID, int maxObjID, int minLineStringSegments, int maxLineStringSegments, Envelope env, String dateFormat){
        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {
                int nLineSegments = (int)HelperClass.getRandomDoubleInRange(minLineStringSegments, maxLineStringSegments);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                List<Coordinate> lineStringCoord = HelperClass.generateRandomLineString(nLineSegments, env);
                assert lineStringCoord != null;

                //GeometryFactory geofact = new GeometryFactory();
                //LineString ls = geofact.createLineString(lineStringCoord.toArray(new Coordinate[0]));
                //System.out.println(ls);

                String line = HelperClass.generateLineStringJson(lineStringCoord, objID, dateTimeString).toString();

                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Polygon Stream Generators
    public void randomPolygons(int nRows, int minObjID, int maxObjID, int minPolygonSides, int maxPolygonSides, Envelope env, String dateFormat){
        KafkaProducer kafkaProducer = new KafkaProducer(topicName, bootStrapServers, isAsync);
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {
                int nPolygonSides = (int)HelperClass.getRandomDoubleInRange(minPolygonSides, maxPolygonSides);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                List<Coordinate> polygonCoord = HelperClass.generateRandomPolygon(nPolygonSides, env);
                List<List<Coordinate>> nestedPolygon = new ArrayList<>();
                nestedPolygon.add(polygonCoord);

                /*
                GeometryFactory geofact = new GeometryFactory();
                Polygon poly = geofact.createPolygon(nestedPolygon.get(0).toArray(new Coordinate[0]));
                Point p = poly.getCentroid();
                System.out.println(poly);
                System.out.println(p);
                 */

                String line = HelperClass.generatePolygonJson(nestedPolygon, objID, dateTimeString).toString();

                lineCount++;
                System.out.println(line);
                //kafkaProducer.sendMessage(lineCount + "", line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
