package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.models.Subject;
import dao.UserDao;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static play.mvc.Http.Context.Implicit.session;

/**
 * Created by lowery on 2/21/17.
 */
public class MyDeadboltHandler extends AbstractDeadboltHandler {
    private final UserDao userDao = new UserDao();

    public MyDeadboltHandler(ExecutionContextProvider ecProvider) {
        super(ecProvider);
    }

    public CompletionStage<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletionStage<Optional<? extends Subject>> getSubject(final Http.Context context) {
        // in a real application, the user name would probably be in the session following a login process
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(userDao.findUserByUsername(session().get("username"))),
                (Executor) executionContextProvider.get());
    }

    @Override
    public CompletionStage<Result> onAuthFailure(final Http.Context context,
                                                 final Optional<String> content) {
        // you can return any result from here - forbidden, etc
        return CompletableFuture.completedFuture(unauthorized());
    }
}
