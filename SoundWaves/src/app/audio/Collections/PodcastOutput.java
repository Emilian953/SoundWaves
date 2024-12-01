package app.audio.Collections;

import app.audio.Files.Episode;

import java.util.ArrayList;
import lombok.Getter;

@Getter
public class PodcastOutput {
    private final String name;
    private final ArrayList<String> episodes;

    public PodcastOutput(Podcast podcast) {
        this.name = podcast.getName();
        this.episodes = new ArrayList<>();

        for (int i = 0; i < podcast.getNumberOfTracks(); i++) {
            episodes.add(podcast.getEpisodes().get(i).getName());
        }
    }
}
