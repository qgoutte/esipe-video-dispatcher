package edu.esipe.i3.ezipflix.frontend;

import java.util.UUID;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
public class ConversionResponse {

    final private UUID uuid = UUID.randomUUID();

    public ConversionResponse() {
    }

    public final UUID getUuid() {
        return uuid;
    }
}
