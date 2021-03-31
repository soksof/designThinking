package gr.uoi.dthink.services;

import gr.uoi.dthink.model.ResourceType;
import gr.uoi.dthink.repos.ResourceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ResourceTypeServiceImpl implements ResourceTypeService{
    ResourceTypeRepository resourceTypeRepository;

    public ResourceTypeServiceImpl(ResourceTypeRepository resourceTypeRepository){
        this.resourceTypeRepository = resourceTypeRepository;
    }

    @Override
    public ResourceType findByType(String type) {
        return resourceTypeRepository.findByType(type);
    }

    @Override
    public List<ResourceType> findAll() {
        return resourceTypeRepository.findAll();
    }

    @Override
    public List<ResourceType> findAllFileTypes() {
        List<ResourceType> fileResourceTypes = new ArrayList<>();
        Stream.of("PDF", "WORD DOCUMENT", "TXT").forEach(type -> {
            fileResourceTypes.add(resourceTypeRepository.findByType(type));
        });
        return fileResourceTypes;
    }
}
