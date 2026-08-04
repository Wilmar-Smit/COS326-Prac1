import UI.CreateResearcher;
import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import factories.DbFactory;

public class Main {

    public static void main(String[] args) throws Exception {
        DbFactory.getFactory();
        CreateResearcher resPage = new CreateResearcher();

        var config = TuiConfig.builder().mouseCapture(true).build();

        try (var tui = TuiRunner.create(config)) {
            tui.run(
                (event, runner) -> {
                    if (
                        event instanceof KeyEvent k &&
                        k.code() == KeyCode.ESCAPE
                    ) {
                        runner.quit();
                        return true;
                    }

                    return resPage.handleEvent(event);
                },
                frame -> {
                    resPage.render(frame);
                }
            );
        } finally {
            DbFactory.getFactory().close();
        }
    }
}
