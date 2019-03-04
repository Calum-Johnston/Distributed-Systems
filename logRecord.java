import java.io.Serializable;

public class logRecord implements Serializable{

    public static final long serialVersionUID = 20120731125401L;

    int i;
    int[] ts;
    updateRequest update;

    public logRecord(int i, int[] ts, updateRequest update) {
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

    public updateRequest getUpdate() {
        return update;
    }

    public void setUpdate(updateRequest update) {
        this.update = update;
    }

}