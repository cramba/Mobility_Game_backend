package de.hsrm.mi.swt02.backend.domain.map;

import de.hsrm.mi.swt02.backend.domain.game.position.MapObjectPosition;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Getter
@Setter
public class MapObject {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private long version;

    private long objectTypeId;
    private int x;
    private int y;

    /**
     * rotation * 90° (0-3)
     */
    private int rotation;

    @ManyToOne
    private Map map;

    @OneToOne(mappedBy = "mapObject")
    MapObjectPosition mapObjectPosition;

    public MapObject() {
    }


    public MapObject(long objectTypeId, int x, int y, int rotation) {
        this.objectTypeId = objectTypeId;
        this.x = x;
        this.y = y;
        this.rotation = rotation % 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapObject that = (MapObject) o;
        return id == that.id && version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}


