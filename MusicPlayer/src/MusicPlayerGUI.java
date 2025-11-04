import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.PINK;
    public static final Color Text_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    //making the file explorer in our app
    private JFileChooser fileChooser;

    private JLabel songTitle;
    private JLabel songArtist;
    private JPanel playbacksBtns;
    private JSlider playbackSlider;

    public MusicPlayerGUI() {
        //JFrame constructor
        super("Music Player");

        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //app will be at the center of the screen and can't be resized
        setLocationRelativeTo(null);
        setResizable(false);

        //making the layout null in order to control the (x, y) coordinates
        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);

        fileChooser = new JFileChooser();

        //set a default path for file explorer
        fileChooser.setCurrentDirectory(new File("src/assets"));

        //filter file chooser to only see mp3 files
        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));


        addGUIComponents();
    }

    private void addGUIComponents() {
        addToolBar();

        //load the record image
        JLabel songImage = new JLabel(loadImage("src/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        //song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setForeground(Text_COLOR);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        //song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setForeground(Text_COLOR);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        //playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                //when the user is holding the tick, we want to pause the song
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e){
                //when the user drops the tick
                JSlider source = (JSlider)e.getSource();

                //get the frame from where the user wants to playback to
                int frame = source.getValue();

                //update the current frame in the music player to this frame
                musicPlayer.setCurrentFrame(frame);

                //update current time in millis as well
                musicPlayer.setCurrentTimeInMillis((int)(frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                //resume the song
                musicPlayer.playCurrentSong();

                //toggle on pause button and toggle off play button
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        //playback buttons
        addPlaybackBtns();
    }

    private void addToolBar() {
        JToolBar toolBar = new JToolBar();

        //preventing the toolbar from being moved
        toolBar.setFloatable(false);
        toolBar.setBounds(0, 0, getWidth(), 20);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(e -> {
            //an interger is returned to us to let us know what the user did
            int result = fileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = fileChooser.getSelectedFile();

            //here we also check to see if the user pressed the "open" button
            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                //create a song obj based on the selected song
                Song song = new Song(selectedFile.getPath());

                //load song in music player
                musicPlayer.loadSong(song);

                //update song title + artist
                updateSongTitleAndArtist(song);

                //update playback slider
                updatePlaybacksSlider(song);

                //toggle on pause button and toggle off play button
                enablePauseButtonDisablePlayButton();

            }
        });
        songMenu.add(loadSong);

        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        //adding items to the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //load music playlist dialog
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    //stop the music
                    musicPlayer.stopSong();

                    //load playlist
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }


    private void addPlaybackBtns() {
        playbacksBtns = new JPanel();
        playbacksBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbacksBtns.setBackground(null);

        //previous
        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to the previous song
                musicPlayer.prevSong();
            }
        });
        playbacksBtns.add(prevButton);

        //play button
        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //toggle off play button and toggle on pause button
                enablePauseButtonDisablePlayButton();

                //play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbacksBtns.add(playButton);

        //pause button
        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e){
               //toggle off pause button and toggle on play button
               enablePlayButtonDisablePauseButton();
               musicPlayer.pauseSong();
           }
        });
        playbacksBtns.add(pauseButton);

        //nextButton
        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to the next song
                musicPlayer.nextSong();
            }
        });
        playbacksBtns.add(nextButton);

        add(playbacksBtns);
    }

    //will be used to update our slider from the music player class
    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    public void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getTitle());
        songArtist.setText(song.getArtist());
    }

    public void updatePlaybacksSlider(Song song){
        //update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        //create song lenght label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        //beginning will be 00:00
        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(Text_COLOR);

        //end will vary dependinf on the song
        JLabel labelEnding = new JLabel(song.getLength());
        labelEnding.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnding.setForeground(Text_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnding);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    public void enablePauseButtonDisablePlayButton() {
        //retrive reference to play button from playbacksBtns panel
        JButton playButton = (JButton) playbacksBtns.getComponent(1);
        JButton pauseButton = (JButton) playbacksBtns.getComponent(2);

        //turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        //turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton() {
        //retrive reference to play button from playbacksBtns panel
        JButton playButton = (JButton) playbacksBtns.getComponent(1);
        JButton pauseButton = (JButton) playbacksBtns.getComponent(2);

        //turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        //turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }
    private ImageIcon loadImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
