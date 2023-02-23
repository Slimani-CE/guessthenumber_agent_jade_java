package ma.enset.tp2_sma.agent;


import jade.core.AID;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;

public class ClientAgent extends GuiAgent {
    private ClientGUI clientGUI;
    private boolean isGameEnded = false;
    @Override
    protected void setup() {
        // Get the GUI
        clientGUI = (ClientGUI) getArguments()[0];
        clientGUI.setClientAgent(this);
        // Send request to server
        // This request will be used to add this agent to the list of agents in the server
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("Player information");
        msg.addReceiver(new AID("server", AID.ISLOCALNAME));
        send(msg);
        Scanner scanner = new Scanner(System.in);
        while(!isGameEnded){
            // Wait for the response
            ACLMessage response = blockingReceive();
            if(response != null) {
                clientGUI.addMessage(response);
                if (response.getPerformative() == ACLMessage.CANCEL) {
                    isGameEnded = true;
                }
            }
        }
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        String message = (String) guiEvent.getParameter(0);
        sendMessage(message);
    }

    public ACLMessage sendMessage(String message){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(message);
        msg.addReceiver(new AID("server", AID.ISLOCALNAME));
        send(msg);
        clientGUI.addMessage(msg);
        return msg;
    }
}
