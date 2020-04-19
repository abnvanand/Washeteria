package github.abnvanand.washeteria.ui.assistant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import github.abnvanand.washeteria.R;

public class AssistantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        Slider rangeSlider = findViewById(R.id.range_slider);
        rangeSlider.setValues(2f, 7f);

    }
}
