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
    public Worker() {
        thread = new Thread(() -> {
            try {
                E e = task.job();
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

    public Worker<E> todo(Task<E> task) {
        this.task = task;
        return this;
    }
    public Worker<E> onComplete(Completion<E> done) {
        this.done = done;
        return this;
    }

    public Worker<E> onError(Throwable error) {
        this.error = error;
        return this;
    }

    public Worker<E> onProgress(Progress prog) {
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
