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
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.select.Select;
import dev.tamboui.widgets.select.SelectState;
import entities.Equipment;
import factories.EquipmentManager;

public class CreateEquipment {

    private Equipment equipment;

    private int focusedIndex = 0;

    private final InputBox nameInput = new InputBox(
        " Equipment Name ",
        "Enter Equipment name..."
    );
    private final InputBox categoryInput = new InputBox(
        " Category ",
        "Enter Category..."
    );
    private final InputBox purchaseDate = new InputBox(
        " Date ",
        "Enter Date yyyy/mm/dd..."
    );
    private final InputBox replaceCostInput = new InputBox(
        " Replacement Cost ",
        "0.00"
    );

    private final SelectState statusState = new SelectState(
        Equipment.WORKING,
        Equipment.OUT_OF_SERVICE
    );

    private final Select statusSelect = Select.builder()
        .leftIndicator("< ")
        .rightIndicator(" >")
        .selectedColor(Color.CYAN)
        .indicatorColor(Color.DARK_GRAY)
        .build();

    private final Button createEQ = new Button(" Create Equipment ", () ->
        createEquipmentFunction()
    );

    public CreateEquipment() {
        equipment = new Equipment();
    }

    private void createEquipmentFunction() {
        EquipmentManager manager = new EquipmentManager();
        equipment.setName(nameInput.getValue());
        equipment.setCategory(categoryInput.getValue());
        equipment.setPurchaseDate(purchaseDate.getValue());

        try {
            double cost = Double.parseDouble(replaceCostInput.getValue());
            equipment.setReplaceCost(cost);
        } catch (NumberFormatException e) {
            equipment.setReplaceCost(0.0);
        }

        equipment.setStatus(statusState.selectedValue());
        manager.save(equipment);

        // Reset form
        equipment = new Equipment();
    }

    public boolean handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.code() == KeyCode.DOWN) {
                focusedIndex = (focusedIndex + 1) % 6;
                return true;
            } else if (keyEvent.code() == KeyCode.UP) {
                focusedIndex = (focusedIndex + 5) % 6;
                return true;
            }

            if (focusedIndex == 0) {
                return nameInput.handleKey(keyEvent);
            } else if (focusedIndex == 1) {
                return categoryInput.handleKey(keyEvent);
            } else if (focusedIndex == 2) {
                return purchaseDate.handleKey(keyEvent);
            } else if (focusedIndex == 3) {
                return replaceCostInput.handleKey(keyEvent);
            } else if (focusedIndex == 4) {
                if (keyEvent.code() == KeyCode.LEFT) {
                    statusState.selectPrevious();
                    return true;
                } else if (keyEvent.code() == KeyCode.RIGHT) {
                    statusState.selectNext();
                    return true;
                }
            } else if (focusedIndex == 5) {
                return createEQ.handleKey(keyEvent);
            }
        } else if (event instanceof MouseEvent mouseEvent) {
            if (
                mouseEvent.button() == MouseButton.LEFT && mouseEvent.isClick()
            ) {
                int mx = mouseEvent.x();
                int my = mouseEvent.y();

                if (nameInput.isClicked(mx, my)) {
                    focusedIndex = 0;
                    return true;
                } else if (categoryInput.isClicked(mx, my)) {
                    focusedIndex = 1;
                    return true;
                } else if (purchaseDate.isClicked(mx, my)) {
                    focusedIndex = 2;
                    return true;
                } else if (replaceCostInput.isClicked(mx, my)) {
                    focusedIndex = 3;
                    return true;
                } else if (createEQ.isClicked(mx, my)) {
                    focusedIndex = 5;
                    createEQ.click();
                    return true;
                }
            }
        }
        return false;
    }

    public void render(Frame frame, Rect area) {
        var container = Block.builder()
            .title("Create Equipment")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.CYAN))
            .padding(new Padding(1, 2, 1, 2))
            .build();

        frame.renderWidget(container, area);

        var rows = Layout.vertical()
            .constraints(new Constraint[] {
                Constraint.length(3), // Name
                Constraint.length(3), // Category
                Constraint.length(3), // Purchase Date
                Constraint.length(3), // Replacement Cost
                Constraint.length(3), // Status Select
                Constraint.length(3), // Submit Button
                Constraint.fill(1),
            })
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        categoryInput.render(frame, rows.get(1), focusedIndex == 1);
        purchaseDate.render(frame, rows.get(2), focusedIndex == 2);
        replaceCostInput.render(frame, rows.get(3), focusedIndex == 3);

        var statusArea = rows.get(4);
        var statusBlock = Block.builder()
            .title(" Status (Left/Right Arrows) ")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(
                focusedIndex == 4
                    ? Style.EMPTY.fg(Color.CYAN)
                    : Style.EMPTY.fg(Color.DARK_GRAY)
            )
            .build();

        frame.renderWidget(statusBlock, statusArea);
        statusSelect.render(
            statusBlock.inner(statusArea),
            frame.buffer(),
            statusState
        );

        createEQ.render(frame, rows.get(5), focusedIndex == 5);
    }
}
