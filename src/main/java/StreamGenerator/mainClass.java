/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package StreamGenerator;

import StreamGenerator.parameters.Params;
import StreamGenerator.timeSeriesGenerators.TimeSeriesGenerator;
import org.locationtech.jts.geom.Envelope;

public class mainClass extends Thread {

    //private static final String topicName = "allDetections_rep1_par8";
    //public static final String folderName = "/home/ubuntu/data/prediction_stream/";
    //public static final String folderName = "/mnt/NFS/gpfs1/projects/g-nedo-dprt/salman/data/prediction_stream";
    //private static final String topicName = "test";
    //public static final String folderName = "/home/salman/Data/prediction_stream/";

    public static void main(String [] args){

        Params params = new Params();
        System.out.println(params);

        String queryOption = params.option;
        String topicName = params.topicName;
        String bootStrapServers = params.bootStrapServers;
        Boolean isAsync = params.asyncLoading;
        int numRows = params.nRows;
        double minX = params.bBox.get(0);
        double minY = params.bBox.get(1);
        double maxX = params.bBox.get(2);
        double maxY = params.bBox.get(3);
        Envelope env = new Envelope(minX, maxX, minY, maxY);
        int minObjID = params.objIDRange.get(0);
        int maxObjID = params.objIDRange.get(1);
        String dateTimeFormat = params.dateFormat;
        double minVariance = params.varianceRange.get(0);
        double maxVariance = params.varianceRange.get(1);
        int minPolygonSides = params.nSidesRange.get(0);
        int maxPolygonSides = params.nSidesRange.get(1);
        int minLineStringSegments = params.nLineSegmentsRange.get(0);
        int maxLineStringSegments = params.nLineSegmentsRange.get(1);

        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.nanoTime();

        TimeSeriesGenerator timeSeriesGenerator = new TimeSeriesGenerator(topicName, bootStrapServers, isAsync);

        switch(queryOption) {
            case "randomPoints": {
                timeSeriesGenerator.random2DPoints(numRows, minObjID, maxObjID, env, dateTimeFormat);
                break;
            }
            case "gaussianPoints": {
                timeSeriesGenerator.gaussian2DPoints(numRows, minObjID, maxObjID, minVariance, maxVariance, env, dateTimeFormat);
                break;
            }
            case "randomLineStrings": {
                timeSeriesGenerator.randomLineStrings(numRows, minObjID, maxObjID, minLineStringSegments, maxLineStringSegments, env, dateTimeFormat);
                break;
            }
            case "randomPolygons": {
                timeSeriesGenerator.randomPolygons(numRows, minObjID, maxObjID, minPolygonSides, maxPolygonSides, env, dateTimeFormat);
                break;
            }
            default:
                System.out.println("Unrecognized query option. Please input the appropriate query option.");


        }

        long endTime = System.nanoTime();
        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
    }
}


