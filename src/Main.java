import UI.CreateBooking;
import UI.CreateEquipment;
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
    };
    private static final String DIVIDER = " | ";

    public static void main(String[] args) throws Exception {
        DbFactory.getFactory();
        CreateResearcher resPage = new CreateResearcher();
        CreateEquipment eqPage = new CreateEquipment();
        CreateBooking bPage = new CreateBooking();
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
                    }

                    if (tabsState.selected() == 0) {
                        return resPage.handleEvent(event);
                    } else if (tabsState.selected() == 1) {
                        return eqPage.handleEvent(event);
                    } else if (tabsState.selected() == 2) {
                        return bPage.handleEvent(event);
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
                                .title(" Navigation (Tab) ")
                                .borders(Borders.ALL)
                                .build()
                        )
                        .build();

                    tabs.render(chunks.get(0), frame.buffer(), tabsState);

                    var contentArea = chunks.get(1);

                    if (tabsState.selected() == 0) {
                        resPage.render(frame, contentArea);
                    } else if (tabsState.selected() == 1) {
                        eqPage.render(frame, contentArea);
                    } else if (tabsState.selected() == 2) {
                        bPage.render(frame, contentArea);
                    }
                }
            );
        } finally {
            DbFactory.getFactory().close();
        }
    }
}
