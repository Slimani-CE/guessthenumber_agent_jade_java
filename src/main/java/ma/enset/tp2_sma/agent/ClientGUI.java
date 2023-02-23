package ma.enset.tp2_sma.agent;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientGUI extends Application {

    @FXML
    private Button button;
    @FXML
    private TextField textField;
    @FXML
    private VBox vbox;
    @FXML
    private ScrollPane scrollPane;
    private ClientAgent clientAgent;
    public static void main(String[] args) {
        launch(args);
    }

    private void startContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
        AgentController agentController = agentContainer.createNewAgent("client2", "ma.enset.tp2_sma.agent.ClientAgent", new Object[]{this});
        agentController.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        BorderPane borderPane = FXMLLoader.load(getClass().getResource("/ma/enset/tp2_sma/views/clientView.fxml"));
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setTitle("Guess the number");
        button = (Button) borderPane.lookup("#button");
        textField = (TextField) borderPane.lookup("#textField");
        vbox = (VBox) borderPane.lookup("#vbox");
        scrollPane = (ScrollPane) borderPane.lookup("#scrollPane");
        button.setOnAction(event -> {
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(textField.getText());
            System.out.println(textField.getText());
            clientAgent.onGuiEvent(guiEvent);
        });
    }

    public void addMessage(ACLMessage message) {
        Platform.runLater(() -> {
            Pane pane = null;
            try {
                pane = FXMLLoader.load(getClass().getResource("/ma/enset/tp2_sma/views/messageView.fxml"));
                TextFlow messageFlow = (TextFlow) pane.lookup("#messageFlow");
                ((Text)pane.lookup("#text")).setText(message.getContent());
                if(message.getSender().getLocalName().equals("server") ) {
                    System.out.println("server");
                    if(message.getPerformative() == ACLMessage.INFORM)
                        messageFlow.getStyleClass().add("alert_message");
                    else if(message.getPerformative() == ACLMessage.FAILURE)
                        messageFlow.getStyleClass().add("error");
                    else
                        messageFlow.getStyleClass().add("text_flow_user_agent");
                    System.out.println("user");
                }
                else{
                    pane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    messageFlow.nodeOrientationProperty().set(NodeOrientation.LEFT_TO_RIGHT);
                }
                vbox.getChildren().add(pane);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            scrollPane.layout();
            scrollPane.setVvalue(1);
        });
    }

    // Setter to set the client agent
    public void setClientAgent(ClientAgent clientAgent) {
        this.clientAgent = clientAgent;
    }
}
