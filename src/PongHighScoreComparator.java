import java.util.Comparator;

public class PongHighScoreComparator implements Comparator<PongScore>
{
    @Override
    public int compare(PongScore o1, PongScore o2)
    {
        return Integer.compare(o2.getPoints(), o1.getPoints());
    }
}
