/** Secured.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import play.Play;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

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
        return redirect(routes.Users.login());
    }
}