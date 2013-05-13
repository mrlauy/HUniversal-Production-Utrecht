package rexos.mas.hardware_agent.behaviours;

/**
 * @file EvaluateDuration.java
 * @brief Handles the GetServiceStepDuratation Message.
 * @date Created: 12-04-13
 * 
 * @author Thierry Gerritse
 * 
 * @section LICENSE
 *          License: newBSD
 * 
 *          Copyright � 2013, HU University of Applied Sciences Utrecht.
 *          All rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without
 *          modification, are permitted provided that the following conditions
 *          are met:
 *          - Redistributions of source code must retain the above copyright
 *          notice, this list of conditions and the following disclaimer.
 *          - Redistributions in binary form must reproduce the above copyright
 *          notice, this list of conditions and the following disclaimer in the
 *          documentation and/or other materials provided with the distribution.
 *          - Neither the name of the HU University of Applied Sciences Utrecht
 *          nor the names of its contributors may be used to endorse or promote
 *          products derived from this software without specific prior written
 *          permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *          LIMITED TO,
 *          THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *          PARTICULAR PURPOSE
 *          ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED
 *          SCIENCES UTRECHT
 *          BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 *          OR
 *          CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *          SUBSTITUTE
 *          GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *          INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *          STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *          ANY WAY OUT
 *          OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *          SUCH DAMAGE.
 **/

import java.io.IOException;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import org.bson.types.ObjectId;

import rexos.libraries.blackboard_client.GeneralMongoException;
import rexos.libraries.blackboard_client.InvalidDBNamespaceException;
import rexos.mas.behaviours.ReceiveBehaviour;
import rexos.mas.data.ScheduleData;
import rexos.mas.hardware_agent.EquipletStepMessage;
import rexos.mas.hardware_agent.HardwareAgent;
import rexos.mas.hardware_agent.Module;
import rexos.mas.hardware_agent.ModuleFactory;
import rexos.mas.service_agent.ServiceStepMessage;

import com.mongodb.BasicDBObject;

public class EvaluateDuration extends ReceiveBehaviour {
	private static final long serialVersionUID = 1L;

	private static MessageTemplate messageTemplate = MessageTemplate
			.MatchOntology("GetServiceStepDuration");
	private HardwareAgent hardwareAgent;
	private ModuleFactory moduleFactory;

	public EvaluateDuration(Agent a, ModuleFactory moduleFactory) {
		super(a, -1, messageTemplate);
		hardwareAgent = (HardwareAgent) a;
		this.moduleFactory = moduleFactory;
	}

	@Override
	public void handle(ACLMessage message) {
		try {
			ObjectId serviceStepId = (ObjectId) message.getContentObject();
			BasicDBObject dbServiceStep =
					(BasicDBObject) hardwareAgent.getServiceStepsBBClient().findDocumentById(
							serviceStepId);

			ServiceStepMessage serviceStep = new ServiceStepMessage(dbServiceStep);
			System.out.format("%s received message from %s (%s:%s)%n", myAgent.getLocalName(),
					message.getSender().getLocalName(), message.getOntology(), serviceStepId);

			int stepDuration = 0;
			int leadingModule = hardwareAgent.getLeadingModule(serviceStep.getServiceId());
			Module module = moduleFactory.getModuleById(leadingModule);
			EquipletStepMessage[] equipletSteps =
					module.getEquipletSteps(serviceStep.getType(), serviceStep.getParameters());
			for(EquipletStepMessage equipletStep : equipletSteps) {
				stepDuration += equipletStep.getTimeData().getDuration();
			}

			ScheduleData schedule = serviceStep.getScheduleData();
			schedule.setDuration(stepDuration);

			hardwareAgent.getServiceStepsBBClient().updateDocuments(
					new BasicDBObject("_id", serviceStepId),
					new BasicDBObject("$set", new BasicDBObject("scheduleData", schedule
							.toBasicDBObject())));

			ACLMessage reply;
			reply = message.createReply();
			reply.setContentObject(serviceStepId);
			reply.setOntology("GetServiceStepDurationResponse");
			myAgent.send(reply);

		} catch(UnreadableException | InvalidDBNamespaceException | GeneralMongoException
				| IOException e) {
			e.printStackTrace();
			myAgent.doDelete();
		}
	}
}
