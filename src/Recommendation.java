public class Recommendation {

    private int rating;
    // rating 0-10
    //TODO napisać metodę, która weryfikuje, czy użytkownik podał dobre dane
    private String description;
    private String authorOfRecommendation;

    public Recommendation(int rating, String description, String authorOfRecommendation) {
        this.rating = rating;
        this.description = description;
        this.authorOfRecommendation = authorOfRecommendation;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorOfRecommendation() {
        return authorOfRecommendation;
    }

    public void setAuthorOfRecommendation(String authorOfRecommendation) {
        this.authorOfRecommendation = authorOfRecommendation;
    }
}
