public class logRecord {

    int i;
    int[] ts;
    updateRecord update;

    public logRecord(int i, int[] ts, updateRecord update) {
        this.i = i;
        this.ts = ts;
        this.update = update;
    }

    public int getI() { 
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int[] getTS() {
        return ts;
    }

    public void setTS(int[] ts) {
        this.ts = ts;
    }

    public updateRecord getUpdate() {
        return update;
    }

    public void setUpdate(updateRecord update) {
        this.update = update;
    }

}