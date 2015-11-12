package net.frebib.util.task;

public interface Task<E> {
    E get() throws Exception;
}
