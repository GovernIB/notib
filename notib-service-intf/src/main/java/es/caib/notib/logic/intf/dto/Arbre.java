package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Arbre<T> implements Serializable {

    private ArbreNode<T> arrel;
    private boolean ingnorarArrel;

    public Arbre(boolean ingnorarArrel) {
        super();
        this.ingnorarArrel = ingnorarArrel;
    }

    /**
     * Retorna el Node arrel de l'arbre.
     *
     * @return el node arrel.
     */
    public ArbreNode<T> getArrel() {
        return this.arrel;
    }
    /**
     * Estableix el Node arrel de l'arbre.
     *
     * @param arrel el node arrel a establir.
     */
    public void setArrel(ArbreNode<T> arrel) {
        this.arrel = arrel;
    }

    public boolean isIngnorarArrel() {
        return ingnorarArrel;
    }

    public void setIngnorarArrel(boolean ingnorarArrel) {
        this.ingnorarArrel = ingnorarArrel;
    }

    /**
     * Retorna l'arbre com una llista de objectes NodeDto<T>. Els elements de la
     * llista es generen recorreguent l'arbre en l'ordre pre-establert.
     *
     * @return una llista List<Node<T>>.
     */
    public List<ArbreNode<T>> toList() {

        List<ArbreNode<T>> list = new ArrayList<>();
        if (arrel != null) {
            recorregut(arrel, list);
        }
        if (ingnorarArrel && list.size() > 0) {
            list.remove(0);
        }
        return list;
    }

    /**
     * Retorna l'arbre com una llista de objectes NodeDto<T>. Els elements de la
     * llista es generen recorreguent l'arbre en l'ordre pre-establert.
     *
     * @return una llista List<Node<T>>.
     */
    public List<T> toDadesList() {
        List<T> list = new ArrayList<T>();
        if (arrel != null) {
            recorregutDades(arrel, list);
        }
        if (ingnorarArrel && list.size() > 0) {
            list.remove(0);
        }
        return list;
    }

    /**
     * Retorna una representació textual de l'arbre. Els elements es generen
     * recorreguent l'arbre en l'odre pre-establert.
     *
     * @return la representació textual de l'arbre.
     */
    public String toString() {
        return toList().toString();
    }

    /**
     * Clona l'arbre.
     *
     * @return un clon de l'arbre.
     */
    public Arbre<T> clone() {
        Arbre<T> clon = new Arbre<T>(ingnorarArrel);
        clon.setArrel(arrel.clone(null));
        return clon;
    }

    private void recorregut(ArbreNode<T> element, List<ArbreNode<T>> list) {
        list.add(element);
        for (ArbreNode<T> data : element.getFills()) {
            recorregut(data, list);
        }
    }

    private void recorregutDades(ArbreNode<T> element, List<T> list) {
        list.add(element.getDades());
        for (ArbreNode<T> data : element.getFills()) {
            recorregutDades(data, list);
        }
    }
}
