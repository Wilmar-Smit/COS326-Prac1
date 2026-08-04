package UI;

import dev.tamboui.layout.Rect;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tui.event.KeyEvent;

public interface UI {
    boolean handleKey(KeyEvent event);
    boolean isClicked(int x, int y);
    void render(Frame frame, Rect area, boolean focused);
}
