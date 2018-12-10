package edu.esipe.i3.ezipflix.frontend.data.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Document(collection = "video_conversions")
public class VideoConversions {
    @Id
    private String uuid;
    private String originPath;
    private String targetPath;

    public VideoConversions() {
    }

    public VideoConversions(String uuid, String originPath, String targetPath) {
        this.uuid = uuid;
        this.originPath = originPath;
        this.targetPath = targetPath;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String toJson() throws JsonProcessingException {
        final ObjectMapper _mapper = new ObjectMapper();
        final Map<String, String> _map = new HashMap<String, String>();
        _map.put("id", uuid);
        _map.put("originPath", originPath);
        _map.put("targetPath", targetPath);
        byte [] _bytes = _mapper.writeValueAsBytes(_map);
        return new String(_bytes);
    }
}
