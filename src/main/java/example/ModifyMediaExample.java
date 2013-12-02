/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaTool;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;
import javax.imageio.ImageIO;

/**
 *
 * @author ismailsunni
 */
public class ModifyMediaExample {
    private static final String inputFilename = "/home/ismailsunni/Downloads/a.mp4";
    private static final String outputFilename = "/home/ismailsunni/Downloads/ab.mp4";
    private static final String imageFilename = "/home/ismailsunni/Downloads/PRETZ.png";
    
    public static void main(String[] args){
        // Create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        
        // Configure it to generate BufferImages
        mediaReader.setBufferedImageTypeToGenerate(
                BufferedImage.TYPE_3BYTE_BGR);
        
        IMediaWriter mediaWriter = 
                ToolFactory.makeWriter(outputFilename, mediaReader);
        
        IMediaTool imageMediaTool = new StaticImageMediaTool(imageFilename);
        IMediaTool audioVolumeMediaTool = new VolumeAdjustMediaTool(0.1);
        
        // Create a tool chain
        // Reader -> addStaticImage -> reduceVolume -> writer
        mediaReader.addListener(imageMediaTool);
        imageMediaTool.addListener(audioVolumeMediaTool);
        audioVolumeMediaTool.addListener(mediaWriter);
        
        int i = 0;
        while (mediaReader.readPacket() == null){
            i += 1;
            System.out.println(i);
        }
        
    }

    private static class StaticImageMediaTool extends MediaToolAdapter {

        private BufferedImage logoImage;
        
        public StaticImageMediaTool(String imageFilename) {
            try {
                logoImage = ImageIO.read(new File(imageFilename));
            } catch (IOException e) {
                throw  new RuntimeException("Could not open file");
            }
        }
        
        @Override
        public void  onVideoPicture(IVideoPictureEvent event){
            BufferedImage image = event.getImage();
            
            // Get the graphics for the image
            Graphics2D g = image.createGraphics();
            
            Rectangle2D bounds = new Rectangle2D.Float(0, 0, logoImage.getWidth(), logoImage.getHeight());
            
            // Compute the ammount to insert the time stamp and translate 
            // the image to that position
            double inset = bounds.getHeight();
            g.translate(inset, event.getImage().getHeight() - inset);
            
            g.setColor(Color.WHITE);
            g.fill(bounds);
            g.setColor(Color.BLACK);
            g.drawImage(logoImage, 0, 0, null);
            
            // Call parent which will pass the video onto next tool in chain
            super.onVideoPicture(event);
        }
    }

    private static class VolumeAdjustMediaTool extends MediaToolAdapter {

        // The amount to adjust the volume by
        private double mVolume;
        
        public VolumeAdjustMediaTool(double volume) {
            mVolume = volume;
        }
        
        @Override
        public void  onAudioSamples(IAudioSamplesEvent event){
            // Get the raw audio bytes and adjust it's value
            ShortBuffer buffer=
                    event.getAudioSamples().getByteBuffer().asShortBuffer();
            for (int i = 0; i < buffer.limit(); ++i){
                buffer.put(i, (short)(buffer.get(i) * mVolume));
            }
            
            // Cal parent which will call pass the audio onto next tool in chain
            super.onAudioSamples(event);
        }
    }
    
}
