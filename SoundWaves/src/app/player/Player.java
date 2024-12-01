package app.player;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.user.User;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private Enums.RepeatMode repeatMode;
    private boolean shuffle;
    private boolean paused;
    private boolean realPause;
    private PlayerSource source;
    @Getter
    private String type;
    @Getter
    private ArrayList<PodcastBookmark> bookmarks = new ArrayList<>();
    private ArrayList<Song> playedSongsData;
    private ArrayList<Episode> playedEpisodes;
    private ArrayList<String> playedEpisodesString;
    private ArrayList<Song> playedWhilePremium;
    private ArrayList<Song> playedUntilAd;
    private User user;


    public Player() {
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.paused = true;
        this.realPause = true;
    }

    public void stop() {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        repeatMode = Enums.RepeatMode.NO_REPEAT;
        paused = true;
        realPause = true;
        source = null;
        shuffle = false;
    }

    private void bookmarkPodcast() {
        if (source != null && source.getAudioFile() != null) {
            PodcastBookmark currentBookmark = new PodcastBookmark(source.getAudioCollection().getName(), source.getIndex(), source.getDuration());
            bookmarks.removeIf(bookmark -> bookmark.getName().equals(currentBookmark.getName()));
            bookmarks.add(currentBookmark);
        }
    }

    public static PlayerSource createSource(String type, LibraryEntry entry, List<PodcastBookmark> bookmarks) {
        if ("song".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.LIBRARY, (AudioFile) entry);
        } else if ("playlist".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.PLAYLIST, (AudioCollection) entry);
        } else if ("podcast".equals(type)) {
            return createPodcastSource((AudioCollection) entry, bookmarks);
        } else if ("album".equals(type)) {
            return new PlayerSource(Enums.PlayerSourceType.PLAYLIST, (AudioCollection) entry);
        }

        return null;
    }

    private static PlayerSource createPodcastSource(AudioCollection collection, List<PodcastBookmark> bookmarks) {
        for (PodcastBookmark bookmark : bookmarks) {
            if (bookmark.getName().equals(collection.getName())) {
                return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection, bookmark);
            }
        }
        return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection);
    }

    public void setSource(LibraryEntry entry, String type, ArrayList<Song> playedSongsData,
                          ArrayList<Episode> playedEpisodes, ArrayList<Song> playedWhilePremium, User user,
                          ArrayList<Song> playedUntilAd, ArrayList<String> playedEpisodesString) {

        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        this.type = type;
        this.source = createSource(type, entry, bookmarks);
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.shuffle = false;
        this.paused = true;
        this.playedSongsData = playedSongsData;
        this.playedEpisodes = playedEpisodes;
        this.playedWhilePremium = playedWhilePremium;
        this.user = user;
        this.playedUntilAd = playedUntilAd;
        this.playedEpisodesString = playedEpisodesString;

        if (type.equals("playlist") && this.source != null) {
            this.playedSongsData.add(Admin.getSongByName(this.source.getAudioFile().getName()));
        }
    }

    public void pause() {
        paused = !paused;
        realPause = paused;
    }

    public void artificialPause() {paused = !paused;}

    public void shuffle (Integer seed) {
        if (seed != null) {
            source.generateShuffleOrder(seed);
        }

        if (source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            shuffle = !shuffle;
            if (shuffle) {
                source.updateShuffleIndex();
            }
        }
    }

    public Enums.RepeatMode repeat() {
        if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
            if (source.getType() == Enums.PlayerSourceType.LIBRARY) {
                repeatMode = Enums.RepeatMode.REPEAT_ONCE;
            } else {
                repeatMode = Enums.RepeatMode.REPEAT_ALL;
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
                repeatMode = Enums.RepeatMode.REPEAT_INFINITE;
            } else {
                if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                    repeatMode = Enums.RepeatMode.REPEAT_CURRENT_SONG;
                } else {
                    repeatMode = Enums.RepeatMode.NO_REPEAT;
                }
            }
        }

        return repeatMode;
    }

    public void simulatePlayer(int time) {
        if (!paused && source!= null) {
            while (time >= source.getDuration()) {
                time -= source.getDuration();
                next();
                if (paused) {
                    break;
                }
            }
            if (!paused) {
                source.skip(-time);
            }
        }
    }

    public void next() {
        paused = source.setNextAudioFile(repeatMode, shuffle);
        realPause = paused;
        if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
            repeatMode = Enums.RepeatMode.NO_REPEAT;
        }

        if (source.getDuration() == 0 && paused) {
            stop();
        } else {
            if ("playlist".equals(type)) {
                playedSongsData.add(Admin.getSongByName(source.getAudioFile().getName()));
            } else if ("podcast".equals(type)) {
                playedEpisodesString.add(source.getAudioFile().getName());
                playedEpisodes.add(Admin.getEpisodeByName(source.getAudioFile().getName()));
            } else if ("album".equals(type)) {
                if (user.getType().equals("premium")) {
                    playedWhilePremium.add(Admin.getSongByNameAndCollection(source.getAudioFile().getName(), source.getAudioCollection().getName()));
                } else {
                    playedUntilAd.add(Admin.getSongByNameAndCollection(source.getAudioFile().getName(), source.getAudioCollection().getName()));
                }
                playedSongsData.add(Admin.getSongByNameAndCollection(source.getAudioFile().getName(), source.getAudioCollection().getName()));
            }
        }
    }

    public void prev() {
        source.setPrevAudioFile(shuffle);
        paused = false;
        realPause = paused;
    }

    private void skip(int duration) {
        source.skip(duration);
        paused = false;
        realPause = paused;
    }

    public void skipNext() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(-90);
        }
    }

    public void skipPrev() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(90);
        }
    }

    public AudioFile getCurrentAudioFile() {
        if (source == null)
            return null;
        return source.getAudioFile();
    }

    public boolean getPaused() {
        return paused;
    }

    public boolean getShuffle() {
        return shuffle;
    }

    public PlayerStats getStats() {
        String filename = "";
        int duration = 0;
        if (source != null && source.getAudioFile() != null) {
            filename = source.getAudioFile().getName();
            duration = source.getDuration();
        } else {
            stop();
        }

        return new PlayerStats(filename, duration, repeatMode, shuffle, realPause);
    }
}
