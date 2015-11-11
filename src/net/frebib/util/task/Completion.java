package net.frebib.util.task;

public interface Completion<E> {
    void onComplete(E e);
}
