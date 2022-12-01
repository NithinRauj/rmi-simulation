public class RandomWeightedChoice {
    public int nextChoice(double[] items) {
        double completeWeight = 0.0;
        for (double item : items) {
            completeWeight += item;
        }
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (int i = 0; i < items.length; i++) {
            countWeight += items[i];
            if (countWeight >= r)
                return i;
        }
        return 0;
    }
}