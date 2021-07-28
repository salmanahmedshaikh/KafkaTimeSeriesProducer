public class KafkaTimeSeriesProducer extends Thread {

    //private static final String topicName = "allDetections_rep1_par8";
    //public static final String folderName = "/home/ubuntu/data/prediction_stream/";
    //public static final String folderName = "/mnt/NFS/gpfs1/projects/g-nedo-dprt/salman/data/prediction_stream";
    //private static final String topicName = "test";
    //public static final String folderName = "/home/salman/Data/prediction_stream/";

    public static void main(String [] args){

        // Check how many arguments were passed in
        if(args.length < 12)
        {
            System.out.println("Proper Usage is: java -jar jarFileName queryOption(random, gaussian) topicName bootstrapServers(localhost:9092 OR 172.16.0.97:9092,172.16.0.56:9092) " +
                    "numRows DataBoundary (minX minY maxX maxY) maxObjIDInt dateTimeFormat variance");
            System.exit(0);
        }

        String queryOption = args[0];
        String topicName = args[1];
        String bootStrapServers = args[2];
        Boolean isAsync = Boolean.parseBoolean(args[3]);
        Integer numRows = Integer.parseInt(args[4]);
        Double minX = Double.parseDouble(args[5]);
        Double minY = Double.parseDouble(args[6]);
        Double maxX = Double.parseDouble(args[7]);
        Double maxY = Double.parseDouble(args[8]);
        Integer maxObjID = Integer.parseInt(args[9]);
        String dateTimeFormat = args[10];
        Double variance = Double.parseDouble(args[11]);
        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long startTime = System.nanoTime();

        TimeSeriesGenerator timeSeriesGenerator = new TimeSeriesGenerator(topicName, bootStrapServers, isAsync);

        switch(queryOption) {
            case "random": {
                timeSeriesGenerator.generate2DRandom(numRows, maxObjID, minX, minY, maxX, maxY, dateTimeFormat);
                break;
            }
            case "gaussian": {
                timeSeriesGenerator.generate2DGaussian(numRows, maxObjID, variance, minX, minY, maxX, maxY, dateTimeFormat);
                break;
            }
        }

        long endTime = System.nanoTime();
        // get difference of two nanoTime values
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
    }
}


