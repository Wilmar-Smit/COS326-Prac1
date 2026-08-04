package UI;

import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Text;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.paragraph.Paragraph;

public class Button implements UI {

    private String label;
    private final Runnable action;
    private Rect bounds;

    public void setLabel(String label) {
        this.label = label;
    }

    public Button(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public boolean handleKey(KeyEvent event) {
        if (
            event.code() == KeyCode.ENTER ||
            (event.code() == KeyCode.CHAR && " ".equals(event.string()))
        ) {
            click();
            return true;
        }
        return false;
    }

    public void render(Frame frame, Rect area, boolean focused) {
        this.bounds = area;

        Style borderStyle = focused
            ? Style.EMPTY.fg(Color.YELLOW)
            : Style.EMPTY.fg(Color.WHITE);

        var paragraph = Paragraph.builder()
            .text(Text.from(label))
            .block(
                Block.builder()
                    .borders(Borders.ALL)
                    .borderStyle(borderStyle)
                    .build()
            )
            .build();

        frame.renderWidget(paragraph, area);
    }

    public boolean isClicked(int x, int y) {
        return bounds != null && bounds.contains(x, y);
    }

    public void click() {
        if (action != null) {
            action.run();
        }
    }
}
