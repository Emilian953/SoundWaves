package app;

import app.audio.Collections.*;
import app.audio.Files.Artist;
import app.audio.Files.Host;
import app.audio.Files.Song;
import app.itemsPage.Event;
import app.itemsPage.Merch;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import fileio.input.SongInput;

import java.util.*;

public class CommandRunner {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectNode search(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            Filters filters = new Filters(commandInput.getFilters());
            String type = commandInput.getType();

            ArrayList<String> results = user.search(filters, type);
            String message;

            if (user.isOnline()) {
                message = "Search returned " + results.size() + " results";
            } else {
                message = user.getUsername() + " is offline.";
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);
            objectNode.put("results", objectMapper.valueToTree(results));

            return objectNode;
        }
        return null;
    }

    public static ObjectNode select(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {

            String message = user.select(commandInput.getItemNumber());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode load(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.load();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }
        return null;
    }

    public static ObjectNode playPause(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.playPause();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }
        return null;
    }

    public static ObjectNode repeat(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.repeat();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode shuffle(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            Integer seed = commandInput.getSeed();
            String message = user.shuffle(seed);

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode forward(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.forward();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode backward(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.backward();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode like(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message;

            if (!user.isOnline()) {
                message = user.getUsername() + " is offline.";
            } else {
                message = user.like();
            }

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode next(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.next();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode prev(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.prev();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode createPlaylist(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.createPlaylist(commandInput.getPlaylistName(), commandInput.getTimestamp());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode addRemoveInPlaylist(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode switchVisibility(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode showPlaylists(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            ArrayList<PlaylistOutput> playlists = user.showPlaylists();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("result", objectMapper.valueToTree(playlists));

            return objectNode;
        }

        return null;
    }

    public static ObjectNode follow(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String message = user.follow();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", message);

            return objectNode;
        }

        return null;
    }

    public static ObjectNode status(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            PlayerStats stats = user.getPlayerStats();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("stats", objectMapper.valueToTree(stats));

            return objectNode;
        }
        return null;
    }

    public static ObjectNode showLikedSongs(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            ArrayList<String> songs = user.showPreferredSongs();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("result", objectMapper.valueToTree(songs));

            return objectNode;
        }
        return null;
    }

    public static ObjectNode getPreferredGenre(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user != null) {
            String preferredGenre = user.getPreferredGenre();

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("result", objectMapper.valueToTree(preferredGenre));

            return objectNode;
        }
        return null;
    }

    public static ObjectNode getTop5Songs(CommandInput commandInput) {
        List<String> songs = Admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    public static ObjectNode getTop5Playlists(CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    public static ObjectNode switchConnectionStatus(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        if (user != null) {
            if (user.getType().equals("artist") || user.getType().equals("host")) {
                message = commandInput.getUsername() + " is not a normal user.";
            } else {
                message = user.switchConnectionStatus(user, user.getUsername());
            }
        } else {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode getOnlineUsers(CommandInput commandInput) {
        List<String> users = Admin.getOnlineUsers();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(users));

        return objectNode;
    }

    public static ObjectNode addUser(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        if (user != null) {
            message = "The username " + commandInput.getUsername() + " is already taken.";
        } else {
            user = new User(commandInput.getUsername(), commandInput.getAge(), commandInput.getCity(), commandInput.getType());
            Admin.addUser(user);
            if (user.getType().equals("artist")) {
                user.toOffline();
                Admin.addArtist(user);
            }

            if (user.getType().equals("host")) {
                user.toOffline();
                Admin.addHost(user);
            }
            message = "The username " + commandInput.getUsername() + " has been added successfully.";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode addAlbum(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else if (user.getType().equals("artist")) {

            ArrayList<SongInput> songInputList = commandInput.getSongs();
            ArrayList<Song> songList;
            songList = Admin.convertInputSong(songInputList);
            Album newAlbum = new Album(commandInput.getName(), commandInput.getReleaseYear(),
                    commandInput.getDescription(), songList, commandInput.getUsername());

            if (Admin.checkExistsAlbums(newAlbum)) {
                message = commandInput.getUsername() + " has another album with the same name.";
            } else {
                if (newAlbum.songAlreadyExists()) {
                    message = commandInput.getUsername() + " has the same song at least twice in this album.";
                } else {
                    Admin.getArtistByName(commandInput.getUsername()).notifyAlbum(objectMapper);
                    Admin.addAlbum(newAlbum);
                    Admin.addToAllSongs(songList);
                    message = commandInput.getUsername() + " has added new album successfully.";
                }
            }
        } else {
            message = commandInput.getUsername() + " is not an artist.";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode showAlbums (CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) return null;

        ArrayList<AlbumOutput> result = new ArrayList<>();
        ArrayList<Album> allOfOwnerAlbums;

        allOfOwnerAlbums = Admin.getAllArtistAlbumms(commandInput.getUsername());

        for (Album iterAlbum : allOfOwnerAlbums) {
            AlbumOutput albumOutput = new AlbumOutput(iterAlbum);
            result.add(albumOutput);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(result));

        return objectNode;
    }

    public static ObjectNode printCurrentPage(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        if (user == null) return null;

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", user.printCurrentPage(commandInput));

        if (!user.isOnline()) {
            String message = commandInput.getUsername() + " is offline.";
            objectNode.put("message", message);
        }

        return objectNode;
    }

    public static ObjectNode addEvent (CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Artist artist = Admin.getArtist(commandInput.getUsername());
        String message;
        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            if (!user.getType().equals("artist")) {
                message = commandInput.getUsername() + " is not an artist.";
            } else {
                if (Admin.checkExistsEvent(commandInput.getName(), user)) {
                    message = commandInput.getUsername() + " has another event with the same name.";
                } else {
                    Admin.getArtistByName(commandInput.getUsername()).notifyEvent(objectMapper);
                    message = artist.addEvent(commandInput);
                }
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode addMerch(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Artist artist = Admin.getArtist(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            if (!user.getType().equals("artist")) {
                message = commandInput.getUsername() + " is not an artist.";
            } else {
                message = artist.addMerch(commandInput, objectMapper);
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode getAllUsers(CommandInput commandInput) {
        ArrayList<User> normalUsers = Admin.getNormalUsers();
        ArrayList<Artist> artists = new ArrayList<>(Admin.getArtists());
        ArrayList<Host> hosts = new ArrayList<>(Admin.getHosts());

        ArrayList<String> users = new ArrayList<>();

        for (User iterUser : normalUsers) {
            users.add(iterUser.getUsername());
        }

        for (Artist iterArtist : artists) {
            users.add(iterArtist.getName());
        }

        for (Host iterHost : hosts) {
            users.add(iterHost.getName());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(users));

        return objectNode;
    }

    public static ObjectNode deleteUser(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        ArrayList<Song> songsStillConnected = new ArrayList<>(Admin.userInteractedWith(user));

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            boolean okay = false;
            if (user.getType().equals("host")) {
                for (Podcast iterPodcast : Admin.allHostPodcasts(user.getUsername())) {
                    if (Admin.podcastStillConnected(iterPodcast.getName())) {
                        okay = true;
                    }
                }
            }

            if (user.getType().equals("artist") || user.getType().equals("host")) {
                if (Admin.personalPageStillViewed(user.getUsername())) {
                    okay = true;
                }
            }

            if (Admin.someonePlaysPlaylist(user)) {
                okay = true;
            }

            if (!songsStillConnected.isEmpty() || okay) {
                message = commandInput.getUsername() + " can't be deleted.";
            } else {
                 Admin.deleteFromLikedSongs(user.getUsername());
                 Admin.deleteFollows(user);
                 Admin.deletePlaylistsFromFollowed(user);
                 Admin.deleteArtistPlaylists(user);
                 Admin.deleteArtistSongs(user);
                 message = commandInput.getUsername() + " was successfully deleted.";
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode addPodcast(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        if (user == null) {
            message = "The username " +  commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.addPodcast(commandInput.getName(), commandInput.getEpisodes());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode showPodcasts(CommandInput commandInput) {
        ArrayList<PodcastOutput> result = new ArrayList<>();
        ArrayList<Podcast> allPodcasts = Admin.allHostPodcasts(commandInput.getUsername());

        for (Podcast iterPodcast : allPodcasts) {
            PodcastOutput podcastOutput = new PodcastOutput(iterPodcast);
            result.add(podcastOutput);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(result));

        return objectNode;
    }

    public static ObjectNode addAnnouncement(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Host host = Admin.getHost(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            if (host == null) {
                message = commandInput.getUsername() + " is not a host.";
            } else {
                message = host.addAnnouncement(commandInput);
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", objectMapper.valueToTree(message));

        return objectNode;
    }

    public static ObjectNode removeAnnouncement(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Host host = Admin.getHost(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            if (host == null) {
                message = commandInput.getUsername() + " is not a host.";
            } else {
                message = host.removeAnnouncement(commandInput);
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", objectMapper.valueToTree(message));

        return objectNode;
    }

    public static ObjectNode removeAlbum(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.removeAlbum(commandInput.getName());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode changePage(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "IDK";
        } else {
            message = user.changePage(commandInput.getNextPage());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode removePodcast(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.removePodcast(commandInput.getName());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode removeEvent(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        Artist artist = Admin.getArtist(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            if (artist == null) {
                message = commandInput.getUsername() + " is not an artist.";
            } else {
                message = artist.removeEvent(commandInput);
            }
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode getTop5Albums(CommandInput commandInput) {
        ArrayList<String> albums = Admin.gettTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albums));

        return objectNode;
    }

    public static ObjectNode getTop5Artists(CommandInput commandInput) {
        Admin.setArtistsLikes();
        ArrayList<String> artists = Admin.getTop5Artists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(artists));

        return objectNode;
    }

    public static ObjectNode wrapped(CommandInput commandInput) {

        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (user == null) {
            objectNode.put("result", "No data to show for user " + commandInput.getUsername() + ".");
        } else if (!user.getType().equals("artist") && !user.getType().equals("host")) {
            if (!user.getPlayedSongsData().isEmpty() || !user.getPlayedEpisodesData().isEmpty()) {
                objectNode.put("result", user.wrapped(objectMapper));
            } else {
                objectNode.put("message", "No data to show for user " + commandInput.getUsername() + ".");
            }
        } else if (user.getType().equals("artist")) {
            Artist artist = Admin.getArtistByName(user.getUsername());
            if (artist != null) {
                if (Admin.listenedArtists().contains(artist)) {
                    objectNode.put("result", artist.wrapped(objectMapper));
                } else {
                    objectNode.put("message", "No data to show for artist " + artist.getName() + ".");
                }
            }
        } else {
            Host host = Admin.getHost(user.getUsername());
            if (host == null) {
                objectNode.put("message", "No data to show for host " + commandInput.getUsername() + ".");
            } else {
                objectNode.put("result", host.wrapped(objectMapper));
            }
        }

        return objectNode;
    }

    public static ObjectNode buyPremium(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.buyPremium();
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode cancelPremium(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.cancelPremium(commandInput);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode adBreak(CommandInput commandInput) {
        String message;
        User user = Admin.getUser(commandInput.getUsername());

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.adBreak(commandInput.getPrice(), commandInput);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode subscribe(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.subscribe();
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode getNotifications(CommandInput commandInput) {

        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        if (user != null) {
            objectNode.set("notifications", user.getNotifications());
            user.clearNotifications();
        }

        return objectNode;
    }

    public static ObjectNode buyMerch(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.buyMerch(commandInput.getName());
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode seeMerch(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (user == null) {
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
        } else {
            objectNode.put("result", objectMapper.valueToTree(user.seeMerch()));
        }

        return objectNode;
    }

    public static ObjectNode updateRecommendations(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "The username " + commandInput.getUsername() + " doesn't exist.";
        } else {
            message = user.updateRecommendations(commandInput.getRecommendationType(), commandInput);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode previousPage(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", user.previousPage());

        return objectNode;
    }

    public static ObjectNode nextPage(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", user.nextPage());

        return objectNode;
    }

    public static ObjectNode loadRecommendations(CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            message = "No recommendations available.";
        } else {
            message = user.loadRecommendations();
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode endProgram() {
        Admin.premiumIncome();
        ArrayList<Artist> listenedArtists = Admin.listenedArtists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "endProgram");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        List<Artist> rankingListenedArtists = new ArrayList<>(listenedArtists);

        Set<Artist> uniqueArtistsSet = new LinkedHashSet<>(rankingListenedArtists);

        rankingListenedArtists.clear();
        rankingListenedArtists.addAll(uniqueArtistsSet);

        for (int i = 0; i < rankingListenedArtists.size(); i++) {
            if (rankingListenedArtists.get(i) != null) {
                rankingListenedArtists.get(i).setRanking(i + 1);
            }
        }

        double zero = 0.0;

        for (int i = 0; i < listenedArtists.size(); i++) {
            Artist artist = listenedArtists.get(i);

            if (artist != null) {
                ObjectNode artistNode = objectMapper.createObjectNode();

                artistNode.put("merchRevenue", artist.getMerchRevenue());
                artistNode.put("songRevenue", Math.round(artist.getSongRevenue() * 100.0) / 100.0);
                artistNode.put("ranking", artist.getRanking());
                if (artist.getMostProfitableSongValue() == 0.0) {
                    artistNode.put("mostProfitableSong", "N/A");
                } else {
                    artistNode.put("mostProfitableSong", artist.getMostProfitableSong().getName());
                }

                resultNode.set(artist.getName(), artistNode);
            }
        }

        if (resultNode != null) {
            objectNode.put("result", resultNode);
        }

        return objectNode;
    }
}
