import UI.CreateResearcher;
import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.text.Text;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.MouseButton;
import dev.tamboui.tui.event.MouseEvent;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.paragraph.Paragraph;
import dev.tamboui.widgets.tabs.Tabs;
import dev.tamboui.widgets.tabs.TabsState;
import factories.DbFactory;

public class Main {

    private static final TabsState tabsState = new TabsState();
    private static final String[] TAB_TITLES = {
        "Create Researcher",
        "Create Equipment",
        "Create Booking",
        "View All",
    };
    private static final String DIVIDER = " | ";

    public static void main(String[] args) throws Exception {
        DbFactory.getFactory();
        CreateResearcher resPage = new CreateResearcher();

        tabsState.select(0);

        var config = TuiConfig.builder().mouseCapture(true).build();

        try (var tui = TuiRunner.create(config)) {
            tui.run(
                (event, runner) -> {
                    if (event instanceof KeyEvent k) {
                        if (k.code() == KeyCode.ESCAPE) {
                            runner.quit();
                            return true;
                        }

                        if (k.code() == KeyCode.TAB) {
                            tabsState.select(
                                (tabsState.selected() + 1) % TAB_TITLES.length
                            );
                            return true;
                        }
                    } else if (event instanceof MouseEvent m) {
                        if (m.button() == MouseButton.LEFT && m.isClick()) {
                            // The navigation block occupies row 0 to 2 (height 3)
                            if (m.y() >= 0 && m.y() < 3) {
                                int x = m.x();
                                int currentX = 1; // 1-char left border padding

                                for (int i = 0; i < TAB_TITLES.length; i++) {
                                    int titleWidth = TAB_TITLES[i].length();
                                    int tabEnd = currentX + titleWidth;

                                    if (x >= currentX && x < tabEnd) {
                                        tabsState.select(i);
                                        return true;
                                    }

                                    currentX = tabEnd + DIVIDER.length();
                                }
                            }
                        }
                    }

                    if (tabsState.selected() == 0) {
                        return resPage.handleEvent(event);
                    }

                    return false;
                },
                frame -> {
                    var area = frame.area();

                    var chunks = Layout.vertical()
                        .constraints(new Constraint[] {
                            Constraint.length(3),
                            Constraint.fill(1),
                        })
                        .split(area);

                    Tabs tabs = Tabs.builder()
                        .titles(TAB_TITLES)
                        .highlightStyle(Style.EMPTY.bold().fg(Color.CYAN))
                        .divider(DIVIDER)
                        .block(
                            Block.builder()
                                .title(" Navigation (F1-F4 / Tab) ")
                                .borders(Borders.ALL)
                                .build()
                        )
                        .build();

                    tabs.render(chunks.get(0), frame.buffer(), tabsState);

                    var contentArea = chunks.get(1);

                    if (tabsState.selected() == 0) {
                        resPage.render(frame, contentArea);
                    } else if (tabsState.selected() == 1) {
                        var page = Paragraph.builder()
                            .text(Text.from("Create Equipment View"))
                            .block(
                                Block.builder()
                                    .title("Create Equipment")
                                    .borders(Borders.ALL)
                                    .build()
                            )
                            .build();
                        frame.renderWidget(page, contentArea);
                    } else if (tabsState.selected() == 2) {
                        var page = Paragraph.builder()
                            .text(Text.from("Create Booking View"))
                            .block(
                                Block.builder()
                                    .title("Create Booking")
                                    .borders(Borders.ALL)
                                    .build()
                            )
                            .build();
                        frame.renderWidget(page, contentArea);
                    } else if (tabsState.selected() == 3) {
                        var page = Paragraph.builder()
                            .text(Text.from("View All Records"))
                            .block(
                                Block.builder()
                                    .title("View All")
                                    .borders(Borders.ALL)
                                    .build()
                            )
                            .build();
                        frame.renderWidget(page, contentArea);
                    }
                }
            );
        } finally {
            DbFactory.getFactory().close();
        }
    }
}
