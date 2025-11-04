import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Song {
    private String title;
    private String artist;
    private String length;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath) {
        this.filePath = filePath;
        try{
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            length = convertSongLengthFormat();
            //we use jaudiotagger library to create an audiofile obj to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            //now we read through the meta data of the audio file
            Tag tag = audioFile.getTag();
            if (tag != null) {
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
            }else{
                title = "N/A";
                artist = "N/A";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String convertSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        return formattedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public void setMp3File(Mp3File mp3File) {
        this.mp3File = mp3File;
    }

    public double getFrameRatePerMilliseconds() {
        return frameRatePerMilliseconds;
    }

    public void setFrameRatePerMilliseconds(double frameRatePerMilliseconds) {
        this.frameRatePerMilliseconds = frameRatePerMilliseconds;
    }
}
