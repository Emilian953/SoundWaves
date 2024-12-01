package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import java.util.ArrayList;
import java.util.List;

public final class Podcast extends AudioCollection {
    private final List<Episode> episodes;

    public Podcast(String name, String owner, List<Episode> episodes) {
        super(name, owner);
        this.episodes = episodes;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public  boolean hasTheSameEpisode() {
        for (int i = 0; i < episodes.size(); i++) {
            for (int j = 0; j < episodes.size(); j++) {
                if (episodes.get(i).getName().equals(episodes.get(j).getName()) && i != j) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getNumberOfTracks() {
        return episodes.size();
    }

    @Override
    public AudioFile getTrackByIndex(int index) {
        return episodes.get(index);
    }
}
