package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.BaseAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CheckMailBehaviour extends SimpleBehaviour {

    private static final long serialVersionUID = -3648610114610477617L;
	private int nextBehaviourSelect; //1 = request connection , 2 = send map
    private BaseAgent myagent;

    public CheckMailBehaviour(final BaseAgent myagent) {
        super(myagent);
        this.myagent = myagent;
    }

    @Override
    public void action() {
        final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
        final ACLMessage msg = this.myAgent.receive(msgTemplate);
        if (msg != null) {
            if(msg.getPostTimeStamp() - System.currentTimeMillis() < 50){
                System.out.println(this.myagent.getLocalName() + " : --Result received from " + msg.getSender().getLocalName());
                nextBehaviourSelect = 2;
            }else{
                System.out.println(this.myagent.getLocalName() + " : --Warning message from  "+msg.getSender().getLocalName()+" too old !--");
                nextBehaviourSelect = 1;
            }
        }else{
            nextBehaviourSelect = 1; //no message was found
        }

    }

    @Override
    public boolean done() {
    	myagent.setPreviousBehaviour("CheckMailBehavior");
        return true;
    }

    public int onEnd() {
        return nextBehaviourSelect;

    }
}
