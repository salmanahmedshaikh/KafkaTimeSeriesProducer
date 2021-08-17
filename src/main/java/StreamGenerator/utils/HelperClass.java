package StreamGenerator.utils;

//import jdk.internal.util.xml.impl.Pair;
import org.json.JSONObject;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.shape.GeometricShapeBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HelperClass {

    //Generate Random XY coordinate
    public static Coordinate generateRandomXYTuple(Envelope env){

        double x = getRandomDoubleInRange(env.getMinX(), env.getMaxX());
        double y = getRandomDoubleInRange(env.getMinY(), env.getMaxY());

        return new Coordinate(x,y);
    }

    //Generate Random XY coordinate based on past coordinate
    public static Coordinate  generateRandomGaussianXYTuple(Coordinate pastCoordinate, Envelope env, double minVariance, double maxVariance){

        Random fRandom = new Random();
        double x;
        double y;

        // Generating x and y gaussian random variable within given variance range
        do {
            x = pastCoordinate.x + fRandom.nextGaussian() * getRandomDoubleInRange(minVariance, maxVariance);
        } while(!withinRange(x, env.getMinX(), env.getMaxX()));

        do {
            y = pastCoordinate.y + fRandom.nextGaussian() * getRandomDoubleInRange(minVariance, maxVariance);
        } while(!withinRange(y, env.getMinY(), env.getMaxY()));

        return new Coordinate(x, y);
    }

    public static Envelope getRandomBBoxInRange(Envelope env){
        double x1 = getRandomDoubleInRange(env.getMinX(), env.getMaxX());
        double y1 = getRandomDoubleInRange(env.getMinY(), env.getMaxY());
        double x2 = getRandomDoubleInRange(env.getMinX(), env.getMaxX());
        double y2 = getRandomDoubleInRange(env.getMinY(), env.getMaxY());

        double minX, maxX, minY, maxY;

        if(x1 > x2){
            maxX = x1;
            minX = x2;
        }else{
            maxX = x2;
            minX = x1;
        }

        if(y1 > y2){
            maxY = y1;
            minY = y2;
        }else{
            maxY = y2;
            minY = y1;
        }

        return new Envelope(minX, maxX, minY, maxY);
    }

    public static Envelope getRandomGaussianBBoxInRange(Coordinate pastCoordinate, Envelope env, double minSeriesVar, double maxSeriesVar, double minBBoxDiagonal, double maxBBoxDiagonal){
        Coordinate c1 = generateRandomGaussianXYTuple(pastCoordinate, env, minSeriesVar, maxSeriesVar);
        double x1 = c1.getX();
        double y1 = c1.getY();
        double bBoxDiagonal = getRandomDoubleInRange(minBBoxDiagonal, maxBBoxDiagonal);
        double x2 = getRandomDoubleInRange(x1, (env.getMaxX() - env.getMinX())*Math.sqrt(2));
        double y2 = getRandomDoubleInRange(y1, (env.getMaxY() - env.getMinY())*Math.sqrt(2));

        double minX, maxX, minY, maxY;

        if(x1 > x2){
            maxX = x1;
            minX = x2;
        }else{
            maxX = x2;
            minX = x1;
        }

        if(y1 > y2){
            maxY = y1;
            minY = y2;
        }else{
            maxY = y2;
            minY = y1;
        }

        return new Envelope(minX, maxX, minY, maxY);
    }

    public static double getRandomDoubleInRange(double minRange, double maxRange){
        return minRange + Math.random() * (maxRange - minRange);
    }

    public static boolean withinRange(double val, double minRange, double maxRange){
        return (val >= minRange && val <= maxRange);
    }

    // Generates completely random linestring within given boundary
    public static List<Coordinate> generateRandomLineString(int numLineSegments, Envelope env){

        if(numLineSegments <= 0)
            return null;

        List<Coordinate> lineStringCoordinates = new ArrayList<>();
        for (int i = 0; i <= numLineSegments; i++) {
            lineStringCoordinates.add(generateRandomXYTuple(env));
        }

        return lineStringCoordinates;
    }

    // Generates random linestring with linesegments' length bounded by min and max variance
    public static List<Coordinate> generateRandomLineString(int numLineSegments, Envelope env, double minLSLengthVar, double maxLSLengthVar){

        if(numLineSegments <= 0)
            return null;

        List<Coordinate> lineStringCoordinates = new ArrayList<>();
        // adding first random coordinate
        lineStringCoordinates.add(generateRandomXYTuple(env));

        // next coordinates are based on min and max line segment length variance
        for (int i = 1; i <= numLineSegments; i++) {
            Coordinate pastCoord = lineStringCoordinates.get(i-1);
            lineStringCoordinates.add(generateRandomGaussianXYTuple(pastCoord, env, minLSLengthVar, maxLSLengthVar));
        }

        return lineStringCoordinates;
    }

    // Generates random linestring with linesegments' length bounded by min and max variance and based on past lineString centroid coordinate
    public static List<Coordinate> generateRandomGaussianLineString(Coordinate pastCentroid, int numLineSegments, Envelope env, double minLSLengthVar, double maxLSLengthVar, double minSeriesVar, double maxSeriesVar){

        if(numLineSegments <= 0)
            return null;

        List<Coordinate> lineStringCoordinates = new ArrayList<>();
        // adding first random Gaussian coordinate based on pastCentroid and min and max Series Variance
        lineStringCoordinates.add(generateRandomGaussianXYTuple(pastCentroid, env, minSeriesVar, maxSeriesVar));

        // next coordinates are based on min and max line segment length variance and pastCoordinate
        for (int i = 1; i <= numLineSegments; i++) {
            Coordinate pastCoord = lineStringCoordinates.get(i-1);
            lineStringCoordinates.add(generateRandomGaussianXYTuple(pastCoord, env, minLSLengthVar, maxLSLengthVar));
        }

        return lineStringCoordinates;
    }

    // Generates completely random polygon within given boundary
    public static List<Coordinate> generateRandomPolygon(int numSides, Envelope env){

        if(numSides <= 0)
            return null;

        List<Coordinate> polyCoordinates = generateRandomLineString(numSides, env);
        assert polyCoordinates != null;
        polyCoordinates.remove(numSides);
        polyCoordinates.add(polyCoordinates.get(0));

        return polyCoordinates;
    }

    // Generates random polygon with sides' length bounded by min and max variance
    public static List<Coordinate> generateRandomPolygon(int numSides, Envelope env, double minSideLengthVar, double maxSideLengthVar){

        if(numSides <= 0)
            return null;

        List<Coordinate> polyCoordinates = generateRandomLineString(numSides, env, minSideLengthVar, maxSideLengthVar);
        assert polyCoordinates != null;
        polyCoordinates.remove(numSides);
        polyCoordinates.add(polyCoordinates.get(0));

        return polyCoordinates;
    }

    // Generates random polygon with sides' length bounded by min and max variance and based on past polygon centroid coordinate
    public static List<Coordinate> generateRandomGaussianPolygon(Coordinate pastCentroid, int numSides, Envelope env, double minSideLengthVar, double maxSideLengthVar, double minSeriesVar, double maxSeriesVar){

        if(numSides <= 0)
            return null;

        List<Coordinate> polyCoordinates = generateRandomGaussianLineString(pastCentroid, numSides, env, minSideLengthVar, maxSideLengthVar, minSeriesVar, maxSeriesVar);
        assert polyCoordinates != null;
        polyCoordinates.remove(numSides);
        polyCoordinates.add(polyCoordinates.get(0));

        return polyCoordinates;
    }

    // Generate JSON Objects
    public static JSONObject generatePointJson(double x, double y, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        double[] coordinate = {x, y};
        jsonGeometry.put("coordinates", coordinate);
        jsonGeometry.put("type", "Point");
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    public static JSONObject generatePolygonJson(List<List<Coordinate>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<double[]>> jsonCoordinate = new ArrayList<List<double[]>>();
        for (List<Coordinate> polygonCoordinates : nestedCoordinateList) {
            List<double[]> coordinates = new ArrayList<double[]>();
            for (Coordinate c : polygonCoordinates) {
                double[] coordinate = {c.x, c.y};
                coordinates.add(coordinate);
            }
            jsonCoordinate.add(coordinates);
        }
        jsonGeometry.put("type", "Polygon");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    public static JSONObject generateMultiPolygonJson(List<List<List<Coordinate>>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<List<double[]>>> jsonCoordinate = new ArrayList<List<List<double[]>>>();
        for (List<List<Coordinate>> listCoordinate : nestedCoordinateList) {
            List<List<double[]>> coordinates = new ArrayList<>();
            for (List<Coordinate> l : listCoordinate) {
                List<double[]> arrCoordinate = new ArrayList<double[]>();
                for (Coordinate c : l) {
                    double[] coordinate = {c.x, c.y};
                    arrCoordinate.add(coordinate);
                }
                coordinates.add(arrCoordinate);
            }
            jsonCoordinate.add(coordinates);
        }
        jsonGeometry.put("type", "MultiPolygon");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    public static JSONObject generateLineStringJson(List<Coordinate> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<double[]> jsonCoordinate = new ArrayList<double[]>();
        for (Coordinate c : nestedCoordinateList) {
            double[] coordinate = {c.x, c.y};
            jsonCoordinate.add(coordinate);
        }
        jsonGeometry.put("type", "LineString");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }

    public static JSONObject generateMultiLineStringJson(List<List<Coordinate>> nestedCoordinateList, int objID, String dateTimeString) {
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        List<List<double[]>> jsonCoordinate = new ArrayList<>();
        for (List<Coordinate> l : nestedCoordinateList) {
            List<double[]> arrCoordinate = new ArrayList<>();
            for (Coordinate c : l) {
                double[] coordinate = {c.x, c.y};
                arrCoordinate.add(coordinate);
            }
            jsonCoordinate.add(arrCoordinate);
        }
        jsonGeometry.put("type", "MultiLineString");
        jsonGeometry.put("coordinates", jsonCoordinate);
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }
    public static JSONObject generateGeometryJson(Geometry geometry, int objID, String dateTimeString){
        JSONObject jsonObj = new JSONObject();

        JSONObject jsonGeometry = new JSONObject();
        if (geometry.getGeometryType() == "Point") {
            Point point = (Point)geometry;
            double[] coordinate = {point.getX(), point.getY()};
            jsonGeometry.put("type", "Point");
            jsonGeometry.put("coordinates", coordinate);
        }
        else if (geometry.getGeometryType() == "MultiPoint") {
            MultiPoint multiPoint = (MultiPoint)geometry;
            Coordinate[] multiPointCoordinates = multiPoint.getCoordinates();
            List<double[]> jsonCoordinate = new ArrayList<>();
            for (Coordinate c : multiPointCoordinates) {
                double[] coordinate = {c.x, c.y};
                jsonCoordinate.add(coordinate);
            }
            jsonGeometry.put("type", "MultiPoint");
            jsonGeometry.put("coordinates", jsonCoordinate);
        }
        else if (geometry.getGeometryType() == "Polygon"){
            Polygon polygon = (Polygon)geometry;
            List<List<double[]>> jsonCoordinate = new ArrayList<>();

            List<List<Coordinate>> listlistCoordinates = new ArrayList();
            listlistCoordinates.add(new ArrayList<>(Arrays.asList(polygon.getExteriorRing().getCoordinates())));
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                listlistCoordinates.add(new ArrayList<>(Arrays.asList(polygon.getInteriorRingN(i).getCoordinates())));
            }

            for (List<Coordinate> polygonCoordinates : listlistCoordinates) {
                List<double[]> coordinates = new ArrayList<>();
                for (Coordinate c : polygonCoordinates) {
                    double[] coordinate = {c.x, c.y};
                    coordinates.add(coordinate);
                }
                jsonCoordinate.add(coordinates);
            }
            jsonGeometry.put("type", "Polygon");
            jsonGeometry.put("coordinates", jsonCoordinate);
        }
        else if (geometry.getGeometryType() == "MultiPolygon") {
            MultiPolygon multiPolygon = (MultiPolygon)geometry;
            List<List<List<double[]>>> jsonCoordinate = new ArrayList<>();
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Polygon polygon = (Polygon)multiPolygon.getGeometryN(i);

                List<List<Coordinate>> listlistCoordinates = new ArrayList();
                listlistCoordinates.add(new ArrayList<>(Arrays.asList(polygon.getExteriorRing().getCoordinates())));
                for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                    listlistCoordinates.add(new ArrayList<>(Arrays.asList(polygon.getInteriorRingN(j).getCoordinates())));
                }

                List<List<double[]>> coordinates = new ArrayList<>();
                for (List<Coordinate> l : listlistCoordinates) {
                    List<double[]> arrCoordinate = new ArrayList<>();
                    for (Coordinate c : l) {
                        double[] coordinate = {c.x, c.y};
                        arrCoordinate.add(coordinate);
                    }
                    coordinates.add(arrCoordinate);
                }
                jsonCoordinate.add(coordinates);
            }
            jsonGeometry.put("type", "MultiPolygon");
            jsonGeometry.put("coordinates", jsonCoordinate);
        }
        else if(geometry.getGeometryType() == "LineString"){
            LineString lineString = (LineString)geometry;
            Coordinate[] lineStringCoordinates = lineString.getCoordinates();
            List<double[]> jsonCoordinate = new ArrayList<>();
            for (Coordinate c : lineStringCoordinates) {
                double[] coordinate = {c.x, c.y};
                jsonCoordinate.add(coordinate);
            }
            jsonGeometry.put("type", "LineString");
            jsonGeometry.put("coordinates", jsonCoordinate);
        }
        else if(geometry.getGeometryType() == "MultiLineString") {
            MultiLineString multiLineString = (MultiLineString)geometry;
            List<List<double[]>> jsonCoordinate = new ArrayList<>();
            for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
                LineString lineString = (LineString)multiLineString.getGeometryN(i);
                Coordinate[] lineStringCoordinates = lineString.getCoordinates();
                List<double[]> listCoordinate = new ArrayList<>();
                for (Coordinate c : lineStringCoordinates) {
                    double[] coordinate = {c.x, c.y};
                    listCoordinate.add(coordinate);
                }
                jsonCoordinate.add(listCoordinate);
            }
            jsonGeometry.put("type", "MultiLineString");
            jsonGeometry.put("coordinates", jsonCoordinate);
        }
        jsonObj.put("geometry", jsonGeometry);

        JSONObject jsonpProperties = new JSONObject();
        jsonpProperties.put("oID", String.valueOf(objID));
        jsonpProperties.put("timestamp", dateTimeString);
        jsonObj.put("properties", jsonpProperties);

        jsonObj.put("type", "Feature");
        return jsonObj;
    }
}
