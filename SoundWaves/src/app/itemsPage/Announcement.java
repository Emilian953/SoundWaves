package app.itemsPage;

import app.user.User;
import lombok.Getter;

@Getter
public class Announcement extends Item {
    private final String description;

    public Announcement(String name, User owner, String description) {
        super(owner, name);
        this.description = description;
    }

    @Override
    public String accept(ItemVisitor visitor) {
        return visitor.visit(this);
    }
}
