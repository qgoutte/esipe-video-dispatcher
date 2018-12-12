package edu.esipe.i3.ezipflix.frontend.data.repositories;

import edu.esipe.i3.ezipflix.frontend.data.entities.VideoConversions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Repository
public interface VideoConversionRepository /*extends MongoRepository<VideoConversions, UUID>*/ {
}
