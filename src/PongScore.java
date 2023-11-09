public class PongScore
{
    private int points;
    private String initials;

    public PongScore(int points, String initials)
    {
        this.points = points;
        this.initials = initials;
    }

    public int getPoints() {
        return points;
    }

    public String getInitials() {
        return initials;
    }
}