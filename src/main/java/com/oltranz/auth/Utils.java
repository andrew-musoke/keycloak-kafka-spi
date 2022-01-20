package com.oltranz.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.json.JSONObject; 

import org.keycloak.events.admin.AdminEvent;
import org.json.simple.parser.*;

public class Utils {
    // When user is created via admin API, format the event and representation into one. 
    // Special case for creating dubai visa user 
    public static String formatRegisterEvent(AdminEvent regEvent){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String eventType = "REGISTER";
        String userInfo = regEvent.getRepresentation();
        JSONParser parser = new JSONParser();
        String userId = regEvent.getResourcePath();
        org.json.simple.JSONObject repObj = null;

      try {
          // parser representation string into json for extraction.
          repObj = (org.json.simple.JSONObject)parser.parse(userInfo);
          System.out.println("representation: "+ repObj);
      }catch(ParseException parseExp) {
            System.out.println("Exception position: " + parseExp.getPosition());
            System.out.println(parseExp);
       }
        
        String registerEventString = new JSONObject()
        .put("clientId", regEvent.getAuthDetails().getClientId())
        .put("ipAddress", regEvent.getAuthDetails().getIpAddress())
            .put("registeredBy", regEvent.getAuthDetails().getUserId())
            // extract userinfo from representation
            .put("userInfo",new JSONObject()
                .put("firstName", repObj.get("firstName"))
                .put("lastName", repObj.get("lastName"))
                .put("email", repObj.get("firstName"))
                .put("username", repObj.get("email"))
                .put("role", repObj.get("groups"))
                .put("attributes", repObj.get("attributes"))
            )
            .put("time", dateFormat.format(new Date(regEvent.getTime())))
            .put("eventType", eventType)
            // remove the 'user/' part of resourcepath
            .put("userId", userId.substring(userId.lastIndexOf('/') + 1))
            .put("operationType", regEvent.getOperationType())
            .put("resourceType", regEvent.getResourceType())
            .toString();
         
        return registerEventString;
	}   

    public static String formatActivitiesObject(String category, String eventtype, String username, String verb, String indirectObject, String directObject, String customerid, String timestamp){
        // Objects.toString(id, "") to return empty string if null
        
        String activitiesEventObject= new JSONObject()
            .put("category" , Objects.toString(category, ""))
            .put("eventType", Objects.toString( eventtype, ""))
            .put("username" , Objects.toString(username, ""))
            .put("verb" , Objects.toString(verb, ""))
            .put("indirectObject" , Objects.toString(indirectObject, ""))
            .put("directObject" , Objects.toString(directObject, ""))
            .put("customerid" , Objects.toString(customerid, ""))
            .put("timestamp", Objects.toString( timestamp, "") )
            .toString(); 
        return activitiesEventObject;
        
    }
    
}