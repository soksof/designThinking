package gr.uoi.dthink.services;

import gr.uoi.dthink.model.ResourceType;

import java.util.List;

public interface ResourceTypeService {
    ResourceType findByType(String type);
    List<ResourceType> findAll();
    List<ResourceType> findAllFileTypes();
}
