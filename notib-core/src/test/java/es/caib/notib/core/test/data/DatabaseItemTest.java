package es.caib.notib.core.test.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DatabaseItemTest<T> {
    @Getter
    protected Map<String, T> objects = new HashMap<>();
    @Getter
    protected Map<String, List<RelatedObject>> relatedObjects = new HashMap<>();

    protected Map<String, Boolean> createdObjects = new HashMap<>();

    public abstract T create (T element, Long entitatId) throws Exception;
    public abstract void delete(Long entitatId, T object) throws Exception;
    public abstract T getRandomInstance();

    public void addRelated(String key, String elementKey, DatabaseItemTest<?> container){
        if(!relatedObjects.containsKey(key)) {
            this.relatedObjects.put(key, new ArrayList<RelatedObject>());
        }
        this.relatedObjects.get(key).add(new RelatedObject(elementKey, container));
    };

    public T getObject(String key) {
        return this.objects.get(key);
    }

    public void relateElements() throws Exception {
        for (String objectIdentifier : this.relatedObjects.keySet()) {
            for (RelatedObject related : relatedObjects.get(objectIdentifier)) {
                this.relateElement(objectIdentifier, related.container.getObject(related.objectIdentifier));
            }
        }
    }

    public void relateElement(String key, Object element) throws Exception{
    }

    public void addObject(String identifier, T object) {
        this.objects.put(identifier, object);
        this.createdObjects.put(identifier, false);
    }

    public void createAll(Long entitatId) throws Exception {
        for(String key : objects.keySet()) {
            if (!this.createdObjects.get(key)) {
                T createdObject = this.create(this.objects.get(key), entitatId);
                objects.put(key, createdObject);
                this.createdObjects.put(key, true);
            }
        }
    }

    public void deleteAll(Long entitatId)  throws Exception{
        for(String key : objects.keySet()) {
            this.delete(entitatId, key);
        }
    }

    public void delete(Long entitatId, String key) throws Exception {
        if (this.createdObjects.get(key)) {
            this.delete(entitatId, this.objects.get(key));
        }
    }

    public T create(T element) throws Exception {
        return create(element, null);
    }

    @AllArgsConstructor
    class RelatedObject {
        String objectIdentifier;
        DatabaseItemTest<?> container;
    }
}
