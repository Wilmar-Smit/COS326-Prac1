import dev.tamboui.text.Text;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.widgets.paragraph.Paragraph;

public class Main {

    public static void main(String[] args) throws Exception {
        try (var tui = TuiRunner.create()) {
            tui.run(
                (event, runner) ->
                    switch (event) {
                        case KeyEvent k when k.isQuit() -> {
                            runner.quit();
                            yield false;
                        }
                        default -> false;
                    },
                frame -> {
                    var paragraph = Paragraph.builder()
                        .text(Text.from("Hello, TamboUI! Press 'q' to quit."))
                        .build();
                    frame.renderWidget(paragraph, frame.area());
                }
            );
        }
    }
}
