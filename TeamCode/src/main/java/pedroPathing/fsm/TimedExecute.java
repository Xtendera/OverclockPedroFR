package pedroPathing.fsm;

public class TimedExecute implements Execute {
    private boolean isDone = true;
    private long endTime = 0;
    private double time;

    private Execute exec;

    public TimedExecute(long time, Execute execute) {
        this.time = time;
        this.exec = execute;
    }

    public void start() {
        if (isDone) isDone = false;
        endTime = (long) (System.nanoTime() + (time * 1_000_000));
    }

    @Override
    public void execute() {
        if (!isDone && System.nanoTime() >= endTime) {
            isDone = true;
            exec.execute();
        }
    }
}
