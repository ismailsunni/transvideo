package example;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Created with IntelliJ IDEA.
 * User: ismailsunni
 * Date: 26/11/13
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public class VideoInfo {

    private static final String filename = "/home/ismailsunni/Downloads/a.mp4";

    public static void main(String[] args){
        // Create a Xuggler container object
        IContainer container = IContainer.make();
        System.out.println(filename);
        // Attempt to open the container
        int result = container.open(filename, IContainer.Type.READ, null);

        // Check if the operation was successful
        if (result < 0){
            throw new RuntimeException("Failed to open media file");
        }

        // Query how many streams the call to open found
        int numStreams = container.getNumStreams();

        // Query how for the total duration
        long duration = container.getDuration();

        // Query for the file size
        long fileSize = container.getFileSize();

        // Query for the bit rate
        long bitRate = container.getBitRate();

        System.out.println("Number of streams: " + numStreams);
        System.out.println("Duration (ms): " + duration);
        System.out.println("File Size (bytes): " + fileSize);
        System.out.println("Bit Rate: " + bitRate);

        // Iterate trough the streams to print their meta data
        for (int i=0; i<numStreams-1; i++){

            // Find the stream object
            IStream stream = container.getStream(i);

            // Get the pre-configured decoder that can decode this stream
            IStreamCoder coder = stream.getStreamCoder();

            System.out.println("*** Start of Stream Info ***");

            System.out.printf("stream %d: ", i);
            System.out.printf("type: %s; ", coder.getCodecType());
            System.out.printf("codec: %s; ", coder.getCodecID());
            System.out.printf("duration: %s; ", stream.getDuration());
            System.out.printf("start time: %s; ", container.getStartTime());
            System.out.printf("timebase: %d/%d; ",
                    stream.getTimeBase().getNumerator(),
                    stream.getTimeBase().getDenominator());
            System.out.printf("coder tb: %d/%d; ",
                    coder.getTimeBase().getNumerator(),
                    coder.getTimeBase().getDenominator());
            System.out.println();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                System.out.printf("sample rate: %d; ", coder.getSampleRate());
                System.out.printf("channels: %d; ", coder.getChannels());
                System.out.printf("format: %s", coder.getSampleFormat());
            }
            else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                System.out.printf("width: %d; ", coder.getWidth());
                System.out.printf("height: %d; ", coder.getHeight());
                System.out.printf("format: %s; ", coder.getPixelType());
                System.out.printf("frame-rate: %5.2f; ", coder.getFrameRate().getDouble());
            }

            System.out.println();
            System.out.println("*** End of Stream Info ***");
        }

    }
}
