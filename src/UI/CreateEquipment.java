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
import dev.tamboui.widgets.checkbox.Checkbox;
import dev.tamboui.widgets.checkbox.CheckboxState;
import dev.tamboui.widgets.select.Select;
import dev.tamboui.widgets.select.SelectState;
import entities.Equipment;
import factories.EquipmentManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateEquipment {

    private Rect area;

    private final Block container = Block.builder()
        .title("Create Equipment")
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .padding(new Padding(1, 2, 1, 2))
        .build();

    private final Constraint[] rowConstraints = new Constraint[] {
        Constraint.length(3), // Name
        Constraint.length(3), // Category
        Constraint.length(3), // Purchase Date
        Constraint.length(3), // Replacement Cost
        Constraint.length(3), // Status Select
        Constraint.length(3), // Filter ID Input
        Constraint.length(3), // Create / Update Button
        Constraint.length(3), // Clear Button
        Constraint.length(3), // Delete Button
        Constraint.length(3), // Checkbox
        Constraint.fill(1), // Table View
    };

    private final ErrorModal errorModal = new ErrorModal();
    private Equipment equipment;

    private final CheckboxState checkboxState = new CheckboxState();
    private final Checkbox checkbox = Checkbox.builder().build();

    // Function called when checkbox toggled
    private void onCheckboxToggle() {
        if (checkboxState.isChecked()) {
            System.out.println("Checkbox is ON");
        } else {
            System.out.println("Checkbox is OFF");
        }
    }

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

    // Status select
    private final SelectState statusState = new SelectState(
        Equipment.WORKING,
        Equipment.OUT_OF_SERVICE
    );
    private final Select statusSelect = Select.builder().build();

    private final Block statusBlock = Block.builder()
        .title(" Status (Left/Right Arrows) ")
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .build();

    // Buttons
    private final Button createEQ = new Button(
        " Create Equipment ",
        this::createEquipmentFunction
    );
    private final Button createNewEquipmentBtn = new Button(
        " Clear and create new equipment ",
        this::CreateNewEquipment
    );
    private final Button deleteEquipmentBtn = new Button(
        " Delete equipment ",
        this::deleteEquipment
    );

    // Table
    private final TableView<Equipment> equipmentTable;

    private final List<UI> widgets = new ArrayList<>();
    private int focusedIndex = 0;
    private Rect tableArea = new Rect(0, 0, 0, 0);

    public CreateEquipment() {
        equipment = new Equipment();

        equipmentTable = new TableView<Equipment>("Existing Equipment")
            .addColumn("ID", Constraint.length(8), eq ->
                String.valueOf(eq.getId())
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

        widgets.add(nameInput);
        widgets.add(categoryInput);
        widgets.add(purchaseDate);
        widgets.add(replaceCostInput);
        widgets.add(filterID);
        widgets.add(createEQ);
        widgets.add(createNewEquipmentBtn);
        widgets.add(deleteEquipmentBtn);

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
                equipmentTable.setItems(
                    match != null ? List.of(match) : Collections.emptyList()
                );
            } catch (NumberFormatException e) {
                equipmentTable.setItems(Collections.emptyList());
            }
        } else {
            equipmentTable.setItems(manager.findAll());
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

            if (Equipment.OUT_OF_SERVICE.equals(selected.getStatus())) {
                statusState.selectLast();
            } else {
                statusState.selectFirst();
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

    public void render(Frame frame, Rect areaInput) {
        this.area = areaInput;
        frame.renderWidget(container, area);

        var rows = Layout.vertical()
            .constraints(rowConstraints)
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        categoryInput.render(frame, rows.get(1), focusedIndex == 1);
        purchaseDate.render(frame, rows.get(2), focusedIndex == 2);
        replaceCostInput.render(frame, rows.get(3), focusedIndex == 3);

        // Status block render
        frame.renderWidget(statusBlock, rows.get(4));
        statusSelect.render(
            statusBlock.inner(rows.get(4)),
            frame.buffer(),
            statusState
        );

        filterID.render(frame, rows.get(5), focusedIndex == 5);
        createEQ.render(frame, rows.get(6), focusedIndex == 6);
        createNewEquipmentBtn.render(frame, rows.get(7), focusedIndex == 7);
        deleteEquipmentBtn.render(frame, rows.get(8), focusedIndex == 8);

        var checkboxArea = rows.get(9);
        checkbox.render(checkboxArea, frame.buffer(), checkboxState);

        tableArea = rows.get(10);
        equipmentTable.render(frame, tableArea, focusedIndex == widgets.size());

        if (errorModal.isVisible()) errorModal.render(frame, area);
    }

    public boolean handleEvent(Event event) {
        if (errorModal.isVisible()) return errorModal.handleEvent(event);

        if (event instanceof KeyEvent keyEvent) {
            // Navigation
            if (keyEvent.code() == KeyCode.DOWN) {
                focusedIndex = (focusedIndex + 1) % (widgets.size() + 1); // +1 for table
                return true;
            } else if (keyEvent.code() == KeyCode.UP) {
                focusedIndex =
                    (focusedIndex + widgets.size()) % (widgets.size() + 1);
                return true;
            }

            // Delegate to focused widget
            if (focusedIndex < widgets.size()) {
                boolean handled = widgets.get(focusedIndex).handleKey(keyEvent);

                // Status select keyboard arrows
                if (focusedIndex == 4) {
                    if (keyEvent.code() == KeyCode.LEFT) {
                        statusState.selectPrevious();
                        return true;
                    } else if (keyEvent.code() == KeyCode.RIGHT) {
                        statusState.selectNext();
                        return true;
                    }
                }

                // Refresh table when filter changes
                if (focusedIndex == 5 && handled) {
                    refreshTableData();
                }
                return handled;
            } else if (focusedIndex == widgets.size()) {
                boolean handled = equipmentTable.handleKey(keyEvent);
                if (handled) populateFieldsFromSelectedRow();
                return handled;
            }
        } else if (
            event instanceof MouseEvent mouseEvent &&
            mouseEvent.button() == MouseButton.LEFT &&
            mouseEvent.isClick()
        ) {
            int mx = mouseEvent.x();
            int my = mouseEvent.y();

            // Normal widgets
            for (int i = 0; i < widgets.size(); i++) {
                UI w = widgets.get(i);
                if (w.isClicked(mx, my)) {
                    focusedIndex = i;
                    if (w instanceof Button) ((Button) w).click();
                    return true;
                }
            }

            var rows = Layout.vertical()
                .constraints(rowConstraints)
                .split(container.inner(area));
            Rect checkboxArea = rows.get(9);

            if (checkboxArea.contains(mx, my)) {
                checkboxState.toggle();
                onCheckboxToggle();
                return true;
            }

            // Table hit-test
            if (equipmentTable.isClicked(mx, my, tableArea)) {
                focusedIndex = widgets.size();
                int rowIndex = equipmentTable.getRowIndexAt(my, tableArea);
                if (rowIndex != -1) populateFieldsFromSelectedRow();
                return true;
            }
        }

        return false;
    }
}
