package app.itemsPage;

import app.user.User;
import lombok.Getter;

@Getter
public class Event extends Item {
    private final String description;
    private final String date;

    public Event(String name, User owner, String description, String date) {
        super(owner, name);
        this.description = description;
        this.date = date;
    }

    @Override
    public String accept(ItemVisitor visitor) {
        return visitor.visit(this);
    }
}
