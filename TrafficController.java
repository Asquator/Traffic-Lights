
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static TrafficApplication.delay;

public class TrafficController {

    /*
    Four traffic lights, four elements in each
     */
    @FXML
    private Rectangle bottomPedestrianGreen;

    @FXML
    private Rectangle bottomPedestrianRed;

    @FXML
    private Circle bottomTrafficGreen;

    @FXML
    private Rectangle leftPedestrianGreen;

    @FXML
    private Rectangle leftPedestrianRed;

    @FXML
    private Circle leftTrafficGreen;

    @FXML
    private Circle leftTrafficRed;

    @FXML
    private Rectangle rightPedestrianGreen;

    @FXML
    private Rectangle rightPedestrianRed;

    @FXML
    private Circle rightTrafficGreen;

    @FXML
    private Circle rightTrafficRed;

    @FXML
    private Rectangle topPedestrianGreen;

    @FXML
    private Rectangle topPedestrianRed;

    @FXML
    private Circle topTrafficGreen;

    @FXML
    private Circle topTrafficRed;

    @FXML
    private Circle bottomTrafficRed;


    public void initialize(){

        //Constructing 4 traffic lights
        //The top is the primary and others synchronize with it
        TrafficLight top = new TrafficLight(topTrafficRed, topTrafficGreen, topPedestrianRed, topPedestrianGreen);

        TrafficLight bottom = new TrafficLight(bottomTrafficRed, bottomTrafficGreen, bottomPedestrianRed, bottomPedestrianGreen,
                top, LightScheduler.SyncMode.SYNCHRONOUS);

        TrafficLight left = new TrafficLight(leftTrafficRed, leftTrafficGreen, leftPedestrianRed, leftPedestrianGreen,
                top, LightScheduler.SyncMode.OPPOSITE);

        TrafficLight right = new TrafficLight(rightTrafficRed, rightTrafficGreen, rightPedestrianRed, rightPedestrianGreen,
                top, LightScheduler.SyncMode.OPPOSITE);


        top.setDelay(delay);

        //Starting the traffic lights in separate threads

        Thread topT = new Thread(top);
        Thread bottomT = new Thread(bottom);
        Thread rightT = new Thread(right);
        Thread leftT = new Thread(left);

        topT.start();
        bottomT.start();
        rightT.start();
        leftT.start();

    }


}
