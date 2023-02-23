package ma.enset.tp2_sma.agent;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;

public class ServerGUI extends Application {

    private VBox vbox;
    private ScrollPane scrollPane;
    private ServerAgent serverAgent;

    public static void main(String[] args) throws ControllerException {
        launch(args);
    }

    private void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
        AgentController agentController = agentContainer.createNewAgent("server", "ma.enset.tp2_sma.agent.ServerAgent", new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        BorderPane borderPane = FXMLLoader.load(getClass().getResource("/ma/enset/tp2_sma/views/serverView.fxml"));
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.show();
        scrollPane = (ScrollPane) borderPane.lookup("#scrollPane");
        vbox = (VBox)(((Label) borderPane.lookup("#VBoxPane")).getParent());
    }

    public void addMessage(ACLMessage message) {
        Platform.runLater(() -> {
            try {
                Pane pane = FXMLLoader.load(getClass().getResource("/ma/enset/tp2_sma/views/messageView.fxml"));
                TextFlow messageFlow = (TextFlow) pane.lookup("#messageFlow");
                ((Text)pane.lookup("#text")).setText(message.getSender().getLocalName() + " : " + message.getContent());
                // Check if an client agent that sends the message and not the server agent
                if(message.getSender().getLocalName().equals("server")){
                    // Change the node orientation to right to left
                    pane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    messageFlow.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
                }
                else{
                    // add client agent style class
                    messageFlow.getStyleClass().add("text_flow_user_agent");
                }
                vbox.getChildren().add(pane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // setter to set the server agent
    public void setServerAgent(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }
}
