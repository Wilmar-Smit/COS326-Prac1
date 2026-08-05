package UI;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Text;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.MouseButton;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.paragraph.Paragraph;
import dev.tamboui.widgets.select.Select;
import dev.tamboui.widgets.select.SelectState;
import entities.Booking;
import entities.Equipment;
import entities.Researcher;
import java.util.ArrayList;
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

    private final TableView<Researcher> researcherTable = new TableView<>(
        "Researchers"
    );
    private final TableView<Equipment> equipmentTable = new TableView<>(
        "Equipment"
    );
    private final TableView<Booking> bookingTable = new TableView<>("Bookings");

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

        // TODO: add table columns for researcher, equipment, booking
        refreshResearcherTable();
        refreshEquipmentTable();
        refreshBookingTable();
    }

    public void refreshResearcherTable() {
        // TODO: implement researcher manager logic
    }

    public void refreshEquipmentTable() {
        // TODO: implement equipment manager logic
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
        researcherNameLabel.setValue("");
        equipmentNameLabel.setValue("");
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
                Constraint.length(3), // researcher filter
                Constraint.length(3), // equipment filter
                Constraint.length(3), // labels
                Constraint.length(3), // buttons
                Constraint.fill(1), // tables
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

        filterResearcher.render(frame, rows.get(5), focusedIndex == 4);
        filterEquipment.render(frame, rows.get(6), focusedIndex == 5);

        researcherNameInput.render(frame, rows.get(7), false);
        equipmentNameInput.render(frame, rows.get(7), false);

        createBookingBtn.render(frame, rows.get(8), focusedIndex == 6);
        clearBookingBtn.render(frame, rows.get(8), focusedIndex == 7);

        var splitTables = Layout.horizontal()
            .constraints(new Constraint[] {
                Constraint.percentage(50),
                Constraint.percentage(50),
            })
            .split(rows.get(9));

        researcherArea = splitTables.get(0);
        equipmentArea = splitTables.get(1);
        researcherTable.render(
            frame,
            researcherArea,
            focusedIndex == widgets.size()
        );
        equipmentTable.render(
            frame,
            equipmentArea,
            focusedIndex == widgets.size()
        );

        bookingArea = rows.get(9);
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
