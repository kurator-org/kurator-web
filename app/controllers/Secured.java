package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

import java.util.Date;

import static play.mvc.Controller.session;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        // see if user is logged in
        if (session("username") == null)
            return null;

        // see if the session is expired
        String previousTick = session("userTime");
        if (previousTick != null && !previousTick.equals("")) {
            long previousT = Long.valueOf(previousTick);
            long currentT = new Date().getTime();
            long timeout = Long.valueOf(Play.application().configuration().getString("sessionTimeout")) * 1000 * 60;
            if ((currentT - previousT) > timeout) {
                // session expired
                session().clear();
                return null;
            }
        }

        // update time in session
        String tickString = Long.toString(new Date().getTime());
        session("userTime", tickString);

        return session("username");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
}