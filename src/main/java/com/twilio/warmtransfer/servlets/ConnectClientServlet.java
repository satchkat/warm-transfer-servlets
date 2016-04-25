package com.twilio.warmtransfer.servlets;

import com.twilio.sdk.verbs.Conference;
import com.twilio.sdk.verbs.Dial;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.warmtransfer.utils.TwilioAuthenticatedActions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ConnectClientServlet extends HttpServlet {

    private TwilioAuthenticatedActions twilioAuthenticatedActions;

    public ConnectClientServlet(){
        this.twilioAuthenticatedActions = new TwilioAuthenticatedActions();
    }

    public ConnectClientServlet(TwilioAuthenticatedActions twilioAuthenticatedActions){
        this.twilioAuthenticatedActions = twilioAuthenticatedActions;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String conference_id = request.getParameter("CallSid");

        twilioAuthenticatedActions.callAgent("agent1", "callback");
        ActiveCallsService.saveNewConference("agent1", conference_id);

        response.setContentType("text/xml");
        response.getWriter().write(generateConnectConference(conference_id));
    }

    private String generateConnectConference(String conference_id) throws RuntimeException {
        TwiMLResponse twiMLResponse = new TwiMLResponse();
        Dial dialVerb = new Dial();
        Conference conferenceVerb = new Conference(conference_id);
        conferenceVerb.setStartConferenceOnEnter(false);
        conferenceVerb.setEndConferenceOnExit(true);
        conferenceVerb.setWaitUrl("/conference/wait");
        try {
            twiMLResponse.append(dialVerb).append(conferenceVerb);
        } catch (TwiMLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating conference.");
        }
        return twiMLResponse.toEscapedXML();
    }
}
