package app.audio.Collections;

import app.utils.Enums;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class AlbumOutput {
    private final String name;
    private final ArrayList<String> songs;

    public AlbumOutput(Album album) {
        this.name = album.getName();
        this.songs = new ArrayList<>();

        for (int i = 0; i < album.getSongs().size(); i++) {
            songs.add(album.getSongs().get(i).getName());
        }
    }
}
