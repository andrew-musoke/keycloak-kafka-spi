package io.github.akoserwal;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.json.JSONObject; 

public class KeycloakCustomEventListener implements EventListenerProvider {

	@Override
	public void onEvent(Event event) {
			System.out.println("Publishing Login Event:-"+event.getType());
			// build log
			String userLogString = new JSONObject()
						.put( "time", event.getTime()) 
						.put( "eventType", event.getType())  
						.put( "clientId", event.getClientId())  
						.put( "userId", event.getUserId())  
						.put( "ipAddress", event.getIpAddress())  
						.put( "details", event.getDetails())
						.put( "error", event.getError())
						.toString();

			Producer.publishEvent("login.keycloak.events", userLogString);
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
			System.out.println("Publishing Admin Event:-"+adminEvent.getResourceType().name()); 

			//build log
			String adminLogString = new JSONObject()
						.put("time", adminEvent.getTime())
						.put("operationType", adminEvent.getOperationType())
						.put("resourceType", adminEvent.getResourceType())
						.put("details", adminEvent.getAuthDetails())
						.put( "error", adminEvent.getError())
						.toString();		

			Producer.publishEvent("admin.keycloak.events", adminLogString);
	}

	@Override
	public void close() {

	}
}
