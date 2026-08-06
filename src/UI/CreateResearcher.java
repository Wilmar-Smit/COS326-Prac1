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
import entities.Researcher;
import factories.ResearcherManager;
import factories.ResearcherManager.ResearcherWithBookings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateResearcher {

    private Rect area;
    private final CheckboxState checkboxState = new CheckboxState();
    private final Checkbox checkbox = Checkbox.builder().build();
    private boolean orderByNumBookings = false;

    private void onCheckboxToggle() {
        if (checkboxState.isChecked()) {
            orderByNumBookings = true;
        } else {
            orderByNumBookings = false;
        }
        refreshTableData();
    }

    private final ErrorModal errorModal = new ErrorModal();
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
    private final InputBox filterID = new InputBox(" Search by ID ", "ID...");

    private final Button createResearcher = new Button(
        " Create new researcher ",
        this::CreateResearcherFunction
    );
    private final Button createNewResearcherBtn = new Button(
        " Clear and create new researcher ",
        this::CreateNewResearcher
    );
    private final Button deleteResearcherBtn = new Button(
        " Delete researcher ",
        this::deleteResearcher
    );

    private final TableView<Researcher> researcherTable;
    private final TableView<ResearcherWithBookings> researcherBookingsTable;

    private final List<UI> widgets = new ArrayList<>();
    private int focusedIndex = 0;
    private Rect tableArea = new Rect(0, 0, 0, 0);

    public CreateResearcher() {
        res = new Researcher();

        researcherTable = new TableView<Researcher>("Existing Researchers")
            .addColumn("ID", Constraint.length(8), r ->
                String.valueOf(r.getResearchId())
            )
            .addColumn(
                "Name",
                Constraint.percentage(30),
                Researcher::getFullName
            )
            .addColumn("Email", Constraint.percentage(35), Researcher::getEmail)
            .addColumn(
                "Department",
                Constraint.fill(),
                Researcher::getDepartment
            );

        researcherBookingsTable = new TableView<ResearcherWithBookings>(
            "Researchers with Bookings"
        )
            .addColumn("Name", Constraint.percentage(30), rwb ->
                rwb.researcher.getFullName()
            )
            .addColumn("Email", Constraint.percentage(35), rwb ->
                rwb.researcher.getEmail()
            )
            .addColumn("Bookings", Constraint.fill(), rwb ->
                String.valueOf(rwb.bookingCount)
            );

        widgets.add(nameInput);
        widgets.add(emailInput);
        widgets.add(departmentInput);
        widgets.add(filterID);
        widgets.add(createResearcher);
        widgets.add(createNewResearcherBtn);
        widgets.add(deleteResearcherBtn);

        refreshTableData();
    }

    private final Constraint[] rowConstraints = new Constraint[] {
        Constraint.length(3), // 0: Name Input
        Constraint.length(3), // 1: Email Input
        Constraint.length(3), // 2: Department Input
        Constraint.length(3), // 3: Filter ID Input
        Constraint.length(3), // 4: Create / Update Button
        Constraint.length(3), // 5: Clear Button
        Constraint.length(3), // 6: Delete Button
        Constraint.length(3), // 7: Checkbox
        Constraint.length(10), // 8: Table View (researcherTable)
        Constraint.length(10), // 9: Table View (researcherBookingsTable)
    };

    public void refreshTableData() {
        ResearcherManager man = new ResearcherManager();
        String filterText =
            filterID.getValue() != null ? filterID.getValue().trim() : "";

        if (!filterText.isEmpty()) {
            try {
                Long searchId = Long.parseLong(filterText);
                Researcher match = man.SearchResearcher(searchId);
                researcherTable.setItems(
                    match != null ? List.of(match) : Collections.emptyList()
                );
            } catch (NumberFormatException e) {
                researcherTable.setItems(Collections.emptyList());
            }
        } else {
            researcherTable.setItems(man.findAll());
        }

        List<ResearcherWithBookings> withBookings = man.findAllResearchers(
            this.orderByNumBookings
        );
        researcherBookingsTable.setItems(withBookings);
    }

    public void deleteResearcher() {
        if (res != null && res.getResearchId() != null) {
            ResearcherManager man = new ResearcherManager();
            man.delete(res);
            CreateNewResearcher();
            refreshTableData();
        }
    }

    private void populateFieldsFromSelectedRow() {
        Researcher selected = researcherTable.getSelectedItem();
        if (selected != null) {
            this.res = selected;
            nameInput.setValue(
                selected.getFullName() != null ? selected.getFullName() : ""
            );
            emailInput.setValue(
                selected.getEmail() != null ? selected.getEmail() : ""
            );
            departmentInput.setValue(
                selected.getDepartment() != null ? selected.getDepartment() : ""
            );
            createResearcher.setLabel(" Update researcher ");
        }
    }

    public void CreateResearcherFunction() {
        boolean dontadd = false;
        ResearcherManager man = new ResearcherManager();

        if (nameInput.getValue().length() > 2) {
            res.setFullName(nameInput.getValue());
        } else {
            errorModal.showError("name too short ");
            dontadd = true;
        }

        if (
            emailInput
                .getValue()
                .matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        ) {
            res.setEmail(emailInput.getValue());
        } else {
            errorModal.showError("email not valid");
            dontadd = true;
        }

        res.setDepartment(departmentInput.getValue());

        if (!dontadd) {
            if (res.getResearchId() == null) {
                man.save(res);
                createResearcher.setLabel(" Update researcher ");
            } else {
                man.update(res);
            }
        }

        refreshTableData();
    }

    public void CreateNewResearcher() {
        nameInput.clear();
        emailInput.clear();
        departmentInput.clear();
        filterID.clear();
        createResearcher.setLabel(" Create new researcher ");
        res = new Researcher();
        refreshTableData();
    }

    public boolean handleEvent(Event event) {
        if (errorModal.isVisible()) return errorModal.handleEvent(event);

        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.code() == KeyCode.DOWN) {
                focusedIndex = (focusedIndex + 1) % (widgets.size() + 1);
                return true;
            } else if (keyEvent.code() == KeyCode.UP) {
                focusedIndex =
                    (focusedIndex + widgets.size()) % (widgets.size() + 1);
                return true;
            }

            if (focusedIndex < widgets.size()) {
                boolean handled = widgets.get(focusedIndex).handleKey(keyEvent);
                if (focusedIndex == 3 && handled) {
                    refreshTableData();
                }
                return handled;
            } else if (focusedIndex == widgets.size()) {
                boolean handled = researcherTable.handleKey(keyEvent);
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

            for (int i = 0; i < widgets.size(); i++) {
                UI w = widgets.get(i);
                if (w.isClicked(mx, my)) {
                    focusedIndex = i;
                    if (w instanceof Button) ((Button) w).click();
                    return true;
                }
            }

            if (this.area != null) {
                var container = Block.builder()
                    .title("Create Researcher")
                    .borders(Borders.ALL)
                    .borderType(BorderType.ROUNDED)
                    .padding(new Padding(1, 2, 1, 2))
                    .build();

                var rows = Layout.vertical()
                    .constraints(rowConstraints)
                    .split(container.inner(this.area));

                Rect checkboxArea = rows.get(7);
                if (checkboxArea.contains(mx, my)) {
                    checkboxState.toggle();
                    onCheckboxToggle();
                    return true;
                }
            }

            if (researcherTable.isClicked(mx, my, tableArea)) {
                focusedIndex = widgets.size();
                int rowIndex = researcherTable.getRowIndexAt(my, tableArea);
                if (rowIndex != -1) populateFieldsFromSelectedRow();
                return true;
            }
        }
        return false;
    }

    public void render(Frame frame, Rect area) {
        this.area = area;

        var container = Block.builder()
            .title("Create Researcher")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(Style.EMPTY.fg(Color.CYAN))
            .padding(new Padding(1, 2, 1, 2))
            .build();

        frame.renderWidget(container, area);

        var rows = Layout.vertical()
            .constraints(rowConstraints)
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        emailInput.render(frame, rows.get(1), focusedIndex == 1);
        departmentInput.render(frame, rows.get(2), focusedIndex == 2);
        filterID.render(frame, rows.get(3), focusedIndex == 3);
        createResearcher.render(frame, rows.get(4), focusedIndex == 4);
        createNewResearcherBtn.render(frame, rows.get(5), focusedIndex == 5);

        var checkboxArea = rows.get(7);
        var checkboxBlock = Block.builder()
            .title(" Order by number of bookings ")
            .borders(Borders.ALL)
            .borderType(BorderType.ROUNDED)
            .borderStyle(
                focusedIndex == 7
                    ? Style.EMPTY.fg(Color.CYAN)
                    : Style.EMPTY.fg(Color.DARK_GRAY)
            )
            .build();

        frame.renderWidget(checkboxBlock, checkboxArea);
        checkbox.render(
            checkboxBlock.inner(checkboxArea),
            frame.buffer(),
            checkboxState
        );

        tableArea = rows.get(8);
        researcherTable.render(
            frame,
            tableArea,
            focusedIndex == widgets.size()
        );

        researcherBookingsTable.render(frame, rows.get(9), false);

        if (errorModal.isVisible()) errorModal.render(frame, area);
    }
}
