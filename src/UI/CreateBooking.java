package UI;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
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
import dev.tamboui.widgets.scrollbar.Scrollbar;
import dev.tamboui.widgets.scrollbar.ScrollbarOrientation;
import dev.tamboui.widgets.scrollbar.ScrollbarState;
import dev.tamboui.widgets.select.Select;
import dev.tamboui.widgets.select.SelectState;
import entities.Booking;
import entities.Equipment;
import entities.Researcher;
import factories.EquipmentManager;
import factories.ResearcherManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateBooking {

    private Rect area;
    private final Block container = Block.builder()
        .title("Create Booking")
        .borders(Borders.ALL)
        .borderType(BorderType.ROUNDED)
        .borderStyle(Style.EMPTY.fg(Color.CYAN))
        .build();

    private final ErrorModal errorModal = new ErrorModal();

    private final InputBox dateInput = new InputBox(" Date ", "yyyy/mm/dd");
    private final InputBox startTimeInput = new InputBox(
        " Start Time ",
        "hh:mm"
    );
    private final InputBox endTimeInput = new InputBox(" End Time ", "hh:mm");
    private final InputBox purposeInput = new InputBox(
        " Purpose ",
        "Enter purpose..."
    );

    private final SelectState statusState = new SelectState(
        "Active",
        "Finished"
    );
    private final Select statusSelect = Select.builder().build();

    private final InputBox filterResearcher = new InputBox(
        " Search Researcher ID ",
        "ID..."
    );
    private final InputBox filterEquipment = new InputBox(
        " Search Equipment ID ",
        "ID..."
    );

    private final InputBox researcherNameInput = new InputBox(
        " Researcher ",
        ""
    );
    private final InputBox equipmentNameInput = new InputBox(" Equipment ", "");

    private final TableView<Researcher> researcherTable =
        new TableView<Researcher>("Researchers")
            .addColumn("ID", Constraint.length(8), r ->
                String.valueOf(r.getResearchId())
            )
            .addColumn(
                "Name",
                Constraint.percentage(30),
                Researcher::getFullName
            );

    private final TableView<Equipment> equipmentTable =
        new TableView<Equipment>("Existing Equipment")
            .addColumn("ID", Constraint.length(8), eq ->
                String.valueOf(eq.getId())
            )
            .addColumn("Name", Constraint.percentage(30), Equipment::getName)
            .addColumn("Status", Constraint.fill(), Equipment::getStatus);

    private final TableView<Booking> bookingTable = new TableView<Booking>(
        "Bookings"
    )
        .addColumn("ID", Constraint.length(8), b -> String.valueOf(b.getId()))
        .addColumn("Date", Constraint.length(12), Booking::getDate)
        .addColumn("Start", Constraint.length(8), Booking::getStartTime)
        .addColumn("End", Constraint.length(8), Booking::getEndTime)
        .addColumn("Purpose", Constraint.percentage(30), Booking::getPurpose)
        .addColumn("Status", Constraint.length(10), Booking::getStatus)
        .addColumn("Researcher", Constraint.percentage(20), b ->
            b.getBookedBy() != null ? b.getBookedBy().getFullName() : ""
        )
        .addColumn("Equipment", Constraint.percentage(20), b ->
            b.getBookedEQ() != null ? b.getBookedEQ().getName() : ""
        );

    private final Button createBookingBtn = new Button(
        " Create Booking ",
        this::createBookingFunction
    );
    private final Button clearBookingBtn = new Button(
        " Clear Booking ",
        this::clearBooking
    );

    private final List<UI> widgets = new ArrayList<>();
    private int focusedIndex = 0;
    private Rect researcherArea, equipmentArea, bookingArea;

    public CreateBooking() {
        widgets.add(dateInput);
        widgets.add(startTimeInput);
        widgets.add(endTimeInput);
        widgets.add(purposeInput);
        widgets.add(filterResearcher);
        widgets.add(filterEquipment);
        widgets.add(createBookingBtn);
        widgets.add(clearBookingBtn);

        refreshResearcherTable();
        refreshEquipmentTable();
        refreshBookingTable();
    }

    public void refreshResearcherTable() {
        ResearcherManager man = new ResearcherManager();
        String filterText =
            filterResearcher.getValue() != null
                ? filterResearcher.getValue().trim()
                : "";

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

    public void refreshEquipmentTable() {
        EquipmentManager manager = new EquipmentManager();
        String filterText =
            filterEquipment.getValue() != null
                ? filterEquipment.getValue().trim()
                : "";

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
            equipmentTable.setItems(manager.findAllEQ(false));
        }
    }

    public void refreshBookingTable() {
        // TODO: implement booking manager logic
    }

    private void createBookingFunction() {
        boolean invalid = false;

        if (!dateInput.getValue().matches("\\d{4}/\\d{2}/\\d{2}")) {
            errorModal.showError("Date format must be yyyy/mm/dd");
            invalid = true;
        }
        if (!startTimeInput.getValue().matches("\\d{2}:\\d{2}")) {
            errorModal.showError("Start time format must be hh:mm");
            invalid = true;
        }
        if (!endTimeInput.getValue().matches("\\d{2}:\\d{2}")) {
            errorModal.showError("End time format must be hh:mm");
            invalid = true;
        }

        if (!invalid) {
            // TODO: implement booking creation logic
        }
        refreshBookingTable();
    }

    private void clearBooking() {
        dateInput.clear();
        startTimeInput.clear();
        endTimeInput.clear();
        purposeInput.clear();
        statusState.selectFirst();
        researcherNameInput.clear();
        equipmentNameInput.clear();
        // TODO: reset booking object
    }

    public void render(Frame frame, Rect areaInput) {
        this.area = areaInput;
        frame.renderWidget(container, area);

        var rows = Layout.vertical()
            .constraints(new Constraint[] {
                Constraint.length(3), // date
                Constraint.length(3), // start time
                Constraint.length(3), // end time
                Constraint.length(3), // purpose
                Constraint.length(3), // status
                Constraint.length(3), // researcher name
                Constraint.length(3), // equipment name
                Constraint.length(3), // create booking button
                Constraint.length(3), // clear booking button
                Constraint.length(3), // researcher filter
                Constraint.length(5), // researcher table
                Constraint.length(3), // equipment filter
                Constraint.length(5), // equipment table
                Constraint.fill(1), // booking table
            })
            .split(container.inner(area));

        dateInput.render(frame, rows.get(0), focusedIndex == 0);
        startTimeInput.render(frame, rows.get(1), focusedIndex == 1);
        endTimeInput.render(frame, rows.get(2), focusedIndex == 2);
        purposeInput.render(frame, rows.get(3), focusedIndex == 3);

        frame.renderWidget(
            Block.builder()
                .title(" Status ")
                .borders(Borders.ALL)
                .borderType(BorderType.ROUNDED)
                .build(),
            rows.get(4)
        );
        statusSelect.render(rows.get(4), frame.buffer(), statusState);

        researcherNameInput.render(frame, rows.get(5), false);
        equipmentNameInput.render(frame, rows.get(6), false);

        createBookingBtn.render(frame, rows.get(7), focusedIndex == 6);
        clearBookingBtn.render(frame, rows.get(8), focusedIndex == 7);

        filterResearcher.render(frame, rows.get(9), focusedIndex == 4);
        researcherArea = rows.get(10);
        researcherTable.render(
            frame,
            researcherArea,
            focusedIndex == widgets.size()
        );

        filterEquipment.render(frame, rows.get(11), focusedIndex == 5);
        equipmentArea = rows.get(12);
        equipmentTable.render(
            frame,
            equipmentArea,
            focusedIndex == widgets.size()
        );

        bookingArea = rows.get(13);
        bookingTable.render(frame, bookingArea, focusedIndex == widgets.size());

        if (errorModal.isVisible()) errorModal.render(frame, area);
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
                if (focusedIndex == 4) {
                    if (keyEvent.code() == KeyCode.LEFT) {
                        statusState.selectPrevious();
                        return true;
                    } else if (keyEvent.code() == KeyCode.RIGHT) {
                        statusState.selectNext();
                        return true;
                    }
                    refreshResearcherTable();
                    return true;
                }
                if (focusedIndex == 5) {
                    refreshEquipmentTable();
                    return true;
                }
                return handled;
            } else {
                return false;
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

            if (researcherTable.isClicked(mx, my, researcherArea)) {
                focusedIndex = widgets.size();
                int rowIndex = researcherTable.getRowIndexAt(
                    my,
                    researcherArea
                );
                if (rowIndex != -1) {
                    Researcher selected = researcherTable.getSelectedItem();
                    if (selected != null) researcherNameInput.setValue(
                        selected.getFullName()
                    );
                }
                return true;
            }

            if (equipmentTable.isClicked(mx, my, equipmentArea)) {
                focusedIndex = widgets.size();
                int rowIndex = equipmentTable.getRowIndexAt(my, equipmentArea);
                if (rowIndex != -1) {
                    Equipment selected = equipmentTable.getSelectedItem();
                    if (selected != null) equipmentNameInput.setValue(
                        selected.getName()
                    );
                }
                return true;
            }

            if (bookingTable.isClicked(mx, my, bookingArea)) {
                focusedIndex = widgets.size();
                int rowIndex = bookingTable.getRowIndexAt(my, bookingArea);
                if (rowIndex != -1) {
                    Booking selected = bookingTable.getSelectedItem();
                    if (selected != null) {
                        dateInput.setValue(selected.getDate());
                        startTimeInput.setValue(selected.getStartTime());
                        endTimeInput.setValue(selected.getEndTime());
                        purposeInput.setValue(selected.getPurpose());
                        if ("Finished".equals(selected.getStatus())) {
                            statusState.selectLast();
                        } else {
                            statusState.selectFirst();
                        }
                        researcherNameInput.setValue(
                            selected.getBookedBy().getFullName()
                        );
                        equipmentNameInput.setValue(
                            selected.getBookedEQ().getName()
                        );
                    }
                }
                return true;
            }
        }

        return false;
    }
}
