package SP;


import java.util.ArrayList;
import java.util.List;

public class SearchOutput {
    public List<QueryVO_data> round_VOs = new ArrayList<>();
    public List<Long> result = new ArrayList<>();

    @Override
    public String toString() {
        return "round_VOs=" + round_VOs +
                ", result=" + result +
                '}';
    }
}
