/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 *
 * @author ismailsunni
 */
public class TranscodingExample {
    
    private static final String inputFilename = "/home/ismailsunni/Downloads/a.mp4";
    private static final String outputFilename = "/home/ismailsunni/Downloads/a.flv";
    
    public static void main(String[] args){
        // Create a media reader
        IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);
        
        // Create a media writer
        IMediaWriter mediaWriter = 
                ToolFactory.makeWriter(outputFilename, mediaReader);
        
        // Add a writer to the reader, to create the output file
        mediaReader.addListener(mediaWriter);
        
        // Create a media viewer with stats enabled
        IMediaViewer mediaViewer = ToolFactory.makeViewer(true);
        
        // Add viewer to the reader, to see the decoded media
        mediaReader.addListener(mediaViewer);
        
        // Read and decode packet from the source file and
        // dispatch decoded audio and video to the writer
        while (mediaReader.readPacket() == null);
    }
    
}
