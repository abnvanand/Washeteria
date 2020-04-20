package github.abnvanand.washeteria.ui.assistant;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import github.abnvanand.washeteria.utils.Constants;
import timber.log.Timber;

public class Filter extends BaseObservable {
    // weekdays
    private boolean mon;
    private boolean tue;
    private boolean wed;
    private boolean thu;
    private boolean fri;
    // weekends
    private boolean sat;
    private boolean sun;

    //
    private boolean allDays;
    private boolean weekdays;
    private boolean weekends;

    private float sliderValueFrom;
    private float sliderValueTo;
    private float[] sliderValues;

    public Filter() {
        allDays = mon = tue = wed = thu = fri = sat = sun = true;
        weekdays = false;
        weekends = false;

        sliderValueFrom = Constants.MIN_DURATION;
        sliderValueTo = Constants.MIN_DURATION;
        sliderValues = new float[2];
        sliderValues[0] = sliderValueFrom;
        sliderValues[1] = sliderValueTo;


    }

    @Bindable
    public boolean isWeekdays() {
        return weekdays;
    }

    public void setWeekdays(boolean weekdays) {
        this.weekdays = weekdays;
    }

    @Bindable
    public boolean isWeekends() {
        return weekends;
    }

    public void setWeekends(boolean weekends) {
        Timber.d("Here");
        if (this.weekends != weekends) {
            this.weekends = weekends;
        }

        if (this.weekends) {
            this.mon = this.tue = this.wed = this.thu = this.fri = false;
            this.sat = this.sun = true;
        }
    }

    @Bindable
    public boolean isMon() {
        return mon;
    }

    @Bindable
    public boolean isAllDays() {
        return allDays;
    }

    public void setAllDays(boolean allDays) {
        if (this.allDays != allDays) {
            this.allDays = allDays;
        }

        if (this.allDays) {
            this.mon = this.tue = this.wed = this.thu = this.fri = this.sat = this.sun = true;
        }
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    @Bindable
    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    @Bindable
    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    @Bindable
    public boolean isThu() {
        return thu;
    }

    public void setThu(boolean thu) {
        this.thu = thu;
    }

    @Bindable
    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    @Bindable
    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    @Bindable
    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    @Bindable
    public float getSliderValueFrom() {
        return sliderValueFrom;
    }

    public void setSliderValueFrom(float sliderValueFrom) {
        this.sliderValueFrom = sliderValueFrom;
    }

    @Bindable
    public float getSliderValueTo() {
        return sliderValueTo;
    }

    public void setSliderValueTo(float sliderValueTo) {
        this.sliderValueTo = sliderValueTo;
    }


    public float[] getSliderValues() {
        return sliderValues;
    }

    public void setSliderValues(float[] sliderValues) {
        this.sliderValues = sliderValues;
    }
}
