public class updateRequest {

    String movie;
    int rating;
    int[] prev;
    int updateID;

    public updateRequest(String movie, int rating, int[] prev, int updateID) {
        this.movie = movie;
        this.rating = rating;
        this.prev = prev;
        this.updateID = updateID;
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