package app.itemsPage;

import app.user.User;
import lombok.Getter;
@Getter
public class Merch extends Item {
    private final String description;
    private final Integer price;

    public Merch(String name, User owner, String description, Integer price) {
        super(owner, name);
        this.description = description;
        this.price = price;
    }

    @Override
    public String accept(ItemVisitor visitor) {
        return visitor.visit(this);
    }
}
