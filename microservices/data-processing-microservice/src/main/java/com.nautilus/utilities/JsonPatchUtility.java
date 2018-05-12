package com.nautilus.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.dto.PatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JsonPatchUtility {

    private final ObjectMapper mapper;

    public <T> Optional patch(Set<PatchDto> patches, T target)
            throws IOException, JsonPatchException {

        String updateBody = mapper.writeValueAsString(patches);

        JsonNode patchedNode;
        JsonPatch patch = mapper.readValue(updateBody, JsonPatch.class);
        patchedNode = patch.apply(mapper.convertValue(target, JsonNode.class));

        return Optional.ofNullable(mapper.convertValue(patchedNode, target.getClass()));
    }
}
