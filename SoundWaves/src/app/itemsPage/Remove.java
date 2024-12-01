package app.itemsPage;

import app.Admin;
import app.audio.Files.Artist;
import app.audio.Files.Host;

public class Remove implements ItemVisitor {
    private Artist artist;
    private Host host;
    public Remove (Artist artist) {
        this.artist = artist;
    }

    public Remove (Host host) {
        this.host = host;
    }
    @Override
    public String visit (Announcement announcement) {
        Admin.removeAnnouncement(announcement);
        return announcement.getOwner().getUsername() + " has successfully deleted the announcement.";
    }

    @Override
    public String visit (Event event) {
        Admin.removeEvent(event);
        return event.getOwner().getUsername() + " deleted the event successfully.";
    }

    @Override
    public String visit (Merch merch) {
        return null;
    }
}
