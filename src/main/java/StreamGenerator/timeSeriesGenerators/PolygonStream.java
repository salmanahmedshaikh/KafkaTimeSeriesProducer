package StreamGenerator.timeSeriesGenerators;

import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PolygonStream extends TimeSeriesGenerator{

    private final int minPolygonSides;
    private final int maxPolygonSides;


    public PolygonStream(String topicName, String bootStrapServers, Boolean isAsync, int minPolygonSides, int maxPolygonSides) {
        super(topicName, bootStrapServers, isAsync);

        this.minPolygonSides = minPolygonSides;
        this.maxPolygonSides = maxPolygonSides;
    }

    @Override
    //public void randomPolygons(int nRows, int minObjID, int maxObjID, int minPolygonSides, int maxPolygonSides, Envelope env, String dateFormat)
    public void random(int nRows, int minObjID, int maxObjID, Envelope env, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int lineCount = 0;

            while (lineCount < nRows) {
                int nPolygonSides = (int) HelperClass.getRandomDoubleInRange(minPolygonSides, maxPolygonSides);
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

    public void gaussian(int nRows, int minObjID, int maxObjID, Double minVariance, Double maxVariance, Envelope env, String dateFormat){
    }
}
