package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class RequestConnectionBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = -4031957810709753691L;
	private BaseAgent myagent;

    public RequestConnectionBehaviour(final BaseAgent myagent) {
        super(myagent);
        this.myagent = myagent;
    }

    @Override
    public void action() {
        //System.out.println(this.myAgent.getLocalName() + " : is requesting communication channel");
        ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL); //acceptation de la proposition de communication
        msg.setSender(this.myagent.getAID());
        msg.setContent("Starting Communication");
        for (String agentName : this.myagent.getList_agentNames()) {
			msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
		}
        ((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
    }

    @Override
    public boolean done() {
        myagent.setPreviousBehaviour("RequestConnectionBehaviour");
        return true;
    }

    @Override
    public int onEnd() {
        return 1;
    }
}
