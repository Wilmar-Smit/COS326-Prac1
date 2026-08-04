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
import factories.ResearcherManager;

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
    private final InputBox departmentInput = new InputBox(
        " Department ",
        "Enter researcher department..."
    );

    private final Button createResearcher = new Button(
        " Create Researcher ",
        () -> CreateResearcherFunction()
    );

    private int focusedIndex = 0;

    public CreateResearcher() {
        res = new Researcher();
    }

    public void CreateResearcherFunction() {
        ResearcherManager man = new ResearcherManager();
        res.setFullName(nameInput.getValue());
        res.setEmail(emailInput.getValue());
        res.setDepartment(departmentInput.getValue());
        man.save(res);
        res = new Researcher();
    }

    public boolean handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            if (
                keyEvent.code() == KeyCode.TAB ||
                keyEvent.code() == KeyCode.DOWN
            ) {
                focusedIndex = (focusedIndex + 1) % 4;
                return true;
            } else if (keyEvent.code() == KeyCode.UP) {
                focusedIndex = (focusedIndex + 3) % 4;
                return true;
            }

            if (focusedIndex == 0) {
                return nameInput.handleKey(keyEvent);
            } else if (focusedIndex == 1) {
                return emailInput.handleKey(keyEvent);
            } else if (focusedIndex == 2) {
                return departmentInput.handleKey(keyEvent);
            } else if (focusedIndex == 3) {
                return createResearcher.handleKey(keyEvent);
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
                } else if (
                    departmentInput.isClicked(mouseEvent.x(), mouseEvent.y())
                ) {
                    focusedIndex = 2;
                    return true;
                } else if (
                    createResearcher.isClicked(mouseEvent.x(), mouseEvent.y())
                ) {
                    focusedIndex = 3;
                    createResearcher.click();
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
                Constraint.length(3),
                Constraint.fill(1),
            })
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        emailInput.render(frame, rows.get(1), focusedIndex == 1);
        departmentInput.render(frame, rows.get(2), focusedIndex == 2);
        createResearcher.render(frame, rows.get(3), focusedIndex == 3);
    }
}
