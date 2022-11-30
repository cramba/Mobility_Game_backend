package de.hsrm.mi.swt02.backend.api.map.service;

import de.hsrm.mi.swt02.backend.api.map.dto.AddMapRequestDTO;
import de.hsrm.mi.swt02.backend.domain.map.Map;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MapService {

    long saveStreetPlan(AddMapRequestDTO dto);
    Map getStreetPlanById(long id);
    void deleteStreetPlanById(long id);
    List<Map> findAllStreetPlans();

}
