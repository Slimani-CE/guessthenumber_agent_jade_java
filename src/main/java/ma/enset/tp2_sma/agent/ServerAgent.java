package ma.enset.tp2_sma.agent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class ServerAgent extends GuiAgent {

    private int numberToGuess;
    private boolean isGameEnded = false;
    private AID winner;
    private ArrayList<AID> agents = new ArrayList<>();
    private ServerGUI serverGUI;

    @Override
    protected void setup() {
        // Get the GUI
        serverGUI = (ServerGUI) getArguments()[0];
        serverGUI.setServerAgent(this);
        // Initialize the number to guess
        numberToGuess = (int) (Math.random() * 1000);
        System.out.println("Number to guess: " + numberToGuess);

        // Cyclic behaviour to receive messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    // Check if the msg is a request.
                    // Request message will be used if the agent is new
                    if(msg.getPerformative() == ACLMessage.REQUEST) {
                        System.out.println("Request message received");
                        // Add the agent to the list of agents
                        agents.add(msg.getSender());
                    }else{
                        // Check if the msg content is a number
                        serverGUI.addMessage(msg);
                        String strMessage;
                        ACLMessage response;
                        try {
                            int number = Integer.parseInt(msg.getContent());
                            if (number == numberToGuess) {
                                response = new ACLMessage(ACLMessage.CONFIRM);
                                strMessage = "You win. The number is " + numberToGuess;
                                winner = msg.getSender();
                                isGameEnded = true;
                            } else if (number < numberToGuess) {
                                strMessage = "The number is greater";
                                response = new ACLMessage(ACLMessage.INFORM);
                            } else {
                                strMessage = "The number is smaller";
                                response = new ACLMessage(ACLMessage.INFORM);
                            }
                        } catch (NumberFormatException e) {
                            strMessage = "The message is not a number";
                            response = new ACLMessage(ACLMessage.FAILURE);
                        }
                        // Send the response to the agent
                        response.addReceiver(msg.getSender());
                        response.setContent(strMessage);
                        send(response);
                    }
                    if (isGameEnded) {
                        // Inform all agents that the game is ended
                        System.out.println(winner.getLocalName());
                        for (AID agent : agents) {
                            ACLMessage response = new ACLMessage(ACLMessage.CANCEL);
                            response.addReceiver(agent);
                            if(!agent.getLocalName().equals(winner.getLocalName()))
                                response.setContent("The game is ended. The number was: " + numberToGuess + " The winner is: " + msg.getSender().getLocalName());
                            else
                                response.setContent("The game is ended. The number was: " + numberToGuess + " The winner is: You");
                            send(response);
                        }
                        doDelete();
                    }
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
