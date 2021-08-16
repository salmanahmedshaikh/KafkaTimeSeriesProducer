package StreamGenerator.timeSeriesGenerators;

import StreamGenerator.geometryGenerator.GeometryGenerator;
import StreamGenerator.geometryGenerator.PolygonGenerator;
import StreamGenerator.kafka.KafkaProducer;
import StreamGenerator.utils.HelperClass;
import org.locationtech.jts.geom.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PolygonStream extends TimeSeriesGenerator{

    private final int minSegments;
    private final int maxSegments;
    private final double minSegmentLengthVar;
    private final double maxSegmentLengthVar;
    private final int minPolygonHoles;
    private final int maxPolygonHoles;

    //SEGMENT_LENGTH_VARIANCE

    public PolygonStream(String topicName, String bootStrapServers, Boolean isAsync) {
        super(topicName, bootStrapServers, isAsync);

        this.minSegments = NUM_OF_SEGMENTS;
        this.maxSegments = NUM_OF_SEGMENTS;
        this.minSegmentLengthVar = SEGMENT_LENGTH_VARIANCE;
        this.maxSegmentLengthVar = SEGMENT_LENGTH_VARIANCE;
        this.minPolygonHoles = NUM_OF_POLYGON_HOLES;
        this.maxPolygonHoles = NUM_OF_POLYGON_HOLES;
    }

    public PolygonStream(String topicName, String bootStrapServers, Boolean isAsync, int minPolygonSides, int maxPolygonSides) {
        super(topicName, bootStrapServers, isAsync);

        this.minSegments = minPolygonSides;
        this.maxSegments = maxPolygonSides;
        this.minSegmentLengthVar = SEGMENT_LENGTH_VARIANCE;
        this.maxSegmentLengthVar = SEGMENT_LENGTH_VARIANCE;
        this.minPolygonHoles = NUM_OF_POLYGON_HOLES;
        this.maxPolygonHoles = NUM_OF_POLYGON_HOLES;
    }

    public PolygonStream(String topicName, String bootStrapServers, Boolean isAsync, int minPolygonSides, int maxPolygonSides, double minPolygonSegmentLengthVar, double maxPolygonSegmentLengthVar) {
        super(topicName, bootStrapServers, isAsync);

        this.minSegments = minPolygonSides;
        this.maxSegments = maxPolygonSides;
        this.minSegmentLengthVar = minPolygonSegmentLengthVar;
        this.maxSegmentLengthVar = maxPolygonSegmentLengthVar;
        this.minPolygonHoles = NUM_OF_POLYGON_HOLES;
        this.maxPolygonHoles = NUM_OF_POLYGON_HOLES;
    }

    @Override
    //public void randomPolygons(int nRows, int minObjID, int maxObjID, int minPolygonSides, int maxPolygonSides, Envelope env, String dateFormat)
    public void random(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat) {

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int rowCount = 0;

            while (rowCount < nRows) {
                int nPolygonSides = (int) HelperClass.getRandomDoubleInRange(minSegments, maxSegments);
                int nPolygonHoles = (int) HelperClass.getRandomDoubleInRange(minPolygonHoles, maxPolygonHoles);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());

                //List<Coordinate> polygonCoord = HelperClass.generateRandomPolygon(nPolygonSides, seriesBBox);
                //List<List<Coordinate>> nestedPolygon = new ArrayList<>();
                //nestedPolygon.add(polygonCoord);

                Geometry geometry = generatePolygon(nPolygonSides, nPolygonHoles, HelperClass.getRandomBBoxInRange(seriesBBox));

                String row = "";
                //String row = HelperClass.generatePolygonJson(geometry, objID, dateTimeString).toString();

                rowCount++;
                System.out.println(row);
                //kafkaProducer.sendMessage(lineCount + "", row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void gaussian(int nRows, int minObjID, int maxObjID, Envelope seriesBBox, String dateFormat, double minSeriesVar, double maxSeriesVar){

        HashMap<Integer, Coordinate> lastObjIDCentroidCoordinateMap = new HashMap();

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
            int rowCount = 0;

            while (rowCount < nRows) {

                int nPolygonSides = (int) HelperClass.getRandomDoubleInRange(minSegments, maxSegments);
                int nPolygonHoles = (int) HelperClass.getRandomDoubleInRange(minPolygonHoles, maxPolygonHoles);
                int objID = (int)HelperClass.getRandomDoubleInRange(minObjID, maxObjID);
                String dateTimeString = dateTimeFormat.format(new Date());
                //List<List<Coordinate>> nestedPolygon = new ArrayList<>();
                Coordinate lastCentroid = lastObjIDCentroidCoordinateMap.get(objID);

                if(lastCentroid != null) { // if point with given objID exist, generate a new point based on it
                    //List<Coordinate> polygonCoord = HelperClass.generateRandomGaussianPolygon(lastCentroid, nPolygonSides, seriesBBox, minSegmentLengthVar, maxSegmentLengthVar, minSeriesVar, maxSeriesVar);
                    //nestedPolygon.add(polygonCoord);
                    Geometry geometry = generatePolygon(nPolygonSides, nPolygonHoles, HelperClass.getRandomGaussianBBoxInRange(lastCentroid, seriesBBox, minSeriesVar, maxSeriesVar, minSegmentLengthVar, maxSegmentLengthVar));
                    //GeometryFactory geofact = new GeometryFactory();
                    //Polygon poly = geofact.createPolygon(nestedPolygon.get(0).toArray(new Coordinate[0]));
                    lastObjIDCentroidCoordinateMap.put(objID, geometry.getCentroid().getCoordinate());
                }
                else {
                    //List<Coordinate> polygonCoord = HelperClass.generateRandomPolygon(nPolygonSides, seriesBBox, minSegmentLengthVar, maxSegmentLengthVar);
                    //nestedPolygon.add(polygonCoord);
                    Geometry geometry = generatePolygon(nPolygonSides, nPolygonHoles, HelperClass.getRandomBBoxInRange(seriesBBox));
                    //GeometryFactory geofact = new GeometryFactory();
                    //Polygon poly = geofact.createPolygon(nestedPolygon.get(0).toArray(new Coordinate[0]));
                    lastObjIDCentroidCoordinateMap.put(objID, geometry.getCentroid().getCoordinate());
                }

                String row = "";
                //String row = HelperClass.generatePolygonJson(nestedPolygon, objID, dateTimeString).toString();

                rowCount++;
                System.out.println(row);
                System.out.println(rowCount);
                //kaProducer.sendMessage(lineCount + "", row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Geometry generatePolygon(int nPoints, int nHoles, Envelope bBox){

        GeometryFactory geometryFactory = new GeometryFactory();
        PolygonGenerator polygonGenerator = GeometryGenerator.createPolygonGenerator();
        polygonGenerator.setGeometryFactory(geometryFactory);
        polygonGenerator.setBoundingBox(bBox);
        polygonGenerator.setNumberPoints(nPoints);
        polygonGenerator.setNumberHoles(nHoles);
        polygonGenerator.setGenerationAlgorithm(1); // ARC Polygon Generation Algorithm
        return polygonGenerator.create();
    }
}
