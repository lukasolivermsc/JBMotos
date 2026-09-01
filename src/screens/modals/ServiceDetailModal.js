import React, { useState, useMemo } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { appColors } from '../../theme';
import { Picker } from '@react-native-picker/picker';
import { Calendar } from 'react-native-calendars';
import { useSession } from '../../context/SessionContext';
import { getMotorcyclesByUsername } from '../../database/motorcycleDao';
import { insertSchedule, getUnavailableDateTimes, formatPrice } from '../../database/serviceDao';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback } from 'react';

// Horários das 8:00 às 18:30, sem 12:xx, de 30 em 30 min
function generateTimeSlots() {
  const slots = [];
  for (let h = 8; h < 19; h++) {
    for (let m = 0; m < 60; m += 30) {
      if (h === 12) continue;
      if (h === 18 && m === 30) break;
      slots.push(`${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`);
    }
  }
  return slots;
}

const TIME_SLOTS = generateTimeSlots();
const LAST_SLOT = TIME_SLOTS[TIME_SLOTS.length - 1]; // '18:00'

function toDateKey(date) {
  // Retorna 'yyyy-mm-dd' no fuso local, evitando offset UTC
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

// Constrói o objeto markedDates para o Calendar:
// - fins de semana e hoje (se já passou do último slot) → disabled + esmaecido
// - cobre os próximos 3 meses
function buildMarkedDates(unavailable) {
  const marked = {};
  const now = new Date();
  const lastSlotHour = parseInt(LAST_SLOT.split(':')[0], 10);
  const lastSlotMin = parseInt(LAST_SLOT.split(':')[1], 10);

  const todayPastLastSlot =
    now.getHours() > lastSlotHour ||
    (now.getHours() === lastSlotHour && now.getMinutes() >= lastSlotMin);

  // Máximo: 3 meses a partir de hoje
  const maxDate = new Date(now);
  maxDate.setMonth(maxDate.getMonth() + 3);

  const cursor = new Date(now);
  cursor.setHours(0, 0, 0, 0);
  let dayIndex = 0;

  while (cursor <= maxDate) {
    const key = toDateKey(cursor);
    const dow = cursor.getDay();
    const isWeekend = dow === 0 || dow === 6;
    const isTodayBlocked = dayIndex === 0 && todayPastLastSlot;

    if (isWeekend || isTodayBlocked) {
      marked[key] = { disabled: true, disabledTextColor: appColors.primaryDark };
    }

    cursor.setDate(cursor.getDate() + 1);
    dayIndex++;
  }

  return marked;
}

export default function ServiceDetailModal({ navigation, route }) {
  const { service, onScheduled } = route.params;
  const { user } = useSession();
  const [motorcycles, setMotorcycles] = useState([]);
  const [selectedMotorcycleId, setSelectedMotorcycleId] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null); // string 'yyyy-mm-dd'
  const [selectedTime, setSelectedTime] = useState(null);
  const [unavailable, setUnavailable] = useState({});

  const now = new Date();
  const todayKey = toDateKey(now);
  const maxDateKey = toDateKey(new Date(now.getFullYear(), now.getMonth() + 3, now.getDate()));

  useFocusEffect(useCallback(() => {
    (async () => {
      const list = await getMotorcyclesByUsername(user?.name);
      setMotorcycles(list);
      if (list.length > 0) setSelectedMotorcycleId(list[0].id);
      const u = await getUnavailableDateTimes();
      setUnavailable(u);
    })();
  }, [user]));

  // Reconstrói markedDates sempre que unavailable ou selectedDate mudarem
  const markedDates = useMemo(() => {
    const base = buildMarkedDates(unavailable);

    // Destaca o dia selecionado
    if (selectedDate) {
      base[selectedDate] = {
        ...base[selectedDate],
        selected: true,
        selectedColor: appColors.secondary, // laranja — contrasta bem no fundo escuro
      };
    }

    return base;
  }, [unavailable, selectedDate]);

  // Ao trocar de data, limpa o horário selecionado
  function handleDayPress(day) {
    setSelectedDate(day.dateString);
    setSelectedTime(null);
  }

  function isSlotUnavailable(slot) {
    if (!selectedDate) return true;
    const intervals = unavailable[selectedDate] ?? [];

    const [h, m] = slot.split(':').map(Number);
    const slotStart = new Date(selectedDate + 'T00:00:00');
    slotStart.setHours(h, m, 0, 0);
    const slotEnd = new Date(slotStart.getTime() + service.duration * 60 * 1000);

    // Se for hoje, bloqueia horários já passados
    if (selectedDate === todayKey && slotStart <= now) return true;

    return intervals.some(({ start, end }) => slotStart < end && slotEnd > start);
  }

  async function handleConfirm() {
    if (!selectedTime || !selectedDate || !selectedMotorcycleId) return;
    const [h, m] = selectedTime.split(':').map(Number);
    const dateTime = new Date(selectedDate + 'T00:00:00');
    dateTime.setHours(h, m, 0, 0);
    await insertSchedule({ serviceId: service.id, motorcycleId: selectedMotorcycleId, dateTime });
    // Avisa a HomeScreen para atualizar os agendamentos do card
    onScheduled?.();
    navigation.goBack();
  }

  const canConfirm = selectedTime && selectedDate && selectedMotorcycleId;

  return (
    <View style={styles.overlay}>
      <View style={styles.card}>
        <TouchableOpacity style={styles.closeButton} onPress={() => navigation.goBack()}>
          <Text style={styles.closeText}>✕</Text>
        </TouchableOpacity>
        <ScrollView showsVerticalScrollIndicator={false}>
          <Text style={styles.serviceName}>{service.name}</Text>
          <Text style={styles.servicePrice}>{formatPrice(service.price)}</Text>
          {!!service.description && <Text style={styles.serviceDesc}>{service.description}</Text>}
          <Text style={styles.serviceDuration}>Duração: {service.duration} min</Text>

          {/* Seletor de moto — estilo padrão do sistema */}
          <Text style={styles.label}>Minha moto</Text>
          <View style={styles.pickerWrapper}>
            <Picker
              selectedValue={selectedMotorcycleId}
              onValueChange={setSelectedMotorcycleId}
              style={{ color: appColors.textPrimary }}
              dropdownIconColor={appColors.textPrimary}
            >
              {motorcycles.map(m => (
                <Picker.Item
                  key={m.id}
                  label={`${m.brand} - ${m.name}`}
                  value={m.id}
                />
              ))}
            </Picker>
          </View>

          {/* Calendário — fins de semana e dias bloqueados aparecem esmaecidos e não clicáveis */}
          <Text style={styles.label}>Data</Text>
          <View style={styles.calendarWrapper}>
            <Calendar
              onDayPress={handleDayPress}
              markedDates={markedDates}
              minDate={todayKey}
              maxDate={maxDateKey}
              disableAllTouchEventsForDisabledDays={true}
              style={styles.calendar}
              theme={{
                backgroundColor: appColors.background,
                calendarBackground: appColors.background,
                textSectionTitleColor: appColors.textSecondary,  // cabeçalho dias da semana
                selectedDayBackgroundColor: appColors.secondary, // dia selecionado — laranja
                selectedDayTextColor: appColors.textLight,
                todayTextColor: appColors.secondary,             // hoje destacado em laranja
                dayTextColor: appColors.textPrimary,             // dias normais — cinza claro
                textDisabledColor: appColors.primaryDark,        // fins de semana — bem escuro, quase some
                arrowColor: appColors.secondary,
                monthTextColor: appColors.textPrimary,
                textMonthFontWeight: 'bold',
                dotColor: appColors.secondary,
              }}
            />
          </View>

          {/* Horários — só aparecem após escolher uma data */}
          {selectedDate && (
            <>
              <Text style={styles.label}>Horário</Text>
              <View style={styles.timeGrid}>
                {TIME_SLOTS.map(slot => {
                  const blocked = isSlotUnavailable(slot);
                  const selected = selectedTime === slot;
                  return (
                    <TouchableOpacity
                      key={slot}
                      disabled={blocked}
                      style={[
                        styles.timeButton,
                        selected && styles.timeButtonSelected,
                        blocked && styles.timeButtonBlocked,
                      ]}
                      onPress={() => setSelectedTime(prev => prev === slot ? null : slot)}
                    >
                      <Text style={[
                        styles.timeButtonText,
                        selected && styles.timeButtonTextSelected,
                        blocked && styles.timeButtonTextBlocked,
                      ]}>
                        {slot}
                      </Text>
                    </TouchableOpacity>
                  );
                })}
              </View>
            </>
          )}

          <TouchableOpacity
            style={[styles.confirmButton, !canConfirm && styles.confirmButtonDisabled]}
            onPress={handleConfirm}
            disabled={!canConfirm}
          >
            <Text style={styles.confirmButtonText}>Confirmar agendamento</Text>
          </TouchableOpacity>
        </ScrollView>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: appColors.overlay, justifyContent: 'flex-end' },
  card: { backgroundColor: appColors.card, borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 24, maxHeight: '90%' },
  closeButton: { position: 'absolute', top: 12, right: 16, padding: 4, zIndex: 1 },
  closeText: { fontSize: 18, color: appColors.textSecondary },

  serviceName: { fontSize: 22, fontWeight: 'bold', color: appColors.textPrimary, marginTop: 8 },
  servicePrice: { fontSize: 18, color: appColors.secondary, marginTop: 4 },
  serviceDesc: { fontSize: 14, color: appColors.textMuted, marginTop: 8 },
  serviceDuration: { fontSize: 13, color: appColors.textMuted, marginTop: 4, marginBottom: 16 },

  label: { fontSize: 13, fontWeight: '600', color: appColors.textSecondary, marginTop: 16, marginBottom: 6 },

  calendarWrapper: { borderRadius: 10, overflow: 'hidden', borderWidth: 1, borderColor: appColors.primaryDark, backgroundColor: appColors.background },
  calendar: { backgroundColor: appColors.background },

  pickerWrapper: { borderWidth: 1, borderColor: appColors.primaryDark, borderRadius: 8, overflow: 'hidden' },

  timeGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },

  timeButton: { borderWidth: 1, borderColor: appColors.textLight, borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  timeButtonText: { color: appColors.textLight, fontSize: 13 },
  timeButtonSelected: { backgroundColor: appColors.secondary, borderColor: appColors.secondary },
  timeButtonTextSelected: { color: appColors.textLight },
  timeButtonBlocked: { borderColor: appColors.primaryDark },
  timeButtonTextBlocked: { color: appColors.primaryDark },

  confirmButton: { backgroundColor: appColors.secondary, borderRadius: 10, paddingVertical: 14, alignItems: 'center', marginTop: 20, marginBottom: 8 },
  confirmButtonDisabled: { backgroundColor: appColors.primaryDark },
  confirmButtonText: { color: appColors.textLight, fontWeight: 'bold', fontSize: 16 },
});