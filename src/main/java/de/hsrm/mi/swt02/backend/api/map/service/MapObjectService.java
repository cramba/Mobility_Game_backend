package de.hsrm.mi.swt02.backend.api.map.service;

import java.util.List;

import de.hsrm.mi.swt02.backend.api.map.dto.AddMapObjectsRequestDTO;
import de.hsrm.mi.swt02.backend.domain.map.MapObject;

/**
 * Service methods that are  to operate on MapObject database.
 */
public interface MapObjectService {
    List<MapObject> findAllMapObjects();
    MapObject getMapObjectById(long id);
    void deleteMapObjectById(long id);
    Long createMapObject(AddMapObjectsRequestDTO mapObjects, long mapId);
    void deleteAllMapObjectsFromMapById(long id);
}
