package wmad.iti.model;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by atef on 5/18/2016.
 */
public class GsonRequest<T> extends Request<T> {
    private  Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;


    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param method detect the method POST or GET
     */
    public GsonRequest(String url, int method,Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     * <p/>
     * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
     *
     * @throws AuthFailureError in the event of auth failure
     */

}