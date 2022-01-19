package com.oltranz.auth;

import org.keycloak.models.UserModel;
import java.text.SimpleDateFormat;
import org.keycloak.events.Event;
import java.util.Date;
import java.util.Objects;

public class ActivitiesSourcing {
    public ActivitiesSourcing() {
    }

    public void sourceActivities(Event event, UserModel user, SimpleDateFormat dateFormat) {
        final String eventType = event.getType().toString();
        String activityObject = "";
        String activityTopic = "";
        String verb = "";
        String indirectObject = "";
        String directObject = "";
        String username = "";
        final String category = "User Management";
        
        if (event.getDetails() != null) {
            username = event.getDetails().get("username");
        }
        final String timestamp = dateFormat.format(new Date(event.getTime()));
        final String customerId = event.getUserId();
       
        switch (eventType) {
            case "LOGIN":
                System.out.println("Structuring Activity:- " + eventType);

                verb = "logged-in";

                activityObject = Utils.formatActivitiesObject(category, eventType, username, verb, indirectObject,
                        directObject, customerId, timestamp);
                break;

            case "REGISTER":
                System.out.println("Structuring Activity:- " + eventType);

                verb = "registered";

                activityObject = Utils.formatActivitiesObject(category, eventType, username, verb, indirectObject,
                        directObject, customerId, timestamp);
                break;

            case "UPDATE_PASSWORD":
                System.out.println("Structuring Activity:- " + eventType);

                verb = "updated";
                directObject = "password";

                activityObject = Utils.formatActivitiesObject(category, eventType, username, verb, indirectObject,
                        directObject, customerId, timestamp);
                break;

            // clientid is always account.
            case "UPDATE_PROFILE":
                System.out.println("Structuring Activity:- " + eventType);

                verb = "updated";
                directObject = "profile";

                activityObject = Utils.formatActivitiesObject(category, eventType, username, verb, indirectObject,
                        directObject, customerId, timestamp);
                break;

            default:
                System.out.println("Unknown Activity:- " + eventType);

        }

        // Determine topic based on UI client performing actions.
        String clientId = Objects.toString(event.getClientId(), "");
        switch (clientId) {
            case "basesms":
                activityTopic = System.getenv("BASESMS_ACTIVITY_TOPIC"); 
                publishToKafka(activityObject, activityTopic);
                break;
            case "baseussd":
                activityTopic = System.getenv("BASEUSSD_ACTIVITY_TOPIC");
                publishToKafka(activityObject, activityTopic);
                break;
            case "account":
                // if clientId is account, push activity to all domains.
                activityTopic = System.getenv("BASESMS_ACTIVITY_TOPIC");
                publishToKafka(activityObject, activityTopic);

                activityTopic = System.getenv("BASEUSSD_ACTIVITY_TOPIC");
                publishToKafka(activityObject, activityTopic);
                break;
            default:
                System.out.println("Unknown clientId:- " + clientId);

        }

    }

    private void publishToKafka(String activityObject, String activityTopic) {
        if (activityTopic != "" && activityObject != "") {
            System.out.println("Publishing Activity to :- " + activityTopic);
            System.out.println("Publishing Activity object :- " + activityObject);

            Producer.publishEvent(activityTopic, activityObject);
        } else {
            System.err.println("ActivityTopic or Activity Object empty.");
        }
    }
}
