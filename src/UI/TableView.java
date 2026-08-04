package UI;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.tui.event.Event;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.table.Row;
import dev.tamboui.widgets.table.Table;
import dev.tamboui.widgets.table.TableState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableView<T> implements UI {

    public static class Column<T> {

        private final String header;
        private final Constraint width;
        private final Function<T, String> valueExtractor;

        public Column(
            String header,
            Constraint width,
            Function<T, String> valueExtractor
        ) {
            this.header = header;
            this.width = width;
            this.valueExtractor = valueExtractor;
        }
    }

    @Override
    public boolean isClicked(int x, int y) {
        return false;
    }

    private final String title;
    private final List<Column<T>> columns = new ArrayList<>();
    private final TableState state = new TableState();
    private List<T> items = new ArrayList<>();

    public TableView(String title) {
        this.title = title;
        this.state.select(0);
    }

    public TableView<T> addColumn(
        String header,
        Constraint width,
        Function<T, String> valueExtractor
    ) {
        columns.add(new Column<>(header, width, valueExtractor));
        return this;
    }

    public void setItems(List<T> items) {
        this.items = items != null ? items : new ArrayList<>();
        Integer current = state.selected();
        if (this.items.isEmpty()) {
            state.select(-1);
        } else if (current == null || current < 0) {
            state.select(0);
        } else if (current >= this.items.size()) {
            state.select(this.items.size() - 1);
        }
    }

    public T getSelectedItem() {
        Integer selected = state.selected();
        if (selected != null && selected >= 0 && selected < items.size()) {
            return items.get(selected);
        }
        return null;
    }

    public boolean isClicked(int mouseX, int mouseY, Rect layoutArea) {
        return (
            mouseX >= layoutArea.x() &&
            mouseX < layoutArea.x() + layoutArea.width() &&
            mouseY >= layoutArea.y() &&
            mouseY < layoutArea.y() + layoutArea.height()
        );
    }

    public int getRowIndexAt(int mouseY, Rect layoutArea) {
        // Table block border (1) + Header row (1) = 2 row offset from top of layoutArea
        int clickedRow = mouseY - (layoutArea.y() + 2);
        if (clickedRow >= 0 && clickedRow < items.size()) {
            state.select(clickedRow);
            return clickedRow;
        }
        return -1;
    }

    @Override
    public boolean handleKey(KeyEvent event) {
        if (items.isEmpty()) return false;

        Integer currentSelected = state.selected();
        int current =
            currentSelected == null || currentSelected < 0
                ? 0
                : currentSelected;

        if (event.code() == KeyCode.DOWN) {
            int next = Math.min(current + 1, items.size() - 1);
            state.select(next);
            return true;
        } else if (event.code() == KeyCode.UP) {
            int prev = Math.max(current - 1, 0);
            state.select(prev);
            return true;
        }
        return false;
    }

    public boolean handleEvent(Event event) {
        if (event instanceof KeyEvent keyEvent) {
            return handleKey(keyEvent);
        }
        return false;
    }

    public void render(Frame frame, Rect area, boolean isFocused) {
        String[] headers = columns
            .stream()
            .map(c -> c.header)
            .toArray(String[]::new);

        List<Row> rows = new ArrayList<>();
        for (T item : items) {
            String[] cells = columns
                .stream()
                .map(c -> c.valueExtractor.apply(item))
                .toArray(String[]::new);
            rows.add(Row.from(cells));
        }

        Constraint[] widths = columns
            .stream()
            .map(c -> c.width)
            .toArray(Constraint[]::new);

        Table table = Table.builder()
            .header(Row.from(headers))
            .rows(rows.toArray(new Row[0]))
            .widths(widths)
            .highlightStyle(
                isFocused
                    ? Style.EMPTY.bg(Color.CYAN).fg(Color.BLACK)
                    : Style.EMPTY.bg(Color.DARK_GRAY)
            )
            .block(
                Block.builder()
                    .title(" " + title + " ")
                    .borders(Borders.ALL)
                    .borderType(BorderType.ROUNDED)
                    .borderStyle(
                        isFocused
                            ? Style.EMPTY.fg(Color.CYAN)
                            : Style.EMPTY.fg(Color.DARK_GRAY)
                    )
                    .build()
            )
            .build();

        table.render(area, frame.buffer(), state);
    }
}
