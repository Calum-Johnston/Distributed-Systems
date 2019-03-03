public class queryRequest {

    String movie;
    int[] prev;

    public queryRequest(String movie, int[] prev) {
        this.movie = movie;
        this.prev = prev;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public int[] getPrev() {
        return prev;
    }

    public void setPrev(int[] prev) {
        this.prev = prev;
    }
}