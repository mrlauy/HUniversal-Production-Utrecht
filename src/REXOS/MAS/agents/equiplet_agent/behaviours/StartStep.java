/**
 * @file src/REXOS/MAS/agents/equiplet_agent/behaviours/StartStep.java
 * @brief Behaviour for handling the messages with the ontology StartStep
 * @date Created: 2013-04-02
 * 
 * @author Hessel Meulenbeld
 * 
 * @section LICENSE
 *          License: newBSD
 * 
 *          Copyright � 2013, HU University of Applied Sciences Utrecht.
 *          All rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 *          the following conditions are met:
 *          - Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *          following disclaimer.
 *          - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *          following disclaimer in the documentation and/or other materials provided with the distribution.
 *          - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be
 *          used to endorse or promote products derived from this software without specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *          THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *          ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *          BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *          CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *          GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *          OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package agents.equiplet_agent.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import libraries.blackboard_client.BlackboardClient;
import libraries.blackboard_client.data_classes.BlackboardSubscriber;
import libraries.blackboard_client.data_classes.FieldUpdateSubscription;
import libraries.blackboard_client.data_classes.GeneralMongoException;
import libraries.blackboard_client.data_classes.InvalidDBNamespaceException;
import libraries.blackboard_client.data_classes.MongoOperation;
import libraries.blackboard_client.data_classes.OplogEntry;
import libraries.blackboard_client.data_classes.FieldUpdateSubscription.MongoUpdateLogOperation;
import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;

import org.bson.types.ObjectId;

import agents.data_classes.EquipletState;
import agents.data_classes.EquipletStateEntry;
import agents.data_classes.StepStatusCode;
import agents.equiplet_agent.EquipletAgent;
import agents.shared_behaviours.ReceiveBehaviour;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Receive behaviour for receiving messages with the ontology: "StartStep".
 * Starts the product step linked to the conversationId.
 * Starts a timer for the next product step.
 */
public class StartStep extends ReceiveBehaviour implements BlackboardSubscriber {
	/**
	 * @var static final long serialVersionUID
	 *      The serial version UID for this class
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @var MessageTemplate MESSAGE_TEMPLATE
	 *      The messageTemplate this behaviour listens to.
	 *      This behaviour listens to the ontology: StartStep.
	 */
	private static MessageTemplate MESSAGE_TEMPLATE = MessageTemplate.MatchOntology("StartStep");

	/**
	 * @var EquipletAgent equipletAgent
	 *      The equipletAgent related to this behaviour.
	 */
	private EquipletAgent equipletAgent;

	private FieldUpdateSubscription stateUpdateSubscription;

	private ObjectId productStepId;

	/**
	 * Instantiates a new can perform step.
	 * 
	 * @param equipletAgent The agent for this behaviour
	 * @param equipletBBClient The BlackboardClient for this equiplet's blackboard.
	 */
	public StartStep(EquipletAgent equipletAgent) {
		super(equipletAgent, MESSAGE_TEMPLATE);
		this.equipletAgent = equipletAgent;
		stateUpdateSubscription = new FieldUpdateSubscription("state", this);
		stateUpdateSubscription.addOperation(MongoUpdateLogOperation.SET);
	}

	/**
	 * Function to handle the incoming messages for this behaviour. Handles the response to the StartStep.
	 * 
	 * @param message The received message.
	 */
	@Override
	public void handle(ACLMessage message) {

		// Gets the productStepId and updates all the productsteps on the blackboard the status to waiting.
		try {
			ObjectId productStepId = equipletAgent.getRelatedObjectId(message.getConversationId());
			if(false){//equipletAgent.getEquipletStateEntry().getEquipletState() != EquipletState.NORMAL) {
				Logger.log(LogLevel.DEBUG, "%d Equiplet Agent-changing state%n", equipletAgent.getCurrentTimeSlot());

				//equipletAgent.getStateBBClient().subscribe(stateUpdateSubscription);
				//equipletAgent.setDesiredEquipletState(EquipletState.NORMAL);
				equipletAgent.getProductStepBBClient().updateDocuments(new BasicDBObject("_id", productStepId),
						new BasicDBObject("$set", new BasicDBObject("status", StepStatusCode.WAITING.name())));
				
				equipletAgent.getTimer().rescheduleTimer();
			} else {
				Logger.log(LogLevel.DEBUG, "%d Equiplet Agent-Starting prod. step.%n", equipletAgent.getCurrentTimeSlot());
				equipletAgent.getProductStepBBClient().updateDocuments(new BasicDBObject("_id", productStepId),
						new BasicDBObject("$set", new BasicDBObject("status", StepStatusCode.WAITING.name())));
				
				equipletAgent.getTimer().rescheduleTimer();
			}
		} catch(InvalidDBNamespaceException | GeneralMongoException e) {
			Logger.log(LogLevel.ERROR, "", e);
			//TODO handle error
			equipletAgent.doDelete();
		}


	}

	@Override
	public void onMessage(MongoOperation operation, OplogEntry entry) {
		try {
			BlackboardClient stateBBClient = equipletAgent.getStateBBClient();
			DBObject dbObject = stateBBClient.findDocumentById(entry.getTargetObjectId());
			if(dbObject != null) {
				EquipletStateEntry state = new EquipletStateEntry((BasicDBObject) dbObject);
				if(state.getEquipletState() == EquipletState.NORMAL) {
					Logger.log(LogLevel.DEBUG, "%d Equiplet Agent-equip. state changed to NORMAL. Starting prod. step.", equipletAgent.getCurrentTimeSlot());

					equipletAgent.getProductStepBBClient().updateDocuments(new BasicDBObject("_id", productStepId),
							new BasicDBObject("$set", new BasicDBObject("status", StepStatusCode.WAITING.name())));

					equipletAgent.getTimer().rescheduleTimer();
					
					stateBBClient.unsubscribe(stateUpdateSubscription);
				}
			}
		} catch(InvalidDBNamespaceException | GeneralMongoException e) {
			Logger.log(LogLevel.ERROR, "", e);
			//TODO handle error
		}
	}
}
