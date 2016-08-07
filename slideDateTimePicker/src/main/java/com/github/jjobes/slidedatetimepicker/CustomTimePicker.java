package com.github.jjobes.slidedatetimepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

import timber.log.Timber;

/**
 * A subclass of {@link android.widget.TimePicker} that uses
 * reflection to allow for customization of the default blue
 * dividers.
 *
 * @author jjobes
 *
 */
public class CustomTimePicker extends TimePicker
{
//    private static final String TAG = "CustomTimePicker";

    public CustomTimePicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Class<?> idClass = null;
        Class<?> numberPickerClass = null;
        Field selectionDividerField = null;
        Field hourField = null;
        Field minuteField = null;
        Field amPmField = null;
        NumberPicker hourNumberPicker = null;
        NumberPicker minuteNumberPicker = null;
        NumberPicker amPmNumberPicker = null;

        try
        {
            // Create an instance of the id class
            idClass = Class.forName("com.android.internal.R$id");

            // Get the fields that store the resource IDs for the hour, minute and amPm NumberPickers
            hourField = idClass.getField("hour");
            minuteField = idClass.getField("minute");
            amPmField = idClass.getField("amPm");

            // Use the resource IDs to get references to the hour, minute and amPm NumberPickers
            hourNumberPicker = (NumberPicker) findViewById(hourField.getInt(null));
            minuteNumberPicker = (NumberPicker) findViewById(minuteField.getInt(null));
            amPmNumberPicker = (NumberPicker) findViewById(amPmField.getInt(null));

            numberPickerClass = Class.forName("android.widget.NumberPicker");

            // Set the value of the mSelectionDivider field in the hour, minute and amPm NumberPickers
            // to refer to our custom drawables
            selectionDividerField = numberPickerClass.getDeclaredField("mSelectionDivider");
            selectionDividerField.setAccessible(true);
            selectionDividerField.set(hourNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
            selectionDividerField.set(minuteNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
            selectionDividerField.set(amPmNumberPicker, getResources().getDrawable(R.drawable.selection_divider));
        }
        catch (ClassNotFoundException e)
        {
            Timber.e("%s ClassNotFoundException in CustomTimePicker", e);
        }
        catch (NoSuchFieldException e)
        {
            Timber.e("%s NoSuchFieldException in CustomTimePicker", e);
        }
        catch (IllegalAccessException e)
        {
            Timber.e("%s IllegalAccessException in CustomTimePicker", e);
        }
        catch (IllegalArgumentException e)
        {
            Timber.d("%s IllegalArgumentException in CustomTimePicker", e);
        }
    }
}
