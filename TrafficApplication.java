
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TrafficApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TrafficApplication.class.getResource("traffic_lights.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Traffic control system");
        stage.setScene(scene);
        stage.show();
    }

    static int delay;

    public static void main(String[] args) {
        if(args.length < 1)
            throw new IllegalArgumentException("Supply a delay value");

        delay = Integer.parseInt(args[0]);
        launch();
    }
}
