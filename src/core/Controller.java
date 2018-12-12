package core;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TableView table;

    @FXML
    private Slider seekBar;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView prevButton;

    @FXML
    private ImageView nextButton;

    @FXML
    private ImageView playButton;

    @FXML
    private Label volumeLabel;

    @FXML
    private Label currentTime;

    @FXML
    private Label totalTime;

    @FXML
    private ImageView volumeButton;

    @FXML
    private TableColumn<Song, String> albumColumn;

    @FXML
    private TableColumn<Song, String> artistColumn;

    @FXML
    private TableColumn<Song, String> titleColumn;

    @FXML
    private TableColumn<Song, String> timeColumn;

    @FXML
    private TableColumn<Song, String> trackColumn;

    @FXML
    private TableColumn<Song, String> genreColumn;

    @FXML
    private TableColumn<Song, String> yearColumn;

    @FXML
    private ImageView songImage;

    @FXML
    private Label albumLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private ToggleButton repeatButton;

    private int nextSong;
    private ArrayList<MediaPlayer> playlist;
    private MediaView mediaView;

    public Controller() {
        nextSong = 0;
        playlist = new ArrayList<MediaPlayer>();
        mediaView = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("album"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("time"));
        trackColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("track"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("year"));

        table.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
            @Override
            public TableRow<Song> call(TableView<Song> tableView) {
                return new TableRow<Song>() {
                    @Override
                    protected void updateItem(Song item, boolean empty) {
                        super.updateItem(item, empty);

                        try {
                            if (item == table.getItems().get(nextSong - 1)) {
                                setStyle("-fx-font-weight: bold");
                            } else {
                                setStyle("");
                            }
                        } catch (Exception e) {
                            setStyle("");
                        }
                    }
                };
            }
        });
    }

    public void fileChooser(ActionEvent actionEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("/home/eliezer"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles == null) {
            return;
        }

        table.getItems().clear(); // clear table
        playlist.clear(); // clear playlist

        for (File f : selectedFiles) {
            // add song to playlist
            playlist.add(createMediaPlayer(f.toURI().toString()));

            // add song to table
            Mp3File mp3 = new Mp3File(f.getAbsolutePath());
            ID3v2 tag = mp3.getId3v2Tag();
            Song song = new Song(tag.getAlbum(), tag.getArtist(), tag.getTitle(), formatTime(mp3.getLengthInSeconds()), tag.getTrack(), tag.getGenreDescription(), tag.getYear());
            table.getItems().add(song);
        }

        // highlight first row
        selectRow(0);

        // start new playlist
        nextSong = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startAnew();
            }
        });
    }

    public void folderChooser(ActionEvent actionEvent) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("/home/eliezer"));
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            return;
        }

        File[] files = selectedDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".mp3")) {
                    return true;
                }

                return false;
            }
        });

        table.getItems().clear(); // clear table
        playlist.clear(); // clear playlist

        for (File f : files) {
            // add song to playlist
            playlist.add(createMediaPlayer(f.toURI().toString()));

            // add song to table
            Mp3File mp3 = new Mp3File(f.getAbsolutePath());
            ID3v2 tag = mp3.getId3v2Tag();
            Song song = new Song(tag.getAlbum(), tag.getArtist(), tag.getTitle(), formatTime(mp3.getLengthInSeconds()), tag.getTrack(), tag.getGenreDescription(), tag.getYear());
            table.getItems().add(song);
        }

        // highlight first row
        selectRow(0);

        // start new playlist
        nextSong = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startAnew();
            }
        });
    }

    private String formatTime(long sec) {
        long min = sec / 60;
        long remSec = sec - min * 60;

        String time = "";

        if (min < 10) {
            time += '0';
        }

        time += Long.toString(min) + ':';

        if (remSec < 10) {
            time += '0';
        }

        time += Long.toString(remSec);

        return time;
    }

    private MediaPlayer createMediaPlayer(String url) {
        return new MediaPlayer(new Media(url));
    }

    private void startPlayingSong() {
        mediaView.getMediaPlayer().play();
        mediaView.getMediaPlayer().setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                if (nextSong == playlist.size()) {
                    if (repeatButton.isSelected()) {
                        nextSong = 0;
                    } else {
                        return;
                    }
                }

                selectRow(nextSong);
                startAnew();
            }
        });
    }

    private void playPauseSong() {
        if (mediaView == null) {
            return;
        }

        if (mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PAUSED) {
            mediaView.getMediaPlayer().play();
        } else {
            mediaView.getMediaPlayer().pause();
        }
    }

    public void playButtonClickHandler(MouseEvent mouseEvent) {
        int selectedRow = getSelectedRow();

        if (nextSong != selectedRow + 1) {
            selectRow(selectedRow);
            nextSong = selectedRow;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    startAnew();
                }
            });
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    playPauseSong();
                }
            });
        }
    }

    private void selectRow(final int row) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                table.requestFocus();
                table.getSelectionModel().select(row);
                table.scrollTo(row);
                table.refresh();
            }
        });
    }

    public void tableClickHandler(MouseEvent mouseEvent) {
        int selectedRow = table.getSelectionModel().getSelectedIndex();

        if (selectedRow == -1) {
            return;
        }

        if (mouseEvent.getClickCount() == 2) {
            selectRow(selectedRow);
            nextSong = selectedRow;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    startAnew();
                }
            });
        }
    }

    public void volumeSliderHandler(MouseEvent mouseEvent) {
        Double val = Math.floor(volumeSlider.getValue());
        volumeLabel.setText(Integer.toString(val.intValue()));

        if (mediaView == null) {
            return;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
            }
        });
    }

    private void setSongImage(byte[] imageData) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
        songImage.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    private void stopCurrentSong() {
        if (mediaView == null) {
            return;
        }

        mediaView.getMediaPlayer().stop();
    }

    private void setNextSong() {
        mediaView = new MediaView(playlist.get(nextSong++));
    }

    private int getSelectedRow() {
        return table.getSelectionModel().getFocusedIndex();
    }

    private void startAnew() {
        stopCurrentSong();
        setNextSong();
        startPlayingSong();
    }

    public void prevButtonClickHandler(MouseEvent mouseEvent) {
        if (mediaView == null) {
            return;
        }

        if (nextSong <= 1) {
            return;
        }

        nextSong -= 2; // backpropagate
        selectRow(nextSong);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startAnew();
            }
        });
    }

    public void nextButtonClickHandler(MouseEvent mouseEvent) {
        if (mediaView == null) {
            return;
        }

        if (nextSong == playlist.size()) {
            return;
        }

        selectRow(nextSong);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startAnew();
            }
        });
    }
}
