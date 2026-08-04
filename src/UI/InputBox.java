package UI;

import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.input.TextInput;
import dev.tamboui.widgets.input.TextInputState;

public class InputBox implements UI{

    private final TextInputState state;
    private final String label;
    private final String placeholder;
    private Rect bounds;

    public void clear() {
        state.clear();
    }

    public void setValue(String val) {
        state.setText(val);
    }

    public InputBox(String label, String placeholder) {
        this.label = label;
        this.placeholder = placeholder;
        this.state = new TextInputState();
    }

    public InputBox(String label, String placeholder, String initialValue) {
        this.label = label;
        this.placeholder = placeholder;
        this.state = new TextInputState(initialValue);
    }

    public boolean handleKey(KeyEvent event) {
        if (event.code() == KeyCode.CHAR) {
            state.insert(event.string());
            return true;
        } else if (event.code() == KeyCode.BACKSPACE) {
            state.deleteBackward();
            return true;
        } else if (event.code() == KeyCode.DELETE) {
            state.deleteForward();
            return true;
        } else if (event.code() == KeyCode.LEFT) {
            state.moveCursorLeft();
            return true;
        } else if (event.code() == KeyCode.RIGHT) {
            state.moveCursorRight();
            return true;
        }
        return false;
    }

    public void render(Frame frame, Rect area, boolean focused) {
        this.bounds = area;
        Style borderStyle = focused
            ? Style.EMPTY.fg(Color.YELLOW)
            : Style.EMPTY.fg(Color.WHITE);

        TextInput textInput = TextInput.builder()
            .placeholder(placeholder)
            .cursorStyle(focused ? Style.EMPTY.reversed() : Style.EMPTY)
            .block(
                Block.builder()
                    .title(label)
                    .borders(Borders.ALL)
                    .borderStyle(borderStyle)
                    .build()
            )
            .build();

        textInput.render(area, frame.buffer(), state);
    }

    public boolean isClicked(int x, int y) {
        return bounds != null && bounds.contains(x, y);
    }

    public String getValue() {
        return state.text();
    }
}
