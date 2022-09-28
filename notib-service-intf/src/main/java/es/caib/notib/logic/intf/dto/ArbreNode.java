package es.caib.notib.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArbreNode<T> implements Serializable {

    public ArbreNode<T> pare;
    public T dades;
    public List<ArbreNode<T>> fills;
    public long count = 0;
    public boolean mostrarCount = false;
    public boolean filtresOk;
    public boolean retornatFiltre;

    public ArbreNode(ArbreNode<T> pare) {
        super();
        this.pare = pare;
    }
    public ArbreNode(ArbreNode<T> pare, T dades) {
        this(pare);
        setDades(dades);
    }

    /**
     * Retorna el pare del node actual.
     *
     * @return El pare del node actual.
     */
    public ArbreNode<T> getPare() {
        return pare;
    }
    /**
     * Retorna els fills del node actual.
     *
     * @return Els fills del node actual.
     */
    public List<ArbreNode<T>> getFills() {
        return this.fills != null ? fills : new ArrayList<ArbreNode<T>>();
    }
    /**
     * Estableix els fills del node actual.
     *
     * @param fills La llista de fills a establir.
     */
    public void setFills(List<ArbreNode<T>> fills) {
        this.fills = fills;
        this.count = fills != null ? fills.size() : 0;
    }
    /**
     * Retorna el nombre de fills per al node actual.
     *
     * @return El nombre de fills.
     */
    public int countFills() {
        return fills != null ? fills.size() : 0;
    }
    /**
     * Afegeix un fill al node actual.
     *
     * @param fill El fill per afegir.
     */
    public void addFill(ArbreNode<T> fill) {

        if (fills == null) {
            fills = new ArrayList<>();
        }
        fills.add(fill);
    }
    /**
     * Esborra un fill del node actual.
     *
     * @param fill El fill a esborrar.
     */
    public void removeFill(ArbreNode<T> fill) {

        if (fills != null) {
            fills.remove(fill);
        }
    }
    /**
     * Insereix un fill a un punt determinat.
     *
     * @param index El punt a on s'afegeix el fill.
     * @param fill El fill a afegir.
     * @throws IndexOutOfBoundsException Si el punt excedeix el tamany de la llista.
     */
    public void insertFillAt(int index, ArbreNode<T> fill) throws IndexOutOfBoundsException {

        if (index == countFills()) {
            // this is really an append
            addFill(fill);
            return;
        }
        fills.get(index); // just to throw the exception, and stop here
        fills.add(index, fill);
    }
    /**
     * Esborra un fill d'un punt determinat.
     *
     * @param index El punt d'on eliminar el fill.
     * @throws IndexOutOfBoundsException Si el punt excedeix
     *        el tamany de la llista.
     */
    public void removeFillAt(int index) throws IndexOutOfBoundsException {
        fills.remove(index);
    }
    /**
     * Obté les dades associades amb el node.
     *
     * @return Les dades.
     */
    public T getDades() {
        return this.dades;
    }
    /**
     * Estableix les dades per al node.
     *
     * @param dades Les dades.
     */
    public void setDades(T dades) {
        this.dades = dades;
    }

    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
        mostrarCount = true;
    }

    public void addCount(long count) {
        this.count += count;
        mostrarCount = true;
    }

    public int getNivell() {
        int nivell = 0;
        ArbreNode<T> nodeActual = this;
        while (nodeActual.getPare() != null) {
            nodeActual = nodeActual.getPare();
            nivell ++;
        }
        return nivell;
    }

    public boolean isMostrarCount() {
        return mostrarCount;
    }

    public ArbreNode<T> clone(ArbreNode<T> pare) {

        ArbreNode<T> clon = new ArbreNode<T>(pare, getDades());
        for (ArbreNode<T> fill: getFills()) {
            clon.addFill(fill.clone(clon));
        }
        return clon;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(getDades().toString()).append(",[");
        int i = 0;
        for (ArbreNode<T> e : getFills()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(e.getDades().toString());
            i++;
        }
        sb.append("]").append("}");
        return sb.toString();
    }
}
