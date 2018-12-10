package edu.esipe.i3.ezipflix.frontend;

import java.net.URI;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
public class ConversionRequest {

    private URI path;

    public ConversionRequest() {
    }
    public ConversionRequest(URI path) {
        this.path = path;
    }
    public URI getPath() {
        return path;
    }
    public void setPath(URI path) {
        this.path = path;
    }
}
