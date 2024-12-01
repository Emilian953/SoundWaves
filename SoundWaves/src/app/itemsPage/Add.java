package app.itemsPage;

import app.Admin;
import app.audio.Files.Artist;
import app.audio.Files.Host;

public class Add implements ItemVisitor {
    private Artist artist;
    private Host host;
    public Add (Artist artist) {
        this.artist = artist;
    }

    public Add (Host host) {
        this.host = host;
    }
    @Override
    public String visit (Announcement announcement) {

        if (Admin.announcementExists(announcement.getName())) {
            return announcement.getOwner() + " has already added an announcement with this name.";
        } else {
            Admin.addAnnouncement(announcement);
            return announcement.getOwner().getUsername() + " has successfully added new announcement.";
        }
    }

    @Override
    public String visit (Event event) {
        String[] parts = event.getDate().split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        boolean check = true;

        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2023) {
            check = false;
        }

        if (month == 2 && day > 28) {
            check = false;
        }

        if (!check) {
            return "Event for " + event.getOwner().getUsername() + " does not have a valid date.";
        } else {
            Admin.addEvent(event);
            return event.getOwner().getUsername() + " has added new event successfully.";
        }
    }

    @Override
    public String visit (Merch merch) {
        if (Admin.checkExistsMerch(merch.getName(), Admin.getUser(merch.getOwner().getUsername()))) {
            return merch.getOwner().getUsername() + " has merchandise with the same name.";
        } else {
            if (merch.getPrice() < 0) {
                return  "Price for merchandise can not be negative.";
            } else {
                Admin.addMerch(merch);
                return merch.getOwner().getUsername() + " has added new merchandise successfully.";
            }
        }
    }
}
