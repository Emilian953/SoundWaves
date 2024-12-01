package app.audio.Files;

import app.Admin;
import app.audio.LibraryEntry;
import app.itemsPage.Add;
import app.itemsPage.Event;
import app.itemsPage.Merch;
import app.itemsPage.Remove;
import app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Artist extends LibraryEntry {
    Integer likes = 0;
    private final ArrayList<Merch> merchandise = new ArrayList<>();
    private final Map<String, Integer> albumCounts = new HashMap<>();
    private final Map<String, Integer> songCounts = new HashMap<>();
    private final Map<String, Integer> fanCounts = new HashMap<>();
    @Setter
    private Integer ranking;
    private final Integer addCount;
    private Double songRevenue;
    private Double merchRevenue;
    private Double totalRevenue;
    @Setter
    private Song mostProfitableSong;
    @Setter
    private double mostProfitableSongValue = 0.0;
    private final List<User> subscribers = Collections.synchronizedList(new ArrayList<>());
    private final ArrayList<User> topFansInfo = new ArrayList<>();

    public Artist(String name, Integer addCount) {
        super(name);
        this.addCount = addCount;
        this.songRevenue = 0.0;
        this.merchRevenue = 0.0;
    }

    public void incrementLikes() {
        this.likes ++;
    }

    public String addEvent(CommandInput commandInput) {
        Event event = new Event(commandInput.getName(), Admin.getUser(this.getName()), commandInput.getDescription(),
                commandInput.getDate());

        Add add = new Add(this);
        String message = event.accept(add);

        return message;
    }

    public String addMerch(CommandInput commandInput, ObjectMapper objectMapper) {
        User user = Admin.getUser(commandInput.getUsername());
        Merch merch = new Merch(commandInput.getName(), user, commandInput.getDescription(),
                commandInput.getPrice());

        Add add = new Add(this);
        String message = merch.accept(add);

        if (message.endsWith("merchandise successfully.")) {
            Admin.getArtistByName(commandInput.getUsername()).notifyMerch(objectMapper);
            this.merchandise.add(merch);
        }

        return message;
    }

    public String removeEvent(CommandInput commandInput) {
        Event event = Admin.getEventByName(commandInput.getName());
        Remove remove = new Remove(this);
        if (event == null) {
            return commandInput.getUsername() + " doesn't have an event with the given name.";
        } else {
            return event.accept(remove);
        }
    }

    public List<String> topListenedAlbums() {
        ArrayList<String> playedAlbums = new ArrayList<>();
        ArrayList<Song> playedSongsData = Admin.allListenedSongs();

        for (Song iterSong : playedSongsData) {
            if (iterSong.getArtist().equals(this.getName())) {
                playedAlbums.add(iterSong.getAlbum());
            }
        }

        for (String album : playedAlbums) {
            albumCounts.put(album, albumCounts.getOrDefault(album, 0) + 1);
        }

        List<String> topAlbums = albumCounts.entrySet()
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

        return topAlbums;
    }

    public List<String> topListenedSongs() {
        ArrayList<String> playedSongsHere = new ArrayList<>();
        ArrayList<Song> playedSongsData = Admin.allListenedSongs();

        for (Song iterSong : playedSongsData) {
            if (iterSong.getArtist().equals(this.getName())) {
                playedSongsHere.add(iterSong.getName());
            }
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

    public List<String> topFans() {
        ArrayList<User> allListeners = Admin.allArtistListenersData(this.getName());

        for (User fan : allListeners) {
            fanCounts.put(fan.getUsername(), fanCounts.getOrDefault(fan.getUsername(), 0) + 1);
        }

        List<String> topFans = fanCounts.entrySet()
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

        this.topFansInfo.clear();
        for (String iterUser : topFans) {
            this.topFansInfo.add(Admin.getUser(iterUser));
        }

        return topFans;
    }

    public ObjectNode wrapped(ObjectMapper objectMapper) {
        ObjectNode resultNode = objectMapper.createObjectNode();

        ObjectNode topAlbumsNode = objectMapper.createObjectNode();
        ObjectNode topSongsNode = objectMapper.createObjectNode();
        ArrayNode topFansArrayNode = objectMapper.createArrayNode();

        List<String> top5Albums = topListenedAlbums();
        List<String> top5Songs = topListenedSongs();
        List<String> top5Fans = topFans();


        for (String album : top5Albums) {
            topAlbumsNode.put(album, albumCounts.get(album));
        }

        for (String song : top5Songs) {
            topSongsNode.put(song, songCounts.get(song));
        }

        for (String fan : top5Fans) {
            topFansArrayNode.add(fan);
        }

        resultNode.set("topAlbums", topAlbumsNode);
        resultNode.set("topSongs", topSongsNode);
        resultNode.set("topFans", topFansArrayNode);
        resultNode.put("listeners", Admin.numberOfArtistListeners(this.getName()));

        return resultNode;
    }

    public void updateRevenue (double songRevenue) {
        this.songRevenue += songRevenue;
    }

    public void updateMerchRevenue (double merchRevenue) {
        this.merchRevenue += merchRevenue;
    }

    public void addSubscriber(User user) {
        this.subscribers.add(user);
    }

    public void removeSubscriber(User user) {
        this.subscribers.remove(user);
    }

    public void notifyAlbum(ObjectMapper objectMapper) {
        for (User iterUser : this.subscribers) {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("name", "New Album");
            notification.put("description", "New Album from " + this.getName() + ".");
            iterUser.addNotification(notification);
        }
    }

    public void notifyMerch(ObjectMapper objectMapper) {
        for (User iterUser : this.subscribers) {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("name", "New Merchandise");
            notification.put("description", "New Merchandise from " + this.getName() + ".");
            iterUser.addNotification(notification);
        }
    }

    public void notifyEvent(ObjectMapper objectMapper) {
        for (User iterUser : this.subscribers) {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("name", "New Event");
            notification.put("description", "New Event from " + this.getName() + ".");
            iterUser.addNotification(notification);
        }
    }

    public void setTotalRevenue() {
        this.totalRevenue = this.merchRevenue + this.songRevenue;
    }

    public Integer listenersCount() {
        return Admin.numberOfArtistListeners(getName());
    }
}
