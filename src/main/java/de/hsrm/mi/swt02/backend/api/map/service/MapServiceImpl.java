package de.hsrm.mi.swt02.backend.api.map.service;

import de.hsrm.mi.swt02.backend.api.lobby.repository.LobbyRepository;
import de.hsrm.mi.swt02.backend.api.map.dto.AddMapRequestDTO;
import de.hsrm.mi.swt02.backend.api.map.repository.MapRepository;
import de.hsrm.mi.swt02.backend.api.player.service.PlayerService;
import de.hsrm.mi.swt02.backend.domain.map.GameAsset;
import de.hsrm.mi.swt02.backend.domain.map.Map;
import de.hsrm.mi.swt02.backend.domain.map.MapObject;
import de.hsrm.mi.swt02.backend.domain.player.Player;
import de.hsrm.mi.swt02.backend.npc.NpcInfo;
import de.hsrm.mi.swt02.backend.npc.NpcVehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class MapServiceImpl implements MapService {

    @Autowired
    MapRepository mapRepository;

    @Autowired
    LobbyRepository lobbyRepository;

    /**
     * save map Plan
     * 
     * @param dto
     * @return id
     */
    @Override
    @Transactional
    public long saveMap(AddMapRequestDTO dto) {
        Map map = new Map(dto.mapName(), dto.creationDate(), dto.sizeX(), dto.sizeY());
        map = mapRepository.save(map);

        return map.getId();
    }

    @Override
    @Transactional
    public Map createNewMap(){
        Map map = new Map();

        return mapRepository.save(map);
    }

    /**
     * assign new Lobby to map and cut old relations
     * 
     */
    @Override
    @Transactional
    public void assignLobbyToMap(long mapId, long lobbyId) {
        Map map =  this.getMapById(mapId);

        lobbyRepository.findById(lobbyId).ifPresent(lobby -> {
            if(lobby.getMap() != null) {
                lobby.getMap().setLobby(null);

            }
            if(map.getLobby() != null) {
                map.getLobby().setMap(null);
            }

            lobby.setMap(map);
            map.setLobby(lobby);
            //map.setMapOwner(lobby.getHost());
            mapRepository.save(map);
        });
    }


   

    /**
     * get map by id
     * 
     * @param id
     * @return map
     */
    @Override
    @Transactional
    public Map getMapById(long id) {
        Optional<Map> mapOpt = mapRepository.findById(id);
        if (mapOpt.isEmpty()) {
            // logger
        }
        return mapOpt.orElseThrow();
    }

    /**
     * delete map by id
     * 
     * @param id
     * @return map
     */
    @Override
    @Transactional
    public void deleteMapById(long id) {
        //Map delMap = this.getMapById(id);
        //Player PlayertoDelMapFrom = delMap.getMapOwner();
        //PlayertoDelMapFrom.removeMapFromMapList(delMap);

        mapRepository.deleteById(id);
    }

    /**
     * get all Maps
     * 
     * @return Maps
     */
    @Override
    @Transactional
    public List<Map> findAllMaps() {

        Optional<List<Map>> allMaps = Optional.of(mapRepository.findAll());

        if (allMaps.isEmpty()) {
            // logger
        }

        return allMaps.get();
    }

    @Override
    @Transactional
    public void saveEditedMap(Map map) {
        mapRepository.save(map);
        
    }

    @Override
    public NpcInfo initNpc(long mapId, long npcId, int npcPosX, int npcPosY, int npcRot) {
        NpcVehicle npc = new NpcVehicle();
        Map map = this.getMapById(mapId);
        List<MapObject> list = map.getMapObjects();
        MapObject nextEle = new MapObject();
        MapObject currentMapObject = list.stream()
        .filter(mapObj -> mapObj.getX() == npcPosX &&  mapObj.getY() == npcPosY)
        .findFirst().get();

        //Todo: neue Car Rotation von Scriptberechnung noch zurückgeben, in AssetArray setzen und im FE ziehen 
        
        npc.setNpcParams(currentMapObject.getX(), currentMapObject.getY(),currentMapObject.getRotation(), npcRot, currentMapObject.getObjectTypeId());
            npc.calcNextMapEle();
            nextEle = list.stream()
                        .filter(mapObj -> mapObj.getX() == npc.retXCoord() &&  mapObj.getY() == npc.retZCoord())
                        .findFirst().get();

        
        
        
        
        return new NpcInfo(nextEle.getObjectTypeId(), nextEle.getX(), nextEle.getY(), nextEle.getRotation(), npc.retCarRot());

        /* 
        int carRot = 0;
        //int newX = 0;
        //int newZ = 0;
        NpcVehicle npc = new NpcVehicle();
        Map map = this.getMapById(id);
        List<MapObject> list = map.getMapObjects();

        //MapObject startEle = new MapObject();
        MapObject currentEle = new MapObject();
        MapObject nextEle = new MapObject();
        
        for (MapObject ele : list){
            if(!ele.getGameAssets().isEmpty()){
                carRot = ele.getGameAssets().get(0).getRotation();
                currentEle = ele;
            }
           
        }


      

        for(int i = 0; i < list.size(); i++){
            npc.setNpcParams(currentEle.getX(), currentEle.getY(),currentEle.getRotation(), carRot, currentEle.getObjectTypeId());
            npc.calcNextMapEle();
            nextEle = list.stream()
                        .filter(mapObj -> mapObj.getX() == npc.retXCoord() &&  mapObj.getY() == npc.retZCoord())
                        .findFirst().get();
            
            currentEle = nextEle;          
            carRot = npc.retCarRot();
            
        }

        */
        
        
    }


}
