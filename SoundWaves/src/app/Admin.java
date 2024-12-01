package app;

import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Artist;
import app.audio.Files.Episode;
import app.audio.Files.Host;
import app.audio.Files.Song;
import app.itemsPage.Announcement;
import app.itemsPage.Event;
import app.itemsPage.Merch;
import app.player.PodcastBookmark;
import app.user.User;
import app.audio.Collections.Album;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Getter
    public static ArrayList<Album> albums = new ArrayList<>();
    public static ArrayList<Event> events = new ArrayList<>();
    public static ArrayList<Merch> merchandise = new ArrayList<>();
    public static ArrayList<Artist> artists = new ArrayList<>();
    public static ArrayList<Host> hosts = new ArrayList<>();
    public static ArrayList<Announcement> announcements = new ArrayList<>();
    private static int artistAddIdx = 0;
    private static int timestamp = 0;

    public static void setUsers(List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity(), "user"));
        }
    }

    public static void setSongs(List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    public static ArrayList<Song> convertInputSong(ArrayList<SongInput> songInputList) {
        ArrayList<Song> songList = new ArrayList<>();
        for(SongInput iterSongInput : songInputList) {
            Song newSong = new Song(iterSongInput.getName(), iterSongInput.getDuration(), iterSongInput.getAlbum(),
                    iterSongInput.getTags(), iterSongInput.getLyrics(), iterSongInput.getGenre(),
                    iterSongInput.getReleaseYear(), iterSongInput.getArtist());
            songList.add(newSong);
        }
        return songList;
    }

    public static void setPodcasts(List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(), episodeInput.getDuration(), episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    public static List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    public static ArrayList<Artist> getArtists() {
        return new ArrayList<>(artists);
    }

    public static ArrayList<Host> getHosts() {
        return new ArrayList<>(hosts);
    }

    public static List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    public static ArrayList<Album> getAlbums() { return new ArrayList<>(albums); }

    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(User newUser) {
        users.add(newUser);
    }

    public static void updateTimestamp(int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            user.simulateTime(elapsed);
        }
    }

    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= 5) break;
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= 5) break;
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    public static ArrayList<String> gettTop5Albums() {
        ArrayList<Album> allAlbums = new ArrayList<>(getAlbums());
        ArrayList<String> top5Albums = new ArrayList<>();


        Comparator<Album> albumComparator = new Comparator<Album>() {
            @Override
            public int compare(Album a1, Album a2) {
                int sumA1 = a1.getSongs().stream().mapToInt(Song::getLikes).sum();
                int sumA2 = a2.getSongs().stream().mapToInt(Song::getLikes).sum();

                if (sumA1 == sumA2) {
                    return a1.getName().compareTo(a2.getName());
                }
                return Integer.compare(sumA2, sumA1);
            }
        };

        PriorityQueue<Album> queue = new PriorityQueue<>(albumComparator);
        queue.addAll(allAlbums);

        for (int i = 0; i < 5 && !queue.isEmpty(); i++) {
            Album topAlbum = queue.poll();
            top5Albums.add(topAlbum.getName());
        }

        return top5Albums;
    }


    public static ArrayList<String> getOnlineUsers() {
        ArrayList<String> onlineUsers = new ArrayList<>();
        for (User iterUser : users) {
            if (iterUser.isOnline()) {
                onlineUsers.add(iterUser.getUsername());
            }
        }
        return onlineUsers;
    }

    public static void addAlbum(Album album) {
        albums.add(album);
    }

    public static boolean checkExistsAlbums(Album album) {
        for (Album iterAlbum : albums) {
            if (iterAlbum.getName().equals(album.getName())) {
                if (iterAlbum.getOwner().equals(album.getOwner())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static ArrayList<Album> getAllArtistAlbumms(String artist) {
        ArrayList<Album> allArtistAlbums = new ArrayList<>();
        for (Album iterAlbum : albums) {
            if (iterAlbum.getOwner().equals(artist)) {
                allArtistAlbums.add(iterAlbum);
            }
        }

        return allArtistAlbums;
    }

    public static void addToAllSongs(ArrayList<Song> songList) {
        for (Song iterSong : songList) {
            if (!songs.contains(iterSong)) {
                songs.add(iterSong);
            }
        }
    }

    public static Song getSongByName(String songName) {
        for (Song iterSong : songs) {
            if (iterSong.getName().equals(songName)) {
                return iterSong;
            }
        }
        return null;
    }

    public static boolean checkExistsEvent(String eventName, User owner) {
        for (Event iterEvent : events) {
            if (iterEvent.getOwner().equals(owner)) {
                if (iterEvent.getName().equals(eventName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addEvent(Event event) {
        events.add(event);
    }

    public static boolean checkExistsMerch(String merchName, User owner) {
        for (Merch iterMerch : merchandise) {
            if (iterMerch.getOwner().equals(owner)) {
                if (iterMerch.getName().equals(merchName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addMerch(Merch merch) {
        merchandise.add(merch);
    }

    public static void addArtist(User user) {
        artists.add(new Artist(user.getUsername(), artistAddIdx));
        artistAddIdx ++;
    }

    public static void addHost(User user) {
        hosts.add(new Host(user.getUsername()));
    }

    public static ArrayList<String> getArtistAlbums(String artistName) {
        ArrayList<String> hisAlbums = new ArrayList<>();

        for (Album iterAlbum : albums) {
            if (iterAlbum.getOwner().equals(artistName)) {
                hisAlbums.add(iterAlbum.getName());
            }
        }

        return hisAlbums;
    }

    public static ArrayList<Merch> getArtistMerch(String artistName) {
        ArrayList<Merch> hisMerch = new ArrayList<>();

        for (Merch iterMerch : merchandise) {
            if (iterMerch.getOwner().getUsername().equals(artistName)) {
                hisMerch.add(iterMerch);
            }
        }

        return hisMerch;
    }

    public static ArrayList<Event> getArtistEvents(String artistName) {
        ArrayList<Event> hisEvents = new ArrayList<>();

        for (Event iterEvents : events) {
            if (iterEvents.getOwner().getUsername().equals(artistName)) {
                hisEvents.add(iterEvents);
            }
        }

        return hisEvents;
    }

    public static ArrayList<User> getNormalUsers() {
        ArrayList<User> normalUsers = new ArrayList<>();
        for (User iterUser : users) {
            if (!iterUser.getType().equals("artist") && !iterUser.getType().equals("host")) {
                normalUsers.add(iterUser);
            }
        }

        return normalUsers;
    }

    public static ArrayList<Song> userInteractedWith(User user) {
        ArrayList<Song> songsStillConnected = new ArrayList<>();
        for (User iterUser : users) {
            if (!iterUser.getPlayerStats().getName().isEmpty()) {
                Song playedSong = getSongByName(iterUser.getPlayerStats().getName());

                if (playedSong != null) {
                    if (playedSong.getArtist().equals(user.getUsername())) {
                        songsStillConnected.add(playedSong);
                    }
                }
            }

        }

        return songsStillConnected;
    }

    public static void deleteArtistSongs(User user) {
        songs.removeIf(iterSong -> iterSong.getArtist().equals(user.getUsername()));
    }

    public static void deleteArtistPlaylists(User user) {
        user.getPlaylists().clear();
    }

    public static void deletePlaylistsFromFollowed(User user) {
        for (User iterUser : users) {
            iterUser.getFollowedPlaylists().removeAll(user.getPlaylists());
        }
    }

    public static void deleteSongs(ArrayList<Song> songsToBeDeleted) {
        songs.removeIf(songsToBeDeleted::contains);
    }

    public static boolean hisPodcastsExists(String user, String podcastName) {
        for (Podcast iterPodcast : podcasts) {
            if (iterPodcast.getName().equals(podcastName) && iterPodcast.getOwner().equals(user)) {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<Episode> convertToEpisode(ArrayList<EpisodeInput> episodeInputs) {
        ArrayList<Episode> episodes = new ArrayList<>();

        for (EpisodeInput iterEps : episodeInputs) {
            episodes.add(new Episode(iterEps.getName(), iterEps.getDuration(), iterEps.getDescription()));
        }

        return episodes;
    }

    public static void addNewPodcast(Podcast podcast) {
        podcasts.add(podcast);
    }

    public static ArrayList<Podcast> allHostPodcasts(String hostName) {
        ArrayList<Podcast> allPodcasts = new ArrayList<>();

        for (Podcast iterPodcast : podcasts) {
            if (iterPodcast.getOwner().equals(hostName)) {
                allPodcasts.add(iterPodcast);
            }
        }

        return allPodcasts;
    }

    public static boolean announcementExists(String announcement) {
        for (Announcement iterAnnouncement : announcements) {
            if (iterAnnouncement.getName().equals(announcement)) {
                return true;
            }
        }

        return false;
    }

    public static void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
    }

    public static void removeAnnouncement(Announcement announcement) {
        announcements.remove(announcement);
    }

    public static Announcement getAnnouncementByName(String announcementName) {
        for (Announcement iterAnnouncement : announcements) {
            if (iterAnnouncement.getName().equals(announcementName)) {
                return iterAnnouncement;
            }
        }

        return null;
    }

    public static ArrayList<Announcement> getAllAnnouncementsByOwner(String ownerName) {
        ArrayList<Announcement> allAnnouncements = new ArrayList<>();
        for (Announcement iterAnnouncement : announcements) {
            if (ownerName.equals(iterAnnouncement.getOwner().getUsername())) {
                allAnnouncements.add(iterAnnouncement);
            }
        }

        return allAnnouncements;
    }

    public static boolean albumExists(String albumName) {
        for (Album iterAlbum : albums) {
            if (iterAlbum.getName().equals(albumName)) {
                return true;
            }
        }

        return false;
    }

    public static Album getAlbumByName(String albumName) {
        for (Album iterAlbum : albums) {
            if (iterAlbum.getName().equals(albumName)) {
                return iterAlbum;
            }
        }

        return null;
    }

    public static boolean songInteractedWith(Song song) {
        for (User iterUser : users) {
            if (iterUser.getPlayerStats().getName().equals(song.getName())) {
                return true;
            }
        }

        return false;
    }

    public static void removeSongs(ArrayList<Song> songsToDelete) {
        songs.removeAll(songsToDelete);
    }

    public static void removeAlbum(Album album) {
        albums.remove(album);
    }

    public static boolean hasPodcast(String podcastName) {
        for (Podcast iterPodcast : podcasts) {
            if (iterPodcast.getName().equals(podcastName)) {
                return true;
            }
        }

        return false;
    }

    public static Podcast getPodcastByName(String podcastName) {
        for (Podcast iterPodcast : podcasts) {
            if (iterPodcast.getName().equals(podcastName)) {
                return iterPodcast;
            }
        }

        return null;
    }

    public static boolean podcastInteractedWith(String podcastName) {
        Podcast podcast = getPodcastByName(podcastName);
        if (podcast != null) {
            for (User iterUser : users) {
                for (Episode iterEpisode : podcast.getEpisodes()) {
                    if (iterUser.getPlayerStats().getName().equals(iterEpisode.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean podcastStillConnected(String podcastName) {
        for (User iterUser : users) {
            for (String iterPlayedPodcast : iterUser.getPlayedPodcasts()) {
                if (iterPlayedPodcast.equals(podcastName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void removeThisPodcast(String podcastName) {
        Podcast podcast = getPodcastByName(podcastName);
        podcasts.remove(podcast);
    }

    public static Podcast getPodcastByEpisodeName(String episodeName) {
        for (Podcast iterPodcast : podcasts) {
            for (Episode iterEpisode : iterPodcast.getEpisodes()) {
                if (iterEpisode.getName().equals(episodeName)) {
                    return iterPodcast;
                }
            }
        }

        return null;
    }

    public static void deleteFollows(User user) {
        for (Playlist iterPlaylist : user.getFollowedPlaylists()) {
            iterPlaylist.decreaseFollowers();
        }
    }

    public static boolean personalPageStillViewed(String username) {
        for (User iterUser : users) {
            if (iterUser.getLastViewedPerson() != null) {
                if (iterUser.getLastViewedPerson().equals(username) && iterUser.isOnPageRN()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Event getEventByName(String eventName) {
        for (Event iterEvent : events) {
            if (iterEvent.getName().equals(eventName)) {
                return iterEvent;
            }
        }

        return null;
    }

    public static void removeEvent(Event event) {
        events.remove(event);
    }

    public static boolean existsSongInPlaylists(Album album) {
        for (User iterUser : users) {
            for (Song iterSong : album.getSongs()) {
                if (iterUser.getPlaylists() != null) {
                    for (Playlist iterPlaylist : iterUser.getPlaylists()) {
                        if (iterPlaylist.getSongs().contains(iterSong)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static void deleteThisUser(User user) {
        users.remove(user);
    }

    public static void deleteArtist(Artist artist) {
        artists.remove(artist);
    }

    public static void deleteHost(Host host) {
        hosts.remove(host);
    }

    public static boolean someoneFollowsPlaylists(User user) {
        for (User iterUser : users) {
            if (iterUser.getFollowedPlaylists() != null) {
                for (Playlist iterPlaylists : iterUser.getFollowedPlaylists()) {
                    if (iterPlaylists.getOwner().equals(user.getUsername())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean someonePlaysPlaylist(User user) {
        for (User iterUser : users) {
            if (iterUser.getPlayer().getType() != null) {
                if (iterUser.getPlayer().getType().equals("playlist") && user.getPlaylists() != null) {
                    for (Playlist iterPlaylist : user.getPlaylists()) {
                        for (Song iterSong : iterPlaylist.getSongs()) {
                            if (iterSong.getName().equals(iterUser.getPlayerStats().getName())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public static Artist getArtistByName(String artistName) {
        for (Artist iterArtist : artists) {
            if (iterArtist.getName().equals(artistName)) {
                return iterArtist;
            }
        }

        return null;
    }

    public static void setArtistsLikes() {
        for (User iterUser : users) {
            for (Song iterSong : iterUser.getLikedSongs()) {
                if (getArtistByName(iterSong.getArtist()) != null) {
                    Objects.requireNonNull(getArtistByName(iterSong.getArtist())).incrementLikes();
                }
            }
        }
    }

    public static ArrayList<String> getTop5Artists() {
        ArrayList<Artist> sortedArtists = new ArrayList<>(artists);
        sortedArtists.sort(Comparator.comparingInt(Artist::getLikes).reversed());
        ArrayList<String> topArtists = new ArrayList<>();
        int count = 0;
        for (Artist iterArtist : sortedArtists) {
            if (count >= 5) break;
            topArtists.add(iterArtist.getName());
            count++;
        }
        return topArtists;
    }

    public static void deleteFromLikedSongs(String artistName) {
        for (User iterUser : users) {
            if (iterUser.getLikedSongs() != null) {
                Iterator<Song> iterator = iterUser.getLikedSongs().iterator();
                while (iterator.hasNext()) {
                    Song iterSong = iterator.next();
                    if (iterSong.getArtist().equals(artistName)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static Host getHost(String hostName) {
        for (Host iterHost : hosts) {
            if (iterHost.getName().equals(hostName)) {
                return iterHost;
            }
        }

        return null;
    }

    public static Artist getArtist(String artistName) {
        for (Artist iterArtists : artists) {
            if (iterArtists.getName().equals(artistName)) {
                return iterArtists;
            }
        }

        return null;
    }

    public static ArrayList<String> getHostEpisodes(String hostName) {
        ArrayList<String> episodeNames = new ArrayList<>();
        for (Podcast iterPod : podcasts) {
            if (iterPod.getOwner().equals(hostName)) {
                for (Episode iterEpisode : iterPod.getEpisodes()) {
                    episodeNames.add(iterEpisode.getName());
                }
            }
        }

        return episodeNames;
    }

    public static Integer hostListenersCount(String hostName) {
        ArrayList<String> hostEpisodes = Admin.getHostEpisodes(hostName);
        Integer counter = 0;
        for (User iterUser : users) {
            for (String iterEp : iterUser.getPlayedEpisodesString()) {
                if (hostEpisodes.contains(iterEp)) {
                    counter ++;
                    break;
                }
            }
        }

        return counter;
    }

    public static ArrayList<String> allListenedEpisodes() {
        ArrayList<String> allListenedEpisodes = new ArrayList<>();
        for (User iterUser : users) {
            allListenedEpisodes.addAll(iterUser.getPlayedEpisodesString());
        }

        return allListenedEpisodes;
    }
    public static ArrayList<Song> allListenedSongs() {
        ArrayList<Song> allListenedSongs = new ArrayList<>();
        for (User iterUser : users) {
            allListenedSongs.addAll(iterUser.getPlayedSongsData());
        }

        return allListenedSongs;
    }

    public static ArrayList<String> allArtistListeners(String artistName) {
        ArrayList<String> allListeners = new ArrayList<>();
        for (User iterUser : users) {
            for (Song iterSong : iterUser.getPlayedSongsData()) {
                if (iterSong.getArtist().equals(artistName)) {
                    allListeners.add(iterUser.getUsername());
                }
            }
        }

        return allListeners;
    }

    public static ArrayList<User> allArtistListenersData(String artistName) {
        ArrayList<User> allListeners = new ArrayList<>();
        for (User iterUser : users) {
            for (Song iterSong : iterUser.getPlayedSongsData()) {
                if (iterSong.getArtist().equals(artistName)) {
                    allListeners.add(iterUser);
                }
            }
        }

        return allListeners;
    }

    public static void setTopFans4Artist(String artistName) {
        Artist artist = getArtistByName(artistName);
        artist.topFans();
    }

    public static Integer numberOfArtistListeners(String artistName) {
        ArrayList<User> allListeners = new ArrayList<>();
        for (User iterUser : users) {
            for (Song iterSong : iterUser.getPlayedSongsData()) {
                if (iterSong.getArtist().equals(artistName)) {
                    allListeners.add(iterUser);
                }
            }
        }

        Set<User> uniqueUsers = new HashSet<>();
        for (User user : allListeners) {
            uniqueUsers.add(user);
        }
        return uniqueUsers.size();
    }

    public static Episode getEpisodeByName(String episodeName) {
        for (Podcast iterPod : podcasts) {
            for (Episode iterEpisode : iterPod.getEpisodes()) {
                if (iterEpisode.getName().equals(episodeName)) {
                    return iterEpisode;
                }
            }
        }

        return null;
    }

//    public static Integer numberOfHostListeners(String hostName) {
//        ArrayList<User> allListeners = new ArrayList<>();
//        for (User iterUser : users) {
//            for (Episode iterEpisode : iterUser.getPlayedEpisodesData()) {
//                if (iterEpisode..equals(artistName)) {
//                    allListeners.add(iterUser);
//                }
//            }
//        }
//
//        Set<User> uniqueUsers = new HashSet<>();
//        for (User user : allListeners) {
//            uniqueUsers.add(user);
//        }
//        return uniqueUsers.size();
//    }

    public static ArrayList<Artist> artistsWithSoldMerch() {
        ArrayList<Artist> artistsWithSoldMerch = new ArrayList<>();
        for (Artist iterArtist : artists) {
            if (iterArtist.getMerchRevenue() != 0.0) {
                artistsWithSoldMerch.add(iterArtist);
            }
        }

        return artistsWithSoldMerch;
    }

    public static void setTotalRevenue4Artists() {
        for (Artist iterArtist : artists) {
            iterArtist.setTotalRevenue();
        }
    }

    public static ArrayList<Artist> listenedArtists() {
        setTotalRevenue4Artists();
        ArrayList<Artist> listenedArtists = new ArrayList<>();

        for (User iterUser : users) {
            for (Song iterSong : iterUser.getPlayedSongsData()) {
                listenedArtists.add(getArtistByName(iterSong.getArtist()));
            }
        }

        listenedArtists.addAll(artistsWithSoldMerch());

        if (listenedArtists != null) {
            Collections.sort(listenedArtists, new Comparator<Artist>() {
                @Override
                public int compare(Artist artist1, Artist artist2) {
                    // Handle nulls
                    if (artist1 == null && artist2 == null) {
                        return 0;
                    } else if (artist1 == null) {
                        return -1;
                    } else if (artist2 == null) {
                        return 1;
                    }

                    // First compare by song revenue
                    int revenueCompare = Double.compare(artist2.getTotalRevenue(), artist1.getTotalRevenue());
                    if (revenueCompare != 0) {
                        return revenueCompare;
                    }

                    // If revenues are equal, then compare by name
                    return artist1.getName().compareTo(artist2.getName());
                }
            });
        }

        return listenedArtists;
    }

    public static Song getSongByNameAndCollection(String name, String collection) {
        for (Song iterSong : songs) {
            if (iterSong.getName().equals(name) && iterSong.getAlbum().equals(collection)) {
                return iterSong;
            }
        }

        return null;
    }

    public static ArrayList<Album> getSortedAlbums() {

        ArrayList<Album> allAlbums = new ArrayList<>(albums);

        Collections.sort(allAlbums, new Comparator<Album>() {
            @Override
            public int compare(Album a1, Album a2) {
                // Compare by addCount using the Admin class
                return Admin.getArtistByName(a1.getOwner()).getAddCount() - Admin.getArtistByName(a2.getOwner()).getAddCount();
            }
        });

        return new ArrayList<>(allAlbums);
    }

    public static void premiumIncome() {
        for (User iterUser : users) {
            if (iterUser.getType().equals("premium")) {
                ArrayList<Artist> listenedArtists = new ArrayList<>();
                for (Song iterSong : iterUser.getPlayedWhilePremium()) {
                    listenedArtists.add(Admin.getArtistByName(iterSong.getArtist()));
                }

                int totalSongs = iterUser.getPlayedWhilePremium().size();

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
                for (Song iterSong : iterUser.getPlayedWhilePremium()) {
                    iterSong.updateRevenue(individualRevenueSong);
                    Artist thisArtist = getArtistByName(iterSong.getArtist());
                    if (iterSong.getRevenue() > thisArtist.getMostProfitableSongValue()) {
                        thisArtist.setMostProfitableSongValue(iterSong.getRevenue());
                        thisArtist.setMostProfitableSong(iterSong);
                    } else if (iterSong.getRevenue() == thisArtist.getMostProfitableSongValue()) {
                        if (iterSong.getName().compareTo(thisArtist.getMostProfitableSong().getName()) < 0) {
                            thisArtist.setMostProfitableSong(iterSong);
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<Song> getSongsByGenre(String genre) {
        ArrayList<Song> songsByGenre = new ArrayList<>();

        for (Song iterSong : songs) {
            if (iterSong.getGenre().equals(genre)) {
                songsByGenre.add(iterSong);
            }
        }

        return songsByGenre;
    }

    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        albums = new ArrayList<>();
        events = new ArrayList<>();
        merchandise = new ArrayList<>();
        artists = new ArrayList<>();
        hosts = new ArrayList<>();
        announcements = new ArrayList<>();
        timestamp = 0;
        artistAddIdx = 0;
    }
}
