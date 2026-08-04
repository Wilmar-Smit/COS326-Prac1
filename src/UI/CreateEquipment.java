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
import java.util.Collections;
import java.util.List;

public class CreateEquipment {

    private final ErrorModal errorModal = new ErrorModal();
    private Equipment equipment;

    // Inputs
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
    private final InputBox filterID = new InputBox(" Search by ID ", "ID...");

    // Select Status
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

    // Buttons
    private final Button createEQ = new Button(" Create Equipment ", () ->
        createEquipmentFunction()
    );

    private final Button createNewEquipmentBtn = new Button(
        " Clear and create new equipment ",
        () -> CreateNewEquipment()
    );

    private final Button deleteEquipmentBtn = new Button(
        " Delete equipment ",
        () -> deleteEquipment()
    );

    // Table
    private final TableView<Equipment> equipmentTable;

    private int focusedIndex = 0;
    private Rect tableArea = new Rect(0, 0, 0, 0);

    public CreateEquipment() {
        equipment = new Equipment();

        equipmentTable = new TableView<Equipment>("Existing Equipment")
            .addColumn("ID", Constraint.length(8), e ->
                String.valueOf(e.getId())
            )
            .addColumn("Name", Constraint.percentage(30), Equipment::getName)
            .addColumn(
                "Category",
                Constraint.percentage(25),
                Equipment::getCategory
            )
            .addColumn(
                "Date",
                Constraint.percentage(20),
                Equipment::getPurchaseDate
            )
            .addColumn("Status", Constraint.fill(), Equipment::getStatus);

        refreshTableData();
    }

    public void refreshTableData() {
        EquipmentManager manager = new EquipmentManager();
        String filterText =
            filterID.getValue() != null ? filterID.getValue().trim() : "";

        if (!filterText.isEmpty()) {
            try {
                Long searchId = Long.parseLong(filterText);

                Equipment match = manager.searchEquipment(searchId);

                if (match != null) {
                    equipmentTable.setItems(List.of(match));
                } else {
                    equipmentTable.setItems(Collections.emptyList());
                }
            } catch (NumberFormatException e) {
                equipmentTable.setItems(Collections.emptyList());
            }
        } else {
            List<Equipment> equipmentList = manager.findAll();
            equipmentTable.setItems(equipmentList);
        }
    }

    public void deleteEquipment() {
        if (equipment != null && equipment.getId() != null) {
            EquipmentManager manager = new EquipmentManager();
            manager.delete(equipment);

            CreateNewEquipment();
            refreshTableData();
        }
    }

    private void populateFieldsFromSelectedRow() {
        Equipment selected = equipmentTable.getSelectedItem();
        if (selected != null) {
            this.equipment = selected;
            nameInput.setValue(
                selected.getName() != null ? selected.getName() : ""
            );
            categoryInput.setValue(
                selected.getCategory() != null ? selected.getCategory() : ""
            );
            purchaseDate.setValue(
                selected.getPurchaseDate() != null
                    ? selected.getPurchaseDate()
                    : ""
            );
            replaceCostInput.setValue(
                String.valueOf(selected.getReplaceCost())
            );

            // Sync status selection if needed
            if (Equipment.OUT_OF_SERVICE.equals(selected.getStatus())) {
                statusState.selectNext();
            } else {
                statusState.selectPrevious();
            }

            createEQ.setLabel(" Update equipment ");
        }
    }

