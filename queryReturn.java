import java.io.Serializable;

public class queryReturn implements Serializable {

    public static final long serialVersionUID = 20120731125403L;

    int rating;
    int[] value_TS;
    boolean update;

    public queryReturn(int rating, int[] value_TS, boolean update) {
        this.rating = rating;
        this.value_TS = value_TS;
        this.update = update;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int[] getvalue_TS() {
        return value_TS;
    }

    public void setvalue_TS(int[] value_TS) {
        this.value_TS = value_TS;
    }

    public boolean getUpdate(){
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}