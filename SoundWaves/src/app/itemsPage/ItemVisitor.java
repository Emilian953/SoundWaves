package app.itemsPage;

public interface ItemVisitor {
    String visit(Announcement announcement);
    String visit(Event event);
    String visit(Merch merch);
}
