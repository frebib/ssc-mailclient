package net.frebib.util.task;

public interface Task<E> {
    void todo(Completion<E> done, Progress prog) throws Exception;
}
