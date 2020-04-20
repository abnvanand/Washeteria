package github.abnvanand.washeteria.ui.assistant;

public enum Prahar {
    MORNING(6, 10), // 6am-10am
    AFTERNOON(10, 16),//10am-4pm
    EVENING(16, 20),//4pm-8pm
    NIGHT(20, 24);//8pm-12am

    private int startHour, endHour;

    Prahar(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
}
