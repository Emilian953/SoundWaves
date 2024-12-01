package app.user;

import app.Admin;
import app.audio.Collections.*;
import app.audio.Files.*;
import app.audio.LibraryEntry;
import app.itemsPage.Announcement;
import app.itemsPage.Event;
import app.itemsPage.Merch;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class User {
    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private final Player player;
    @Getter
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Getter
    private boolean online;
    @Getter
    private String type;
    @Getter
    private String currentPage = "HomePage";
    @Getter
    private String lastViewedPerson;
    @Getter
    private ArrayList<String> playedPodcasts = new ArrayList<>();
    @Getter
    private ArrayList<Song> playedSongsData = new ArrayList<>();
    @Getter
    private ArrayList<Episode> playedEpisodesData = new ArrayList<>();
    @Getter
    private ArrayList<String> playedEpisodesString = new ArrayList<>();
    @Getter
    private boolean onPageRN = false;
    private Map<String, Integer> artistCounts = new HashMap<>();
    private Map<String, Integer> genreCounts = new HashMap<>();
    private Map<String, Integer> songCounts = new HashMap<>();
    private Map<String, Integer> albumCounts = new HashMap<>();
    private Map<String, Integer> episodeCounts = new HashMap<>();
    @Getter
    private ArrayList<Song> playedWhilePremium = new ArrayList<>();
    @Getter @Setter
    private ArrayList<Song> playedUntilAd = new ArrayList<>();
    private ArrayList<Artist> artistsSubscribed = new ArrayList<>();
    private ArrayList<Host> hostsSubscribed = new ArrayList<>();
    private ObjectMapper notificationsObjectMapper = new ObjectMapper();
    private List<ObjectNode> notificationList = new ArrayList<>();
    private ArrayList<String> acquiredMerch = new ArrayList<>();
    private ArrayList<String> playlistsRecommendations = new ArrayList<>();
    private ArrayList<String> songRecommendations = new ArrayList<>();
    private String lastRecommendationType;
    private ArrayList<String> viewedPageHistory = new ArrayList<>();
    private ArrayList<String> possibleNextPages = new ArrayList<>();
    private ArrayList<String> viewedHostsArtists = new ArrayList<>();
    private ArrayList<String> possibleNexrHostsArtists = new ArrayList<>();

    public User(String username, int age, String city, String type) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.type = type;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        online = true;
        viewedPageHistory.add("HomePage");
        viewedHostsArtists.add("nobody");
    }

    public ArrayList<String> search(Filters filters, String type) {

        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();
        if (!this.isOnline()) return results;
        List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

        for (LibraryEntry libraryEntry : libraryEntries) {
            results.add(libraryEntry.getName());
        }
        return results;
    }

    public String select(int itemNumber) {
        if (!lastSearched)
            return "Please conduct a search before making a selection.";

        lastSearched = false;

        LibraryEntry selected = searchBar.select(itemNumber);

        if (selected == null)
            return "The selected ID is too high.";

        if (searchBar.getLastSearchType().equals("artist")) {
            this.currentPage = "ArtistPage";
            this.lastViewedPerson = searchBar.getLastSelected().getName();
            onPageRN = true;
            viewedHostsArtists.add(selected.getName());
            return "Successfully selected %s's page.".formatted(selected.getName());
        }

        if (searchBar.getLastSearchType().equals("host")) {
            this.currentPage = "HostPage";
            this.lastViewedPerson = searchBar.getLastSelected().getName();
            onPageRN = true;
            viewedHostsArtists.add(selected.getName());
            return "Successfully selected %s's page.".formatted(selected.getName());
        }

        return "Successfully selected %s.".formatted(selected.getName());
    }

    public String load() {
        if (searchBar.getLastSelected() == null)
            return "Please select a source before attempting to load.";

        if (!searchBar.getLastSearchType().equals("song") && ((AudioCollection)searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType(),
                playedSongsData, playedEpisodesData, playedWhilePremium, this, playedUntilAd, playedEpisodesString);

        if (searchBar.getLastSearchType() != null && searchBar.getLastSelected() != null) {

            if (searchBar.getLastSearchType().equals("podcast")) {
                playedPodcasts.add(searchBar.getLastSelected().getName());
                Podcast playingPod = Admin.getPodcastByName(searchBar.getLastSelected().getName());
                if (playingPod != null && playingPod.getEpisodes() != null) {
                    playedEpisodesData.add(playingPod.getEpisodes().get(0));
                    playedEpisodesString.add(playingPod.getEpisodes().get(0).getName());
                }
            } else if (searchBar.getLastSearchType().equals("song")) {
                Song playedSong = Admin.getSongByName(searchBar.getLastSelected().getName());
                if (this.type.equals("premium")) {
                    playedWhilePremium.add(playedSong);
                } else {
                    playedUntilAd.add(playedSong);
                }
                playedSongsData.add(playedSong);
            } else if (searchBar.getLastSearchType().equals("album")) {
                Album playedAlbum = Admin.getAlbumByName(searchBar.getLastSelected().getName());
                if (playedAlbum != null && playedAlbum.getSongs() != null) {
                    if (this.type.equals("premium")) {
                        playedWhilePremium.add(playedAlbum.getSongs().get(0));
                    } else {
                        playedUntilAd.add(playedAlbum.getSongs().get(0));
                    }
                    playedSongsData.add(playedAlbum.getSongs().get(0));
                }
            }
        }

        searchBar.clearSelection();

        player.pause();

        return "Playback loaded successfully.";
    }

    public String playPause() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before attempting to pause or resume playback.";

        player.pause();

        if (player.getPaused())
            return "Playback paused successfully.";
        else
            return "Playback resumed successfully.";
    }

    public String repeat() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before setting the repeat status.";

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch(repeatMode) {
            case NO_REPEAT -> repeatStatus = "no repeat";
            case REPEAT_ONCE -> repeatStatus = "repeat once";
            case REPEAT_ALL -> repeatStatus = "repeat all";
            case REPEAT_INFINITE -> repeatStatus = "repeat infinite";
            case REPEAT_CURRENT_SONG -> repeatStatus = "repeat current song";
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    public String shuffle(Integer seed) {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before using the shuffle function.";

        if (!player.getType().equals("playlist") && !player.getType().equals("album"))
            return "The loaded source is not a playlist or an album.";

        player.shuffle(seed);

        if (player.getShuffle())
            return "Shuffle function activated successfully.";
        return "Shuffle function deactivated successfully.";
    }

    public String forward() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before attempting to forward.";

        if (!player.getType().equals("podcast"))
            return "The loaded source is not a podcast.";

        player.skipNext();

        return "Skipped forward successfully.";
    }

    public String backward() {
        if (player.getCurrentAudioFile() == null)
            return "Please select a source before rewinding.";

        if (!player.getType().equals("podcast"))
            return "The loaded source is not a podcast.";

        player.skipPrev();

        return "Rewound successfully.";
    }

    public String like() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before liking or unliking.";

        if (!player.getType().equals("song") && !player.getType().equals("playlist") && !player.getType().equals("album"))
            return "Loaded source is not a song.";

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    public String next() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before skipping to the next track.";

        player.next();

        if (player.getCurrentAudioFile() == null)
            return "Please load a source before skipping to the next track.";

        return "Skipped to next track successfully. The current track is %s.".formatted(player.getCurrentAudioFile().getName());
    }

    public String prev() {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before returning to the previous track.";

        player.prev();

        return "Returned to previous track successfully. The current track is %s.".formatted(player.getCurrentAudioFile().getName());
    }

    public String createPlaylist(String name, int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name)))
            return "A playlist with the same name already exists.";

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    public String addRemoveInPlaylist(int Id) {
        if (player.getCurrentAudioFile() == null)
            return "Please load a source before adding to or removing from the playlist.";

        if (player.getType().equals("podcast"))
            return "The loaded source is not a song.";

        if (Id > playlists.size())
            return "The specified playlist does not exist.";

        Playlist playlist = playlists.get(Id - 1);

        if (playlist.containsSong((Song)player.getCurrentAudioFile())) {
            playlist.removeSong((Song)player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song)player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    public String switchPlaylistVisibility(Integer playlistId) {
        if (playlistId > playlists.size())
            return "The specified playlist ID is too high.";

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    public String follow() {
        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null)
            return "Please select a source before following or unfollowing.";

        if (!type.equals("playlist"))
            return "The selected source is not a playlist.";

        Playlist playlist = (Playlist)selection;

        if (playlist.getOwner().equals(username))
            return "You cannot follow or unfollow your own playlist.";

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    public String switchConnectionStatus(User user, String username) {
        if (user.isOnline()) {
            user.online = false;
        } else {
            user.online = true;
        }
        player.artificialPause();

        return username + " has changed status successfully.";
    }

    public ArrayList<String> top5Songs4User() {
        ArrayList<Song> sortedSongs = new ArrayList<>(likedSongs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        ArrayList<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= 5) break;
            if(song.getLikes() > 0) topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    public List<String> topPlaylists() {
        List<String> topPlaylists = new ArrayList<>();
        List<Playlist> likedPlaylists = new ArrayList<>(followedPlaylists);

        for (int i = 0; i < 5; i++) {
            int maxLikePod = -1;
            Playlist toBeAdded = null;
            for (Playlist iterPod : likedPlaylists) {
                int sum = 0;
                for (Song iterSong : iterPod.getSongs()) {
                    sum += iterSong.getLikes();
                }

                if (sum > maxLikePod) {
                    toBeAdded = iterPod;
                    maxLikePod = sum;
                }
            }

            if (toBeAdded != null) {
                topPlaylists.add(toBeAdded.getName());
                likedPlaylists.remove(toBeAdded);
            }
        }
        return topPlaylists;
    }

    public String printCurrentPage(CommandInput commandInput) {

        if (this.getCurrentPage().equals("HomePage")) {

            List<String> bestSongs = top5Songs4User();
            List<String> playlists = topPlaylists();



            return "Liked songs:" + "\n\t" + bestSongs + "\n\nFollowed playlists:\n\t" + playlists +
                    "\n\nSong recommendations:\n\t" + songRecommendations + "\n\nPlaylists recommendations:\n\t"
                    + playlistsRecommendations;

        } else if (this.getCurrentPage().equals("ArtistPage")) {



                ArrayList<String> albums = new ArrayList<>(Objects.requireNonNull(Admin.getArtistAlbums(lastViewedPerson)));

                ArrayList<Merch> merchandise = new ArrayList<>(Objects.requireNonNull(Admin.getArtistMerch(lastViewedPerson)));

                ArrayList<Event> events = new ArrayList<>(Objects.requireNonNull(Admin.getArtistEvents(lastViewedPerson)));

                StringBuilder result = new StringBuilder();
                result.append("Albums:\n\t");
                result.append(albums);
                result.append("\n\nMerch:\n\t[");

                int counter = 0;

                for (Merch iterMerch : merchandise) {
                    counter ++;
                    result.append(iterMerch.getName());
                    result.append(" - ");
                    result.append(iterMerch.getPrice());
                    result.append(":\n\t");
                    result.append(iterMerch.getDescription());

                    if (counter < merchandise.size()) {
                        result.append(", ");
                    } else {
                        result.append("]");
                    }
                }

                if (merchandise.isEmpty()) {
                    result.append("]");
                }

                result.append("\n\nEvents:\n\t[");

                if (events.isEmpty()) {
                    result.append("]");
                }

                counter = 0;

                for (Event iterEvents : events) {
                    counter ++;
                    result.append(iterEvents.getName());
                    result.append(" - ");
                    result.append(iterEvents.getDate());
                    result.append(":\n\t");
                    result.append(iterEvents.getDescription());

                    if (counter < events.size()) {
                        result.append(", ");
                    } else {
                        result.append("]");
                    }
                }

                return result.toString();


        } else if (currentPage.equals("HostPage")) {
            if (lastViewedPerson != null) {
                StringBuilder output = new StringBuilder("Podcasts:\n\t[");
                ArrayList<Podcast> podcasts = new ArrayList<>(Admin.allHostPodcasts(lastViewedPerson));
                ArrayList<Announcement> announcements = new ArrayList<>(Admin.getAllAnnouncementsByOwner(lastViewedPerson));

                int counter = 0;

                for (Podcast podcast : podcasts) {
                    output.append(podcast.getName()).append(":\n\t[");
                    for (int i = 0; i < podcast.getEpisodes().size(); i++) {
                        Episode episode = podcast.getEpisodes().get(i);
                        output.append(episode.getName()).append(" - ").append(episode.getDescription());
                        if (i < podcast.getEpisodes().size() - 1) {
                            output.append(", ");
                        }
                    }
                    output.append("]\n");
                    counter ++;
                    if (counter < podcasts.size()) {
                        output.append(", ");
                    }
                }

                output.append("]\n");

                output.append("\nAnnouncements:\n");
                if (announcements.isEmpty()) {
                    output.append("\t[");
                }

                for (Announcement announcement : announcements) {
                    output.append("\t[").append(announcement.getName()).append(":\n\t")
                            .append(announcement.getDescription()).append("\n");
                }

                output.append("]");

                return output.toString();
            }
        } else if (currentPage.equals("LikedContent")) {
            StringBuilder result = new StringBuilder();
            result.append("Liked songs:\n\t[");
            List<String> likedSongNames = likedSongs.stream()
                    .map(song -> song.getName() + " - " + song.getArtist())
                    .collect(Collectors.toList());
            result.append(String.join(", ", likedSongNames));

            result.append("]");
            result.append("\n\nFollowed playlists:\n\t[");
            List<String> followedPlaylistNames = followedPlaylists.stream()
                    .map(playlist -> playlist.getName() + " - " + playlist.getOwner())
                    .collect(Collectors.toList());
            result.append(String.join(", ", followedPlaylistNames));
            result.append("]");
            return result.toString();
        }

        return null;
    }

    public void toOffline() {
        this.online = false;
    }

    public String addPodcast(String podcastName, ArrayList<EpisodeInput> episodeInputs) {
        if (!type.equals("host")) {
            return username + " is not a host.";
        } else {
            if (Admin.hisPodcastsExists(this.username, podcastName)) {
                return username + " has another podcast with the same name.";
            } else {
                ArrayList<Episode> episodes = Admin.convertToEpisode(episodeInputs);
                Podcast newPod = new Podcast(podcastName, this.getUsername(), episodes);

                if (newPod.hasTheSameEpisode()) {
                    return username + " has the same episode in this podcast.";
                } else {
                    Admin.addNewPodcast(newPod);
                    return username + " has added new podcast successfully.";
                }
            }
        }
    }

    public String removeAlbum(String albumName) {
        boolean ready = false;
        if (!this.type.equals("artist")) {
            return this.getUsername() + " is not an artist.";
        } else {
            if(!Admin.albumExists(albumName)) {
                return this.getUsername() + " doesn't have an album with the given name.";
            } else {
                Album album = Admin.getAlbumByName(albumName);
                if (album != null) {
                    for (Song iterSong : album.getSongs()) {
                        if (Admin.songInteractedWith(iterSong) || Admin.existsSongInPlaylists(album)) {
                            return this.getUsername() + " can't delete this album.";
                        } else {
                            ready = true;
                        }
                    }

                    if (ready) {
                        Admin.removeSongs(album.getSongs());
                        Admin.removeAlbum(album);
                        return username + " deleted the album successfully.";
                    }
                }
            }
        }

        return null;
    }

    public String changePage(String nextPage) {
        if (nextPage.equals("LikedContent")) {
            if (this.getCurrentPage().equals("LikedContent")) {
                return this.getUsername() + " is trying to access a non-existent page.";
            } else {
                this.currentPage = "LikedContent";
                onPageRN = false;
                viewedPageHistory.add("LikedContent");
                this.possibleNextPages.clear();
                viewedHostsArtists.add("nobody");
                return this.getUsername() + " accessed " + nextPage + " successfully.";
            }
        } else if (nextPage.equals("Home")) {
            if (this.getCurrentPage().equals("Home")) {
                return this.getUsername() + " is trying to access a non-existent page.";
            } else {
                this.currentPage = "HomePage";
                onPageRN = false;
                viewedPageHistory.add("HomePage");
                this.possibleNextPages.clear();
                viewedHostsArtists.add("nobody");
                return this.getUsername() + " accessed " + nextPage + " successfully.";
            }
        } else if (nextPage.equals("Artist")) {
            this.currentPage = "ArtistPage";
            viewedPageHistory.add("ArtistPage");
            Song song = Admin.getSongByName(player.getCurrentAudioFile().getName());
            if (song != null) {
                this.lastViewedPerson = song.getArtist();
                viewedHostsArtists.add(song.getArtist());
                possibleNexrHostsArtists.clear();
            }
            this.possibleNextPages.clear();
            return this.getUsername() + " accessed Artist successfully.";
        } else if (nextPage.equals("Host")) {
            this.currentPage = "HostPage";
            viewedPageHistory.add("HostPage");
            String hostName = Admin.getPodcastByEpisodeName(player.getStats().getName()).getOwner();
            if (hostName != null) {
                this.lastViewedPerson = hostName;
                viewedHostsArtists.add(hostName);
                possibleNexrHostsArtists.clear();
            }
            this.possibleNextPages.clear();
            return this.getUsername() + " accessed Host successfully.";
        }

        return null;
    }

    public String removePodcast(String podcastName) {
        if (!this.type.equals("host")) {
            return username + " is not a host.";
        } else {
            if (!Admin.hasPodcast(podcastName)) {
                return this.getUsername() + " doesn't have a podcast with the given name.";
            } else {
                if (Admin.podcastInteractedWith(podcastName)) {
                    return this.username + " can't delete this podcast.";
                }
                Admin.removeThisPodcast(podcastName);
                return this.getUsername() + " deleted the podcast successfully.";
            }
        }
    }

    public List<String> topListenedArtists() {
        ArrayList<String> playedArtists = new ArrayList<>();

        for (Song iterSong : playedSongsData) {
            playedArtists.add(iterSong.getArtist());
        }

        for (String artist : playedArtists) {
            artistCounts.put(artist, artistCounts.getOrDefault(artist, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedArtistList = artistCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());

        List<String> top5Artists = sortedArtistList.subList(0, Math.min(5, sortedArtistList.size()))
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return top5Artists;
    }

    public List<String> topListenedGenres() {
        ArrayList<String> playedGenres = new ArrayList<>();

        for (Song iterSong : playedSongsData) {
            playedGenres.add(iterSong.getGenre());
        }

        for (String genre : playedGenres) {
            genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedGenreList = genreCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());

        List<String> top5Genres = sortedGenreList.subList(0, Math.min(5, sortedGenreList.size()))
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return top5Genres;
    }

    public List<String> topListenedSongs() {
        ArrayList<String> playedSongsHere = new ArrayList<>();

        for (Song iterSong : playedSongsData) {
            playedSongsHere.add(iterSong.getName());
        }

        for (String song : playedSongsHere) {
            songCounts.put(song, songCounts.getOrDefault(song, 0) + 1);
        }

        List<String> topSongs = songCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int compareByCount = entry2.getValue().compareTo(entry1.getValue());
                    if (compareByCount == 0) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                    return compareByCount;
                })
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return topSongs;
    }

    public List<String> topListenedAlbums() {
        ArrayList<String> playedAlbums = new ArrayList<>();

        for (Song iterSong : playedSongsData) {
            playedAlbums.add(iterSong.getAlbum());
        }

        for (String album : playedAlbums) {
            albumCounts.put(album, albumCounts.getOrDefault(album, 0) + 1);
        }

        List<String> topAlbums = albumCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int compareByCount = entry2.getValue().compareTo(entry1.getValue());
                    if (compareByCount == 0) {
                        return entry1.getKey().compareTo(entry2.getKey()); // Alphabetical sorting when counts are equal
                    }
                    return compareByCount;
                })
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return topAlbums;
    }

    public List<String> topListenedEpisodes() {
        for (String episode : playedEpisodesString) {
            episodeCounts.put(episode, episodeCounts.getOrDefault(episode, 0) + 1);
        }

        List<String> topEpisodes = episodeCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int compareByCount = entry2.getValue().compareTo(entry1.getValue());
                    if (compareByCount == 0) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                    return compareByCount;
                })
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return topEpisodes;
    }

    public ObjectNode wrapped(ObjectMapper objectMapper) {
        ObjectNode resultNode = objectMapper.createObjectNode();

        ObjectNode topArtistsNode = objectMapper.createObjectNode();
        ObjectNode topGenresNode = objectMapper.createObjectNode();
        ObjectNode topSongsNode = objectMapper.createObjectNode();
        ObjectNode topAlbumsNode = objectMapper.createObjectNode();
        ObjectNode topEpisodesNode = objectMapper.createObjectNode();

        List<String> top5Artists = topListenedArtists();
        List<String> top5Genres = topListenedGenres();
        List<String> top5Songs = topListenedSongs();
        List<String> top5Albums = topListenedAlbums();
        List<String> top5Episodes = topListenedEpisodes();

        for (String artist : top5Artists) {
            topArtistsNode.put(artist, artistCounts.get(artist));
        }

        for (String genre : top5Genres) {
            topGenresNode.put(genre, genreCounts.get(genre));
        }

        for (String song : top5Songs) {
            topSongsNode.put(song, songCounts.get(song));
        }

        for (String album : top5Albums) {
            topAlbumsNode.put(album, albumCounts.get(album));
        }

        for (String episode : top5Episodes) {
            topEpisodesNode.put(episode, episodeCounts.get(episode));
        }

        resultNode.set("topArtists", topArtistsNode);
        resultNode.set("topGenres", topGenresNode);
        resultNode.set("topSongs", topSongsNode);
        resultNode.set("topAlbums", topAlbumsNode);
        resultNode.set("topEpisodes", topEpisodesNode);

        return resultNode;
    }

    public String buyPremium() {
        if (this.type.equals("premium")) {
            return this.username + " is already a premium user.";
        } else {
            this.type = "premium";
            return this.username + " bought the subscription successfully.";
        }
    }

    public String cancelPremium(CommandInput commandInput) {
        if (!this.type.equals("premium")) {
            return this.username + " is not a premium user.";
        } else {
            this.type = "standard";

            ArrayList<Artist> listenedArtists = new ArrayList<>();
            for (Song iterSong : playedWhilePremium) {
                listenedArtists.add(Admin.getArtistByName(iterSong.getArtist()));
            }

            int totalSongs = playedWhilePremium.size();

            Map<String, Integer> artistCounts = new HashMap<>();

            for (Artist artist : listenedArtists) {
                String artistName = artist.getName();
                artistCounts.put(artistName, artistCounts.getOrDefault(artistName, 0) + 1);
            }

            Set<String> uniqueArtists = new HashSet<>();
            for (Artist artist : listenedArtists) {
                uniqueArtists.add(artist.getName());
            }

            for (String artist : uniqueArtists) {
                int count = artistCounts.get(artist);

                double songRevenue = (double)((1000000.0 / totalSongs) * count);
                Admin.getArtistByName(artist).updateRevenue(songRevenue);
            }

            double individualRevenueSong = (double)(1000000.0 / totalSongs);
            for (Song iterSong : playedWhilePremium) {
                iterSong.updateRevenue(individualRevenueSong);
                Artist thisArtist = Admin.getArtistByName(iterSong.getArtist());
                if (iterSong.getRevenue() > thisArtist.getMostProfitableSongValue()) {
                    thisArtist.setMostProfitableSongValue(iterSong.getRevenue());
                    thisArtist.setMostProfitableSong(iterSong);
                } else if (iterSong.getRevenue() == thisArtist.getMostProfitableSongValue()) {
                    if (iterSong.getName().compareTo(thisArtist.getMostProfitableSong().getName()) < 0) {
                        thisArtist.setMostProfitableSong(iterSong);
                    }
                }
            }

            return this.username + " cancelled the subscription successfully.";
        }
    }

    public String adBreak(Integer price, CommandInput commandInput) {
        if (this.player.getCurrentAudioFile() == null) {
            return this.getUsername() + " is not playing any music.";
        }

        ArrayList<Artist> listenedArtists = new ArrayList<>();
        for (Song iterSong : playedUntilAd) {
            listenedArtists.add(Admin.getArtistByName(iterSong.getArtist()));
        }

        int totalSongs = playedUntilAd.size();

        Map<String, Integer> artistCounts = new HashMap<>();

        for (Artist artist : listenedArtists) {
            String artistName = artist.getName();
            artistCounts.put(artistName, artistCounts.getOrDefault(artistName, 0) + 1);
        }

        Set<String> uniqueArtists = new HashSet<>();
        for (Artist artist : listenedArtists) {
            uniqueArtists.add(artist.getName());
        }

        for (String artist : uniqueArtists) {
            int count = artistCounts.get(artist);

            double songRevenue = (double)((price / totalSongs) * count);
            Admin.getArtistByName(artist).updateRevenue(songRevenue);
        }

        double individualRevenueSong = (double)(price / totalSongs);
        for (Song iterSong : playedUntilAd) {
            iterSong.updateRevenue(individualRevenueSong);
            Artist thisArtist = Admin.getArtistByName(iterSong.getArtist());
            if (iterSong.getRevenue() > thisArtist.getMostProfitableSongValue()) {
                thisArtist.setMostProfitableSongValue(iterSong.getRevenue());
                thisArtist.setMostProfitableSong(iterSong);
            } else if (iterSong.getRevenue() == thisArtist.getMostProfitableSongValue()) {
                if (iterSong.getName().compareTo(thisArtist.getMostProfitableSong().getName()) < 0) {
                    thisArtist.setMostProfitableSong(iterSong);
                }
            }
        }

        this.playedUntilAd.clear();
        return "Ad inserted successfully.";
    }

    public String subscribe() {
        if (!this.currentPage.equals("ArtistPage") && !this.currentPage.equals("HostPage")) {
            return "To subscribe you need to be on the page of an artist or host.";
        } else if (this.currentPage.equals("ArtistPage")) {
            Artist artist = Admin.getArtistByName(this.lastViewedPerson);
            if (this.artistsSubscribed.contains(artist)) {
                this.artistsSubscribed.remove(artist);
                artist.removeSubscriber(this);
                return this.getUsername() + " unsubscribed from " + this.lastViewedPerson +  " successfully.";
            }
            this.artistsSubscribed.add(artist);
            artist.addSubscriber(this);
            return this.getUsername() + " subscribed to " + this.lastViewedPerson +  " successfully.";
        }

        Host host = Admin.getHost(this.lastViewedPerson);
        if (this.hostsSubscribed.contains(host)) {
            this.hostsSubscribed.remove(host);
            return this.getUsername() + " unsubscribed from " + this.lastViewedPerson +  " successfully.";
        }
        this.hostsSubscribed.add(host);
        return this.getUsername() + " subscribed to " + this.lastViewedPerson +  " successfully.";
    }

    public void addNotification(ObjectNode notification) {
        notificationList.add(notification);
    }

    public ArrayNode getNotifications() {
        ArrayNode arrayNode = notificationsObjectMapper.createArrayNode();
        for (ObjectNode notification : notificationList) {
            arrayNode.add(notification);
        }
        return arrayNode;
    }

    public void clearNotifications() {
        this.notificationList.clear();
    }

    public String buyMerch(String merchName) {
        if (!this.currentPage.equals("ArtistPage")) {
            return "Cannot buy merch from this page.";
        } else {
            Artist artist = Admin.getArtistByName(this.lastViewedPerson);
            boolean itemExists = false;
            Merch merch = null;
            if (artist.getMerchandise() != null) {
                for (Merch iterMerch : artist.getMerchandise()) {
                    if (iterMerch.getName().equals(merchName)) {
                        itemExists = true;
                        merch = iterMerch;
                    }
                }
            }

            if (!itemExists) {
                return "The merch " + merchName + " doesn't exist.";
            }

            artist.updateMerchRevenue(merch.getPrice());
            this.acquiredMerch.add(merch.getName());
            return this.getUsername() + " has added new merch successfully.";

        }
    }

    public ArrayList<String> seeMerch() {
        return this.acquiredMerch;
    }

    public String updateRecommendations(String recommendationType, CommandInput commandInput) {

        if (recommendationType.equals("fans_playlist")) {
            if (player.getCurrentAudioFile() != null) {
                Song playingSong = Admin.getSongByName(player.getCurrentAudioFile().getName());
                if (playingSong != null) {
                    Artist artist = Admin.getArtistByName(playingSong.getArtist());
                    Admin.setTopFans4Artist(artist.getName());
                    ArrayList<Song> topFansLikedSongs = new ArrayList<>();
                    for (User iterUser : artist.getTopFansInfo()) {
                        ArrayList<Song> fanLikedSongs = new ArrayList<>(iterUser.likedSongs);
                        // blablabla
                    }
                    this.lastRecommendationType = "playlist";
                    this.playlistsRecommendations.add(artist.getName() + " Fan Club recommendations");
                    return "The recommendations for user " + this.getUsername() + " have been updated successfully.";
                }
            }
        } else if (recommendationType.equals("random_song")) {
            if (player.getCurrentAudioFile() != null) {
                Integer timePassed = player.getCurrentAudioFile().getDuration() - player.getStats().getRemainedTime();
                if (timePassed >= 30) {
                    Song playingSong = Admin.getSongByName(player.getCurrentAudioFile().getName());
                    if (playingSong != null) {
                        ArrayList<Song> songsByGenre = Admin.getSongsByGenre(playingSong.getGenre());
                        Random random = new Random(timePassed);
                        int randomIndex = random.nextInt(songsByGenre.size());
                        Song randomSong = songsByGenre.get(randomIndex);
                        this.songRecommendations.add(randomSong.getName());
                        this.lastRecommendationType = "song";
                        return "The recommendations for user " + this.getUsername() + " have been updated successfully.";
                    }
                }
            }
        } else if (recommendationType.equals("random_playlist")){
            if (player.getCurrentAudioFile() != null) {
                Song playingSong = Admin.getSongByName(player.getCurrentAudioFile().getName());
                if (playingSong != null) {
                    this.lastRecommendationType = "playlist";
                    this.playlistsRecommendations.add(this.getUsername() + "'s recommendations");
                    return "The recommendations for user " + this.getUsername() + " have been updated successfully.";
                }
            }
        }

        return "TO BE CONTINUED";
    }

    public String loadRecommendations() {
        if (this.lastRecommendationType == null) {
            return "No recommendations available.";
        } else if (this.lastRecommendationType.equals("song")) {
            LibraryEntry song = Admin.getSongByName(this.songRecommendations.get(songRecommendations.size() - 1));
            searchBar.setLastSelected(song);
            searchBar.setLastSearchType("song");
            load();
            return "Playback loaded successfully.";
        }

        return "TO BE CONTINUED";
    }

    public String previousPage() {
        if (this.viewedPageHistory.size() <= 1) {
            return "There are no pages left to go back.";
        } else {
            this.possibleNextPages.add(this.currentPage);
            possibleNexrHostsArtists.add(viewedHostsArtists.get(viewedHostsArtists.size() - 1));
            viewedHostsArtists.remove(viewedHostsArtists.size() - 1);
            if (viewedHostsArtists.size() > 1) {
                this.lastViewedPerson = viewedHostsArtists.get(viewedHostsArtists.size() - 1);
            }
            this.viewedPageHistory.remove(this.viewedPageHistory.size() - 1);
            this.currentPage = this.viewedPageHistory.get(this.viewedPageHistory.size() - 1);
            return "The user " + this.getUsername() + " has navigated successfully to the previous page.";
        }
    }

    public String nextPage() {
        if (this.possibleNextPages.isEmpty()) {
            return "There are no pages left to go forward.";
        } else {
            viewedHostsArtists.add(possibleNexrHostsArtists.get(possibleNextPages.size() - 1));
            possibleNexrHostsArtists.remove(possibleNexrHostsArtists.size() - 1);
            this.currentPage = this.possibleNextPages.get(possibleNextPages.size() - 1);
            this.viewedPageHistory.add(this.currentPage);
            this.possibleNextPages.remove(possibleNextPages.size() - 1);
            this.lastViewedPerson = viewedHostsArtists.get(viewedHostsArtists.size() - 1);
            return "The user " + this.getUsername() + " has navigated successfully to the next page.";
        }
    }

    public void simulateTime(int time) {
        player.simulatePlayer(time);
    }
}
