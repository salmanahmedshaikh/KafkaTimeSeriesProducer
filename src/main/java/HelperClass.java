import jdk.internal.util.xml.impl.Pair;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelperClass {

    //Generate Random Objects
    public static Coordinate generateRandomXYTuple(Envelope env){

        double x = getRandomDoubleInRange(env.getMinX(), env.getMaxX());
        double y = getRandomDoubleInRange(env.getMinY(), env.getMaxY());

        return new Coordinate(x,y);
    }

    public static Coordinate  generateRandomGaussianXYTuple(Coordinate c, Envelope env, double minVariance, double maxVariance){

        Random fRandom = new Random();
        double x;
        double y;

        // Generating x and y gaussian random variable within given variance range
        do {
            x = c.x + fRandom.nextGaussian() * getRandomDoubleInRange(minVariance, maxVariance);
        } while(!withinRange(x, env.getMinX(), env.getMaxX()));

        do {
            y = c.y + fRandom.nextGaussian() * getRandomDoubleInRange(minVariance, maxVariance);
        } while(!withinRange(y, env.getMinY(), env.getMaxY()));

        return new Coordinate(x, y);
    }

    public static double getRandomDoubleInRange(double minRange, double maxRange){
        return minRange + Math.random() * (maxRange - minRange);
    }

    public static boolean withinRange(double val, double minRange, double maxRange){
        return (val >= minRange && val <= maxRange);
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

}
