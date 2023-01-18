package de.hsrm.mi.swt02.backend.api.map.service;

import de.hsrm.mi.swt02.backend.api.lobby.repository.LobbyRepository;
import de.hsrm.mi.swt02.backend.api.map.dto.AddMapRequestDTO;
import de.hsrm.mi.swt02.backend.api.map.repository.MapRepository;
import de.hsrm.mi.swt02.backend.api.player.repository.PlayerRepository;
import de.hsrm.mi.swt02.backend.domain.map.Map;
import de.hsrm.mi.swt02.backend.domain.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MapServiceImpl implements MapService {

    @Autowired
    MapRepository mapRepository;

    @Autowired
    LobbyRepository lobbyRepository;

    @Autowired
    PlayerRepository playerRepository;

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
        if (dto.mapOwnerId() != 0L) {
            Optional<Player> playerOptional = playerRepository.findById(dto.mapOwnerId());
            if (playerOptional.isPresent()) {
                map.setMapOwner(playerOptional.get());
            } else
                log.info("Player not found: "+ dto.mapOwnerId());
        }
        map = mapRepository.save(map);

        return map.getId();
    }

    @Override
    @Transactional
    public Map createNewMap() {
        Map map = new Map();

        return mapRepository.save(map);
    }

    /**
     * assign new Lobby to map and cut old relations
     */
    @Override
    @Transactional
    public void assignLobbyToMap(long mapId, long lobbyId) {
        Map map = this.getMapById(mapId);

        lobbyRepository.findById(lobbyId).ifPresent(lobby -> {
            if (lobby.getMap() != null) {
                lobby.getMap().setLobby(null);

            }
            if (map.getLobby() != null) {
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

    /**
     *
     * @param playerId
     * @return
     */
    @Override
    public List<Map> findAllMapsFromPlayer(long playerId) {
        return mapRepository.findAll()
                .stream()
                .filter(map ->
                        map.getMapOwner().getId() == playerId)
                .toList();
    }
}
