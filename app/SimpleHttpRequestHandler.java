import play.api.http.JavaCompatibleHttpRequestHandler;
import play.routing.Router;
import play.api.mvc.Handler;
import play.http.*;
import play.mvc.*;
import play.libs.streams.Accumulator;
import play.core.j.JavaHandler;
import play.core.j.JavaHandlerComponents;

import javax.inject.Inject;

public class SimpleHttpRequestHandler implements HttpRequestHandler {
    private final Router router;
    private final JavaHandlerComponents components;

    @Inject
    public SimpleHttpRequestHandler(Router router, JavaHandlerComponents components) {
        this.router = router;
        this.components = components;
    }

    public HandlerForRequest handlerForRequest(Http.RequestHeader request) {
        Handler handler = router.route(request).orElseGet(() ->
                EssentialAction.of(req -> Accumulator.done(Results.notFound()))
        );

        String path = request.path();
        if (path.equals("/") || path.endsWith("kurator-web")) {
            handler = EssentialAction.of(req -> Accumulator.done(Results.redirect("/kurator-web/")));
        }

        if (handler instanceof JavaHandler) {
            handler = ((JavaHandler)handler).withComponents(components);
        }

        return new HandlerForRequest(request, handler);
    }
}