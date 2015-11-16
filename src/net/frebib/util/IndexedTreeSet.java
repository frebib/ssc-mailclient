package net.frebib.util;

import java.util.Comparator;
import java.util.TreeSet;

public class IndexedTreeSet<E> extends TreeSet<E> {

    public IndexedTreeSet(Comparator<? super E> comparator) {
        super(comparator);
    }

    public int indexOf(E e) {
        return contains(e) ? headSet(e).size() : -1;
    }

    public E get(int i) {
        for(E e : this)
            if(i-- <= 0)
                return e;
        return null;
    }
}
