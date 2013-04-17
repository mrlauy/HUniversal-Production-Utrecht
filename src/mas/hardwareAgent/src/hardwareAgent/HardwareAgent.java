package hardwareAgent;

/**
 * Author: Hessel Meulenbeld & Thierry Gerritse
 * Class: HardwareAgent.java 
 **/

import java.util.HashMap;

import hardwareAgent.behaviours.*;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import jade.core.Agent;
import newDataClasses.DbData;
import nl.hu.client.BasicOperationSubscription;
import nl.hu.client.BlackboardClient;
import nl.hu.client.BlackboardSubscriber;
import nl.hu.client.GeneralMongoException;
import nl.hu.client.InvalidDBNamespaceException;
import nl.hu.client.MongoOperation;
import nl.hu.client.OplogEntry;

public class HardwareAgent extends Agent implements BlackboardSubscriber {
	private static final long serialVersionUID = 1L;

	private BlackboardClient serviceStepBBClient, equipletStepBBClient;
	private DbData dbData;
	private HashMap<String,Module> ModulesMap;
		
	public void RegisterModule(String name,Module module){
		
		this.ModulesMap.put(name, module);
		
	}
	
	public Module GetModuleByName(String name)throws Exception{
		
		if (this.ModulesMap.get(name)!= null){			
			return this.ModulesMap.get(name);
		}
		else{
			throw new Exception("No Stepleader difined for this stepType");
		}
	}
	
	public void setup() {
		System.out.println("Hardware agent "+ this +" reporting.");

		// TODO fill in host, database and collection
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			dbData = (DbData) args[0];
		}

		try {
			serviceStepBBClient = new BlackboardClient(dbData.ip);
			serviceStepBBClient.setDatabase(dbData.name);
			serviceStepBBClient.setCollection("ServiceStepsBlackBoard");
			serviceStepBBClient.subscribe(new BasicOperationSubscription(MongoOperation.INSERT, this));
			serviceStepBBClient.subscribe(new BasicOperationSubscription(MongoOperation.UPDATE, this));
			
			equipletStepBBClient = new BlackboardClient(dbData.ip);
			equipletStepBBClient.setDatabase(dbData.name);
			equipletStepBBClient.setCollection("EquipletStepsBlackBoard");
			equipletStepBBClient.subscribe(new BasicOperationSubscription(MongoOperation.UPDATE, this));
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
		
		EvaluateDuration evaluateDurationBehaviour = new EvaluateDuration(this);
		addBehaviour(evaluateDurationBehaviour);
		
		FillPlaceholders fillPlaceholdersBehaviour = new FillPlaceholders(this);
		addBehaviour(fillPlaceholdersBehaviour);
		
		CheckForModules checkForModules = new CheckForModules(this);
		addBehaviour(checkForModules);
		
		///Register modules
		
		/// modulefactory aan het werk gezet
		
		
		// for now: use precompiled grippermodule class
		GripperModule gp = new GripperModule();
		RegisterModule("gripper", gp);
		///
		
		
	}

	public void takeDown() {
		// TODO implement graceful death
	}

	public BlackboardClient getServiceStepsBBClient(){
		return serviceStepBBClient;
	}
	
	
	@Override
	public void onMessage(MongoOperation operation, OplogEntry entry) {
		switch (entry.getNamespace().split(".")[1]) {
		case "ServiceStepsBlackboard":
			switch (operation) {
			case INSERT:
						
				break;
			default:
				break;
			}
			break;
		case "EquipletStepsBlackboard":
			switch(operation){
			case UPDATE:
				try {
					ObjectId id = entry.getTargetObjectId();
					BasicDBObject query = new BasicDBObject();
					query.put("_id", id);
					DBObject equipletStep = equipletStepBBClient.findDocuments(query).get(0);
				} catch (InvalidDBNamespaceException | GeneralMongoException e) {
					// TODO Error no document
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			break;
		}
	}
}
