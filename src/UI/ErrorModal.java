package UI;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Padding;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.MouseButton;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.widgets.Clear;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;

public class ErrorModal  {

    private String errorMessage = null;
    private final Button closeButton;
    private Rect modalArea = new Rect(0, 0, 0, 0);

    public ErrorModal() {
        this.closeButton = new Button(" Dismiss ", this::clearError);
    }

    public void showError(String message) {
        this.errorMessage = message;
    }

    public void clearError() {
        this.errorMessage = null;
    }

    public boolean isVisible() {
        return errorMessage != null && !errorMessage.isBlank();
    }

    public boolean handleEvent(Event event) {
        if (!isVisible()) {
            return false;
        }

        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.code() == KeyCode.ENTER) {
                clearError();
                return true;
            }
            closeButton.handleKey(keyEvent);
            // Catch all key events while modal is visible so background inputs don't receive keypresses
            return true;
        } else if (event instanceof MouseEvent mouseEvent) {
            if (
                mouseEvent.button() == MouseButton.LEFT && mouseEvent.isClick()
            ) {
                if (closeButton.isClicked(mouseEvent.x(), mouseEvent.y())) {
                    clearError();
                    return true;
                }
            }
            return true;
        }

        return true;
    }

    public void render(Frame frame, Rect fullArea) {
        if (!isVisible()) {
            return;
        }

        int width = Math.min(50, fullArea.width());
        int height = Math.min(10, fullArea.height());
        int x = fullArea.x() + (fullArea.width() - width) / 2;
        int y = fullArea.y() + (fullArea.height() - height) / 2;

        this.modalArea = new Rect(x, y, width, height);

        // 1. Wipe out existing cells in the target rect so underlying UI text disappears
        frame.renderWidget(Clear.INSTANCE, modalArea);

        // 2. Render container block
        var modalBlock = Block.builder()
            .title(" ERROR ")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.RED))
            .padding(new Padding(1, 2, 1, 2))
            .build();

        frame.renderWidget(modalBlock, modalArea);

        var innerArea = modalBlock.inner(modalArea);
        var rows = Layout.vertical()
            .constraints(new Constraint[] {
                Constraint.fill(1), // Message text area
                Constraint.length(3), // Dismiss Button
            })
            .split(innerArea);

        frame
            .buffer()
            .setString(
                rows.get(0).x(),
                rows.get(0).y(),
                errorMessage,
                Style.EMPTY.fg(Color.RED)
            );

        closeButton.render(frame, rows.get(1), true);
    }
}
