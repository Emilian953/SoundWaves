package app.audio.Files;

import app.Admin;
import app.audio.LibraryEntry;
import app.itemsPage.Add;
import app.itemsPage.Announcement;
import app.itemsPage.Remove;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Host extends LibraryEntry {
    private final Map<String, Integer> episodeCounts = new HashMap<>();

    public Host(String name) {
        super(name);
    }

    public String addAnnouncement(CommandInput commandInput) {
        Announcement announcement = new Announcement(commandInput.getName(), Admin.getUser(this.getName()),
                commandInput.getDescription());

        Add add = new Add(this);
        String message = announcement.accept(add);

        return message;
    }

    public String removeAnnouncement(CommandInput commandInput) {
        Announcement realAnnouncement = Admin.getAnnouncementByName(commandInput.getName());

        Remove remove = new Remove(this);
        //String message = realAnnouncement.accept(remove);
        if (!Admin.announcementExists(commandInput.getName())) {
            return commandInput.getUsername() + " has no announcement with the given name.";
        } else {
            return realAnnouncement.accept(remove);
        }
    }

    public List<String> topListenedEpisodes() {
        ArrayList<String> playedEpisodesOriginal = Admin.allListenedEpisodes();
        ArrayList<String> allHostEpisodes = Admin.getHostEpisodes(this.getName());
        ArrayList<String> playedEpisodes = new ArrayList<>();

        for (String iterEp : playedEpisodesOriginal) {
            if (allHostEpisodes.contains(iterEp)) {
                playedEpisodes.add(iterEp);
            }
        }

        for (String episode : playedEpisodes) {
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

        ObjectNode topEpisodes = objectMapper.createObjectNode();

        List<String> top5Episodes = topListenedEpisodes();

        for (String episode : top5Episodes) {
            topEpisodes.put(episode, episodeCounts.get(episode));
        }

        resultNode.set("topEpisodes", topEpisodes);
        resultNode.put("listeners", Admin.hostListenersCount(this.getName()));

        return resultNode;
    }
}
