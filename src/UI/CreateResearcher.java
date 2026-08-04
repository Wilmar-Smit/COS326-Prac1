package UI;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Padding;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.MouseButton;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import entities.Researcher;

public class CreateResearcher {

    private Researcher res;
    private final InputBox nameInput = new InputBox(
        " Name ",
        "Enter researcher name..."
    );
    private final InputBox emailInput = new InputBox(
        " Email ",
        "Enter researcher email..."
    );

    private int focusedIndex = 0;

    public CreateResearcher() {
        res = new Researcher();
    }

    public boolean handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            if (
                keyEvent.code() == KeyCode.TAB ||
                keyEvent.code() == KeyCode.DOWN ||
                keyEvent.code() == KeyCode.UP
            ) {
                focusedIndex = focusedIndex == 0 ? 1 : 0;
                return true;
            }

            if (focusedIndex == 0) {
                return nameInput.handleKey(keyEvent);
            } else {
                return emailInput.handleKey(keyEvent);
            }
        } else if (event instanceof MouseEvent mouseEvent) {
            if (
                mouseEvent.button() == MouseButton.LEFT && mouseEvent.isClick()
            ) {
                if (nameInput.isClicked(mouseEvent.x(), mouseEvent.y())) {
                    focusedIndex = 0;
                    return true;
                } else if (
                    emailInput.isClicked(mouseEvent.x(), mouseEvent.y())
                ) {
                    focusedIndex = 1;
                    return true;
                }
            }
        }
        return false;
    }

    public void render(Frame frame) {
        var area = frame.area();

        var container = Block.builder()
            .title("Create Researcher")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.CYAN))
            .padding(new Padding(1, 2, 1, 2))
            .build();

        frame.renderWidget(container, area);

        var rows = Layout.vertical()
            .constraints(new Constraint[] {
                Constraint.length(3),
                Constraint.length(3),
                Constraint.length(3),
                Constraint.fill(1),
            })
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        emailInput.render(frame, rows.get(1), focusedIndex == 1);
        var createResearcher = new Button("Create Researcher", action)
    }

    public String getName() {
        return nameInput.getValue();
    }

    public String getEmail() {
        return emailInput.getValue();
    }
}
