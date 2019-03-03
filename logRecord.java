public class logRecord {

    int i;
    int[] ts;
    String movie;
    int rating;
    int[] prev;
    int updateID;

    public logRecord(int i, int[] ts, String movie, int rating, int[] prev, int updateID) {
        this.i = i;
        this.ts = ts;
        this.movie = movie;
        this.rating = rating;
        this.prev = prev;
        this.updateID = updateID;
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

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int[] getPrev() {
        return prev;
    }

    public void setPrev(int[] prev) {
        this.prev = prev;
    }

    public int getupdateID() {
        return updateID;
    }

    public void setupdateID(int updateID) {
        this.updateID = updateID;   
    }


}