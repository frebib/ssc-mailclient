package net.frebib.util.task;

public class Worker<E> {
    private boolean cancelled, complete;
    private Thread thread;
    private Task<E> task;
    private DoTask dotask;
    private Completion<E> done;
    private Progress prog;
    private Throwable error;

    public Worker(Task<E> task) {
        this();
        get(task);
    }
    public Worker() {
        thread = new Thread(() -> {
            try {
                E e = null;
                if (task != null)
                    e = task.get();
                else
                    dotask.job();
                if (!cancelled && done != null)
                    done.onComplete(e);
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

    public Worker<E> get(Task<E> task) {
        this.task = task;
        return this;
    }
    public Worker<E> todo(DoTask task) {
        this.dotask = task;
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

    public void setProgress(int progress) {
        if (prog != null)
            prog.onProgress(progress);
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
