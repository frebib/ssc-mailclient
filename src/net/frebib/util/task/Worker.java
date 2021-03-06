package net.frebib.util.task;

public class Worker<E> {
    private boolean cancelled, complete;
    private Thread thread;
    private Task<E> task;
    private Completion<E> done;
    private Progress prog;
    private Throwable error;

    public Worker(Task<E> task) {
        this();
        todo(task);
    }
    public Worker(String name) {
        this();
        thread.setName(name);
    }
    public Worker() {
        thread = new Thread(() -> {
            try {
                task.todo(done, prog);
                if (!cancelled && done != null)
                    done.onComplete(null);
                complete = true;
            } catch (Exception ex) {
                if (error != null)
                    error.onError(ex);
                else {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }

    public Worker<E> todo(Task<E> task) {
        this.task = task;
        return this;
    }
    public Worker<E> done(Completion<E> done) {
        this.done = done;
        return this;
    }

    public Worker<E> error(Throwable error) {
        this.error = error;
        return this;
    }

    public Worker<E> progress(Progress prog) {
        this.prog = prog;
        return this;
    }

    public Worker<E> start() {
        thread.start();
        return this;
    }

    public boolean isComplete() {
        return complete;
    }

    public void cancel() {
        cancelled = true;
    }
}
