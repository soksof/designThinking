package gr.uoi.dthink.services;

import gr.uoi.dthink.model.EmpathyMap;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.repos.EmpathyMapRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpathyMapServiceImpl implements EmpathyMapService{
    EmpathyMapRepository empathyMapRepository;

    public EmpathyMapServiceImpl(EmpathyMapRepository empathyMapRepository){
        this.empathyMapRepository = empathyMapRepository;
    }

    @Override
    public EmpathyMap save(EmpathyMap empMap) {
        return empathyMapRepository.save(empMap);
    }
}
