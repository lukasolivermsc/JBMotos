package com.jbmotos.app.main.service;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.jbmotos.app.R;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.session.SessionManager;
import com.jbmotos.app.session.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ServiceOverlayActivity extends AppCompatActivity {

    private final List<Motorcycle> motorcycleList;
    private Motorcycle selectedMotorcycle;
    private Service referencedService;
    private final List<MaterialButton> timeButtons = new ArrayList<>();
    private MaterialButton selectedHour;
    private Calendar selectedDay;
    private CalendarView calendarView;
    private static final String timeZoneTag = "America/Sao_Paulo";
    private final ServicesScheduleManager servicesScheduleManager = ServicesScheduleManager.getInstance();
    private Pair<Calendar, Calendar> minAndMaxDates;

    public ServiceOverlayActivity() {
        User user = SessionManager.getInstance(getBaseContext()).getUser();
        this.motorcycleList = user == null ? new ArrayList<>() : user.getMotorcycles();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        referencedService = getIntent().getParcelableExtra("SERVICE");

        if (motorcycleList.isEmpty() || referencedService == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_overlay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Selecionador de motos
        Spinner motorcycleSelect = findViewById(R.id.motorcycleSelect);
        ArrayAdapter<Motorcycle> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                motorcycleList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        motorcycleSelect.setAdapter(adapter);

        //Botão confirmar
        Button confirmButton = findViewById(R.id.btnConfirm);
        confirmButton.setOnClickListener(v -> {
            Motorcycle selectedMotorcycle = motorcycleList.get(motorcycleSelect.getSelectedItemPosition());

            if (selectedHour != null && selectedDay != null) {
                final String selectedHourString = selectedHour.getText().toString();
                final LocalDate date = selectedDay.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                ServiceScheduled serviceScheduled = new ServiceScheduled(referencedService,
                        LocalDateTime.of(date, LocalTime.parse(selectedHourString, DateTimeFormatter.ofPattern("HH:mm"))));
                if (selectedMotorcycle.scheduleService(serviceScheduled)) {
                    SessionManager.getInstance(getApplicationContext()).saveUser();
                    servicesScheduleManager.scheduleService(serviceScheduled);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        //Botão fechar
        ImageButton closeButton = findViewById(R.id.btnClose);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });



        /** ---------- Visualizadores: Calendário e Horários ---------- */

        calendarView = findViewById(R.id.calendarPicker);
        initCalendarViewDefaultLimits();

        //Inicializar dia de hoje
        selectedDay = minAndMaxDates.first;
        while (selectedDay.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                selectedDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            selectedDay.add(Calendar.DATE, 1);
        }
        try {
            calendarView.setDate(selectedDay);
        } catch (OutOfDateRangeException e) {
            throw new RuntimeException(e);
        }

        //Remover fins de semana
        List<Calendar> disabledDays = new ArrayList<>();
        Calendar currentDayHelper = (Calendar) minAndMaxDates.first.clone();
        while (!currentDayHelper.after(minAndMaxDates.second)) {
            int dayOfWeek = currentDayHelper.get(Calendar.DAY_OF_WEEK);

            switch (dayOfWeek) {
                case Calendar.SATURDAY:
                    disabledDays.add((Calendar) currentDayHelper.clone());
                    currentDayHelper.add(Calendar.DATE, 1);
                    break;
                case Calendar.SUNDAY:
                    disabledDays.add((Calendar) currentDayHelper.clone());
                    currentDayHelper.add(Calendar.DATE, 6);
                    break;
                default:
                    currentDayHelper.add(Calendar.DATE, 1);
                    break;
            }
        }
        calendarView.setDisabledDays(disabledDays);

        calendarView.setOnCalendarDayClickListener(day -> {
            if (day.getCalendar().compareTo(minAndMaxDates.first) >= 0
                    && day.getCalendar().compareTo(minAndMaxDates.second) <= 0
                    && day.getCalendar().get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                    && day.getCalendar().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            ) {
                if (selectedDay.get(Calendar.MONTH) != day.getCalendar().get(Calendar.MONTH)) {
                    selectedDay = day.getCalendar();
                    try {
                        calendarView.setDate(selectedDay);
                    } catch (OutOfDateRangeException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    selectedDay = day.getCalendar();
                }
                updateAvailableHours();
            } else {
                try {
                    calendarView.setDate(selectedDay);
                } catch (OutOfDateRangeException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.d("SelectedDay", selectedDay.getTime().toString());
        });

        //Configurar horas disponíveis
        Calendar currentTimeForLoop = Calendar.getInstance();
        currentTimeForLoop.set(Calendar.HOUR_OF_DAY, 8);
        currentTimeForLoop.set(Calendar.MINUTE, 30);

        FlexboxLayout hourFlexbox = findViewById(R.id.timeFlexbox);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        //Criar botões de horários das 8:30 até as 18:30
        while (currentTimeForLoop.get(Calendar.HOUR_OF_DAY) < 19) {

            //Não criar botões de horários entre 12:00 e 12:59
            if ((currentTimeForLoop.get(Calendar.HOUR_OF_DAY) != 12)) {

                String timeText = formatter.format(currentTimeForLoop.getTime());
                MaterialButton button = new MaterialButton(this, null,
                        com.google.android.material.R.attr.materialButtonOutlinedStyle);
                button.setText(timeText);
                button.setId(View.generateViewId());
                button.setCheckable(true);
                button.setEllipsize(null);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setPadding(0, 0, 0, 0);

                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(width, height);
                params.setMargins(5, 5, 5, 5);
                button.setLayoutParams(params);
                button.setMinWidth(width);
                button.setMinHeight(height);

                hourFlexbox.addView(button);
                timeButtons.add(button);

                //Seleção de horário
                button.setOnClickListener(v -> {
                    if (button == selectedHour) {
                        button.setChecked(false);
                        selectedHour = null;
                        return;
                    }
                    for (MaterialButton b : timeButtons) {
                        b.setChecked(false);
                    }
                    button.setChecked(true);
                    selectedHour = button;
                });
            }
            currentTimeForLoop.add(Calendar.MINUTE, 30);
        }

        //Selecionar moto
        motorcycleSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMotorcycle = motorcycleList.get(position);

                //Se a moto já tiver agendado esse serviço, desabilitar botões, se não, rehabilitá-los
                if (selectedMotorcycle.isServiceScheduled(referencedService)) {
                    setCalendarViewUnselectable();

                    if (selectedHour != null) {
                        selectedHour.setChecked(false);
                        selectedHour = null;
                    }
                    for (MaterialButton button : timeButtons) {
                        button.setEnabled(false);
                    }
                    confirmButton.setEnabled(false);
                }
                else {
                    updateAvailableHours();
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMotorcycle = null;
            }
        });
    }

    private void updateAvailableHours() {
        Calendar helperCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneTag));

        //Setando o mínimo horário disponível para serviços se hoje estiver selecionado
        if (selectedDay.get(Calendar.DAY_OF_YEAR) == helperCalendar.get(Calendar.DAY_OF_YEAR)) {

            LocalTime maxTime = LocalTime.of(helperCalendar.get(Calendar.HOUR_OF_DAY), helperCalendar.get(Calendar.MINUTE));
            maxTime.plusHours(1);
            for (MaterialButton timeButton : timeButtons) {
                LocalTime buttonTime = LocalTime.parse(timeButton.getText().toString(), DateTimeFormatter.ofPattern("HH:mm"));
                if (buttonTime.isAfter(maxTime)) {
                    break;
                } else {
                    timeButton.setEnabled(false);
                }
            }
        }
        else {
            for (MaterialButton timeButton : timeButtons) {
                timeButton.setEnabled(true);
            }
        }

        Map<LocalDate, List<Pair<LocalTime, LocalTime>>> unavailableMap = servicesScheduleManager.getUnavailableDateTimes();
        List<Pair<LocalTime, LocalTime>> unavailableList = unavailableMap.get(
                getLocalDateTimeFromCalendar(selectedDay).toLocalDate()
        );
        if (unavailableList != null) {
            for (Pair<LocalTime, LocalTime> unavailableInterval : unavailableList) {
                for (MaterialButton timeButton : timeButtons) {
                    LocalTime buttonTime = LocalTime.parse(timeButton.getText().toString(), DateTimeFormatter.ofPattern("HH:mm"));
                    if (!buttonTime.isBefore(unavailableInterval.first.plusMinutes(-referencedService.getDuration()))
                            && !buttonTime.isAfter(unavailableInterval.second)) {
                        timeButton.setEnabled(false);
                    }
                }
            }
        }
    }

    private void initCalendarViewDefaultLimits() {
        Calendar minDate = Calendar.getInstance(TimeZone.getTimeZone(timeZoneTag));
        LocalDateTime minLocalDateTime = getLocalDateTimeFromCalendar(minDate);
        LocalTime limitTime = LocalTime.of(18, 30);
        LocalDateTime endDateTime = minLocalDateTime.plusMinutes(referencedService.getDuration());
        Calendar maxDate = (Calendar) minDate.clone();

        // Se o serviço durar mais que o limitTime, ir para o dia seguinte
        if (endDateTime.toLocalTime().isAfter(limitTime)) {
            minDate.set(Calendar.HOUR, 0);
            minDate.set(Calendar.MINUTE, 0);
            minDate.set(Calendar.SECOND, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            minDate.add(Calendar.DATE, 1);
            maxDate.set(Calendar.HOUR, 0);
            maxDate.set(Calendar.MINUTE, 0);
            maxDate.set(Calendar.SECOND, 0);
            maxDate.set(Calendar.MILLISECOND, 0);
            maxDate.add(Calendar.DATE, 1);
        }
        maxDate.add(Calendar.MONTH, 6);
        calendarView.setMinimumDate(minDate);
        calendarView.setMaximumDate(maxDate);
        
        minAndMaxDates = Pair.create(minDate, maxDate);
    }

    private void setCalendarViewUnselectable() {
        Calendar minDate = Calendar.getInstance(TimeZone.getTimeZone(timeZoneTag));
        Calendar maxDate = (Calendar) minDate.clone();

        calendarView.setMinimumDate(minDate);
        maxDate.add(Calendar.DATE, -1);
        calendarView.setMaximumDate(maxDate);
    }

    private static LocalDateTime getLocalDateTimeFromCalendar(@NonNull Calendar calendar) {
        return LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND) * 1_000_000);
    }
}