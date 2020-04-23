package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Stage;
import gr.uoi.dthink.repos.StageRepository;
import org.springframework.stereotype.Service;

@Service
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;

    public StageServiceImpl(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public Stage save(Stage stage) {
        return stageRepository.save(stage);
    }
}
