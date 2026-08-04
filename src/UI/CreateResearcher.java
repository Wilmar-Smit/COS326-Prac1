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
import entities.Researcher;
import factories.ResearcherManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateResearcher {

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

        widgets.add(nameInput);
        widgets.add(emailInput);
        widgets.add(departmentInput);
        widgets.add(filterID);
        widgets.add(createResearcher);
        widgets.add(createNewResearcherBtn);
        widgets.add(deleteResearcherBtn);

        refreshTableData();
    }

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
                if (focusedIndex == 3 && handled) {
                    // filterID refresh
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
            for (int i = 0; i < widgets.size(); i++) {
                UI w = widgets.get(i);
                if (w.isClicked(mouseEvent.x(), mouseEvent.y())) {
                    focusedIndex = i;
                    if (w instanceof Button) ((Button) w).click();
                    return true;
                }
            }

            if (
                researcherTable.isClicked(
                    mouseEvent.x(),
                    mouseEvent.y(),
                    tableArea
                )
            ) {
                focusedIndex = widgets.size();
                int rowIndex = researcherTable.getRowIndexAt(
                    mouseEvent.y(),
                    tableArea
                );
                if (rowIndex != -1) populateFieldsFromSelectedRow();
                return true;
            }
        }
        return false;
    }

    public void render(Frame frame, Rect area) {
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
                Constraint.length(3), // Name Input
                Constraint.length(3), // Email Input
                Constraint.length(3), // Department Input
                Constraint.length(3), // Filter ID Input
                Constraint.length(3), // Create / Update Button
                Constraint.length(3), // Clear Button
                Constraint.length(3), // Delete Button
                Constraint.fill(1), // Table View
            })
            .split(container.inner(area));

        nameInput.render(frame, rows.get(0), focusedIndex == 0);
        emailInput.render(frame, rows.get(1), focusedIndex == 1);
        departmentInput.render(frame, rows.get(2), focusedIndex == 2);
        filterID.render(frame, rows.get(3), focusedIndex == 3);
        createResearcher.render(frame, rows.get(4), focusedIndex == 4);
        createNewResearcherBtn.render(frame, rows.get(5), focusedIndex == 5);
        deleteResearcherBtn.render(frame, rows.get(6), focusedIndex == 6);

        tableArea = rows.get(7);
        researcherTable.render(
            frame,
            tableArea,
            focusedIndex == widgets.size()
        );

        if (errorModal.isVisible()) errorModal.render(frame, area);
    }
}
