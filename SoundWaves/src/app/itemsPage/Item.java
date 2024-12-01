package app.itemsPage;

import app.user.User;
import lombok.Getter;

@Getter
public abstract class Item {
    private final User owner;
    public final String name;

    public Item(User owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public abstract String accept(ItemVisitor visitor);
}
