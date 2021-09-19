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

	public KeycloakCustomEventListener(KeycloakSession session) {
		this.session = session;
	}

	@Override
	public void onEvent(Event event) {
		System.out.println("Publishing Login Event:-" + event.getType());

		// Get realm and user models created at event time.
		RealmModel realm = session.realms().getRealm(event.getRealmId());
		UserModel user = null;
		if (event.getUserId() != null) {
			user = session.users().getUserById(event.getUserId(), realm);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		// build topic name
		String loginTopic = "login." + event.getRealmId() + "-realm.keycloak.events";

		// build log
		JSONObject userLogString = new JSONObject()
				.put("time", dateFormat.format(new Date(event.getTime())))
				.put("eventType", event.getType())
				.put("clientId", event.getClientId())
				.put("userId", event.getUserId())
				.put("ipAddress", event.getIpAddress())
				.put("details", new JSONObject(event.getDetails()))
				.put("error", event.getError());
		if (user != null){
		userLogString.put("customerInfo", new JSONObject()
					.put("userCreationTime", dateFormat.format(new Date(user.getCreatedTimestamp())))
					.put("attributes", new JSONObject(user.getAttributes()))
					.put("serviceAccount", user.getServiceAccountClientLink())
					.put("emailVerified", user.isEmailVerified()));
		}
		// publish to kafka
		Producer.publishEvent(loginTopic, userLogString.toString());
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean respresentationExists) {
		String adminLogString = "{}";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		System.out.println("Publishing Admin Event:-" + adminEvent.getResourceType().name());

		// build topic name
		String adminTopic = "admin." + adminEvent.getAuthDetails().getRealmId() + "-realm.keycloak.events";

		// If the admin event has a representation i.e. is a CREATE or UPDATE then do
		// the expensive string comparisons.
		if (respresentationExists) {
			System.out.println("Op type:-" + adminEvent.getOperationType().toString());

			// Do special formating if the event is a USER CREATION.
			if (adminEvent.getResourceTypeAsString().equals("USER")
					&& adminEvent.getOperationType().toString().equals("CREATE")) {
				adminLogString = Utils.formatRegisterEvent(adminEvent);
				// publish to kafka
				Producer.publishEvent(adminTopic, adminLogString);
				return;
			}
		}
		// build log
		adminLogString = new JSONObject().put("time", dateFormat.format(new Date(adminEvent.getTime())))
				.put("operationType", adminEvent.getOperationType()).put("resourceType", adminEvent.getResourceType())
				.put("details",
						new JSONObject().put("clientID", adminEvent.getAuthDetails().getClientId())
								.put("ipAddress", adminEvent.getAuthDetails().getIpAddress())
								.put("realmId", adminEvent.getAuthDetails().getRealmId())
								.put("userId", adminEvent.getAuthDetails().getUserId()))
				.put("error", adminEvent.getError()).toString();
		// publish to kafka
		Producer.publishEvent(adminTopic, adminLogString);
		return;
	}

	@Override
	public void close() {

	}
}
