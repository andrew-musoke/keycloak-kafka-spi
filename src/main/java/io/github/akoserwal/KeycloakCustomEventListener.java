package io.github.akoserwal;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class KeycloakCustomEventListener implements EventListenerProvider {

	@Override
	public void onEvent(Event event) {
			System.out.println("Publishing Login Event:-"+event.getType());
			Producer.publishEvent("login.keycloak.events", event.getUserId());
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
			System.out.println("Publishing Admin Event:-"+adminEvent.getResourceType().name()); 
			Producer.publishEvent("admin.keycloak.events", adminEvent.getAuthDetails().getUserId());
	}

	@Override
	public void close() {

	}
}
