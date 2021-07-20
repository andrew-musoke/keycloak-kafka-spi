package io.github.akoserwal;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.json.JSONObject; 

public class KeycloakCustomEventListener implements EventListenerProvider {

	@Override
	public void onEvent(Event event) {
			System.out.println("Publishing Login Event:-"+event.getType());
			String loginTopic = "login."+ event.getRealmId() +"-realm.keycloak.events";
			// build log
			String userLogString = new JSONObject()
						.put( "time", event.getTime()) 
						.put( "eventType", event.getType())  
						.put( "clientId", event.getClientId())  
						.put( "userId", event.getUserId())  
						.put( "ipAddress", event.getIpAddress())  
						.put( "details",new JSONObject(event.getDetails()))
						.put( "error", event.getError())
						.toString();

			Producer.publishEvent(loginTopic, userLogString);
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
			System.out.println("Publishing Admin Event:-"+adminEvent.getResourceType().name()); 
			String adminTopic = "admin."+ adminEvent.getAuthDetails().getRealmId() + "-realm.keycloak.events";
			//build log
			String adminLogString = new JSONObject()
						.put("time", adminEvent.getTime())
						.put("operationType", adminEvent.getOperationType())
						.put("resourceType", adminEvent.getResourceType())
						.put("details",  new JSONObject()
								.put("clientID", adminEvent.getAuthDetails().getClientId())
								.put("ipAddress", adminEvent.getAuthDetails().getIpAddress())
								.put("realmId", adminEvent.getAuthDetails().getRealmId())
								.put("userId", adminEvent.getAuthDetails().getUserId())
							)
						.put( "error", adminEvent.getError())
						.toString();		

			Producer.publishEvent(adminTopic, adminLogString);
	}

	@Override
	public void close() {

	}
}
