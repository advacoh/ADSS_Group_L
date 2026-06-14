package domain.hr;

public class SlotSubmission {
    private boolean constraint; // hard block — HR must respect
    private boolean preference; // soft — HR should consider

    // Constructor
    public SlotSubmission() {
        this.constraint = true;
        this.preference = true;
    }

    // Getters
    public boolean isConstraint() {
        return constraint;
    }

    public boolean isPreference() {
        return preference;
    }

    // Setters
    public void setConstraint(boolean constraint) {
        this.constraint = constraint;
    }

    public void setPreference(boolean preference) {
        this.preference = preference;
    }
}