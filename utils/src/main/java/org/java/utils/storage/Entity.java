package org.java.utils.storage;

/**
 * Created by msamoylych on 04.04.2017.
 */
public class Entity {

    private Long id;

    public Long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return id != null && id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id:" + id + ']';
    }
}