    private void createEquipmentFunction() {
        boolean dontadd = false;
        EquipmentManager manager = new EquipmentManager();

        if (nameInput.getValue().length() > 2) {
            equipment.setName(nameInput.getValue());
        } else {
            errorModal.showError("name too short");
            dontadd = true;
        }

        equipment.setCategory(categoryInput.getValue());
        if (purchaseDate.getValue().matches("\\d{4}/\\d{2}/\\d{2}")) {
            equipment.setPurchaseDate(purchaseDate.getValue());
        } else {
            errorModal.showError("Date format must be yyyy/mm/dd");
            dontadd = true;
        }

        try {
            double cost = Double.parseDouble(replaceCostInput.getValue());
            equipment.setReplaceCost(cost);
        } catch (NumberFormatException e) {
            equipment.setReplaceCost(0.0);
        }

        equipment.setStatus(statusState.selectedValue());

        if (!dontadd) {
            if (equipment.getId() == null) {
                manager.save(equipment);
                createEQ.setLabel(" Update equipment ");
            } else {
                manager.update(equipment);
            }
        }

        refreshTableData();
    }

    public void CreateNewEquipment() {
        nameInput.clear();
        categoryInput.clear();
        purchaseDate.clear();
        replaceCostInput.clear();
        filterID.clear();
        createEQ.setLabel(" Create equipment ");
        equipment = new Equipment();
        refreshTableData();
    }

    public boolean handleEvent(Event event) {
        if (errorModal.isVisible()) {
            return errorModal.handleEvent(event);
        }

        if (event instanceof KeyEvent keyEvent) {
            if (focusedIndex == 9) {
                if (keyEvent.code() == KeyCode.TAB) {
                    focusedIndex = 0;
                    return true;
                }
                boolean handled = equipmentTable.handleEvent(event);
                if (handled) {
                    populateFieldsFromSelectedRow();
                    return true;
                }
            }

            if (keyEvent.code() == KeyCode.DOWN) {
                focusedIndex = (focusedIndex + 1) % 10;
                if (focusedIndex == 9) {
                    populateFieldsFromSelectedRow();
                }
                return true;
            } else if (keyEvent.code() == KeyCode.UP) {
                focusedIndex = (focusedIndex + 9) % 10;
                if (focusedIndex == 9) {
                    populateFieldsFromSelectedRow();
                }
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
                boolean handled = filterID.handleKey(keyEvent);
                if (handled) {
                    refreshTableData();
                }
                return handled;
            } else if (focusedIndex == 6) {
                return createEQ.handleKey(keyEvent);
            } else if (focusedIndex == 7) {
                return createNewEquipmentBtn.handleKey(keyEvent);
            } else if (focusedIndex == 8) {
                return deleteEquipmentBtn.handleKey(keyEvent);
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
                } else if (filterID.isClicked(mx, my)) {
                    focusedIndex = 5;
                    return true;
                } else if (createEQ.isClicked(mx, my)) {
                    focusedIndex = 6;
                    createEQ.click();
                    return true;
                } else if (createNewEquipmentBtn.isClicked(mx, my)) {
                    focusedIndex = 7;
                    createNewEquipmentBtn.click();
                    return true;
                } else if (deleteEquipmentBtn.isClicked(mx, my)) {
                    focusedIndex = 8;
                    deleteEquipmentBtn.click();
                    return true;
                } else if (equipmentTable.isClicked(mx, my, tableArea)) {
                    focusedIndex = 9;
                    int rowIndex = equipmentTable.getRowIndexAt(my, tableArea);
                    if (rowIndex != -1) {
                        populateFieldsFromSelectedRow();
                    }
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
                Constraint.length(3), // Filter ID Input
                Constraint.length(3), // Create / Update Button
                Constraint.length(3), // Clear Button
                Constraint.length(3), // Delete Button
                Constraint.fill(1), // Table View
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

        filterID.render(frame, rows.get(5), focusedIndex == 5);
        createEQ.render(frame, rows.get(6), focusedIndex == 6);
        createNewEquipmentBtn.render(frame, rows.get(7), focusedIndex == 7);
        deleteEquipmentBtn.render(frame, rows.get(8), focusedIndex == 8);

        tableArea = rows.get(9);
        equipmentTable.render(frame, tableArea, focusedIndex == 9);

        if (errorModal.isVisible()) errorModal.render(frame, area);
    }
}
