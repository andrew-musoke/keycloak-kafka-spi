package com.oltranz.auth;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject; 

public class KeycloakCustomEventListener implements EventListenerProvider {
	private KeycloakSession session;

    public KeycloakCustomEventListener (KeycloakSession session) {
        this.session = session;
    }


    @Override
	public void onEvent(Event event) {
		System.out.println("Publishing Login Event:-"+event.getType());
		
		// Get realm and user models created at event time.
		RealmModel realm = session.realms().getRealm(event.getRealmId());
		UserModel user = session.users().getUserById(event.getUserId(), realm);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		// build topic name
		String loginTopic =  "login."+ event.getRealmId() +"-realm.keycloak.events";
		
		// build log
		String userLogString = new JSONObject()
					.put( "time", event.getTime()) 
					.put( "eventType", event.getType())  
					.put( "clientId", event.getClientId())  
					.put( "userId", event.getUserId())  
					.put( "ipAddress", event.getIpAddress())  
					.put( "details",new JSONObject(event.getDetails()))
					.put( "customerInfo", new JSONObject()
						.put("userCreationTime", dateFormat.format(new Date(user.getCreatedTimestamp())))
						.put("attributes", new JSONObject(user.getAttributes()))
						.put("serviceAccount", user.getServiceAccountClientLink())
						.put("emailVerified", user.isEmailVerified())
					)
					.put( "error", event.getError())
					.toString();

		// publish to kafka
		Producer.publishEvent(loginTopic, userLogString);
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		System.out.println("Publishing Admin Event:-"+adminEvent.getResourceType().name()); 

		// build topic name
		String adminTopic = "admin."+ adminEvent.getAuthDetails().getRealmId() + "-realm.keycloak.events";

		//build log
		String adminLogString = new JSONObject()
					.put("time", dateFormat.format(new Date(adminEvent.getTime())))
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

		// publish to kafka
		Producer.publishEvent(adminTopic, adminLogString);
	}

	@Override
	public void close() {

	}
}
