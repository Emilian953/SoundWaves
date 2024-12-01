package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.utils.Enums;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Album extends AudioCollection {
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs;

    public Album(String name, Integer releaseYear, String description, ArrayList<Song> songs, String owner) {
        super(name, owner);
        this.name = name;
        this.songs = songs;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    public boolean songAlreadyExists() {
        for (int i = 0; i < songs.size(); i++) {
            for (int j = 0; j < songs.size(); j++) {
                if (i != j && songs.get(i).getName().equals(songs.get(j).getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    @Override
    public AudioFile getTrackByIndex(int index) {
        return songs.get(index);
    }
}
